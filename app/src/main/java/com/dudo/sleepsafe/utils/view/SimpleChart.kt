package com.dudo.sleepsafe.utils.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.hardware.SensorManager
import android.util.AttributeSet
import android.view.View
import com.dudo.sleepsafe.utils.dpToPx

class SimpleChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var step = 0f
    private var center = 0f
    private var dataSet = mutableListOf<Float>()

    fun update(dataSet: MutableList<Float>) {
        this.dataSet = dataSet
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (dataSet.isNotEmpty()) {
            if(dataSet.size != 60){
                for (i in 0..(60 - dataSet.size)) {
                    dataSet.add(0f)
                }
            }
            val paint = Paint()
            paint.color = Color.WHITE
            paint.isAntiAlias = true
            paint.strokeWidth = context.dpToPx(3f)
            if (step == 0f) {
                step = width / 60f
            }
            if (center == 0f) {
                center = height / 2f
            }
            dataSet.forEachIndexed { index, fl ->
                if (index != dataSet.size - 1) {
                    canvas?.drawLine(
                        index * step,
                        center + (SensorManager.GRAVITY_EARTH - fl) * 50,
                        (index + 1) * step,
                        center + (SensorManager.GRAVITY_EARTH - dataSet[index + 1]) * 50,
                        paint
                    )
                }
            }
        }
    }

}