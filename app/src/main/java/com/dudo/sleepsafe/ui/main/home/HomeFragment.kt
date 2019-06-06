package com.dudo.sleepsafe.ui.main.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.*
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.dudo.sleepsafe.R
import com.dudo.sleepsafe.di.Injector
import com.dudo.sleepsafe.ui.tracking.TrackingActivity
import com.dudo.sleepsafe.utils.ViewModelFactory
import com.dudo.sleepsafe.utils.injectViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.abs

class HomeFragment : Fragment(), SensorEventListener {

    private val dataset = mutableListOf<Float>()
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var job: Job? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Injector.initHomeComponent()
        Injector.homeComponent?.inject(this)
        initViewModel()
        initButtons()
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    private fun initButtons() {
        buttonTrackBlock.setOnClickListener {
            val intent = Intent(activity!!.applicationContext, TrackingActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }
    }

    private fun initViewModel() {
        viewModel = injectViewModel(viewModelFactory)
        viewModel.init()
        if (viewModel.isAlarm.hasObservers()) return
        viewModel.isReady.observe(this, Observer {
            if (it) {
                if (job == null) {
                    job = GlobalScope.launch(Dispatchers.IO) {
                        delay(2000)
                        withContext(Dispatchers.Main) {
                            val autoTransition = AutoTransition()
                            autoTransition.duration = 500
                            val constraintSet = ConstraintSet()
                            constraintSet.clone(constraintLayout)
                            constraintSet.setVisibility(R.id.textHint, View.GONE)
                            constraintSet.setVisibility(R.id.buttonTrackBlock, View.VISIBLE)
                            TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
                            constraintSet.applyTo(constraintLayout)
                        }
                    }
                }
            } else {
                job?.cancel()
                job = null
                textHint.visibility = View.VISIBLE
                buttonTrackBlock.visibility = View.GONE
            }
        })
        /*viewModel.isFlashlight.observe(this, Observer {
            if (checkCameraPermission()) {
                if (it) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraId?.let { camId ->
                            cameraManager?.setTorchMode(camId, true)
                        }
                    } else {
                        camera = Camera.open()
                        val p = camera?.parameters
                        p?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                        camera?.parameters = p
                        camera?.startPreview()
                    }
                    buttonFlash.setImageResource(R.drawable.ic_flash_on)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraId?.let { camId ->
                            cameraManager?.setTorchMode(camId, false)
                        }
                    } else {
                        camera?.stopPreview()
                        camera?.release()
                        camera = null
                    }
                    buttonFlash.setImageResource(R.drawable.ic_flash_off)
                }
            }
        })*/
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        val x = abs(event.values[0])
        val y = abs(event.values[1])
        val z = abs(event.values[2])
        dataset.add(x + y + z)
        if (dataset.size > 60) {
            dataset.removeAt(0)
        }
        val gX = event.values[0] / SensorManager.GRAVITY_EARTH
        val gY = event.values[1] / SensorManager.GRAVITY_EARTH
        val params = circleInner.layoutParams as ConstraintLayout.LayoutParams
        params.horizontalBias = (0.5f - (gX / 2)).coerceIn(0f, 1f)
        params.verticalBias = (0.5f + (gY / 2)).coerceIn(0f, 1f)
        circleInner.layoutParams = params

        if (gX !in -0.1..0.1 || gY !in -0.1..0.1) {
            circleMain.background = ContextCompat.getDrawable(context!!, R.drawable.circle_main_filled)
            circleInner.background = ContextCompat.getDrawable(context!!, R.drawable.circle_inner)
            viewModel.isReady.value = false
        } else {
            circleMain.background = ContextCompat.getDrawable(context!!, R.drawable.circle_main)
            circleInner.background = ContextCompat.getDrawable(context!!, R.drawable.circle_inner_white)
            viewModel.isReady.value = true
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensorManager.unregisterListener(this)
        job?.cancel()
        job = null
        Injector.releaseHomeComponent()
    }

}
