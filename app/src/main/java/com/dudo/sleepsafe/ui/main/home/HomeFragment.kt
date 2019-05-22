package com.dudo.sleepsafe.ui.main.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.hardware.*
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.dudo.sleepsafe.R
import com.dudo.sleepsafe.data.model.DetectedActivity
import com.dudo.sleepsafe.di.Injector
import com.dudo.sleepsafe.utils.ViewModelFactory
import com.dudo.sleepsafe.utils.checkCameraPermission
import com.dudo.sleepsafe.utils.injectViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.*
import java.lang.Thread.sleep
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

class HomeFragment : Fragment(), SensorEventListener {

    private val dataset = mutableListOf<Float>()
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var vibrator: Vibrator
    private var camera: Camera? = null
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
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

    private fun initCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraId = cameraManager?.cameraIdList?.first()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Injector.initHomeComponent()
        Injector.homeComponent?.inject(this)
        initCamera()
        initViewModel()
        initButtons()
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private fun initButtons() {
        buttonTrackBlock.setOnClickListener {
            if (viewModel.isButtonCancel.value == true) {
                AlertDialog.Builder(context!!)
                    .setTitle("Tracking")
                    .setMessage("Are you sure you want to stop tracking?")
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        viewModel.isTracking.value = false
                        viewModel.isAlarm.value = false
                        viewModel.isButtonCancel.value = false
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .show()
            } else {
                viewModel.isTracking.value = true
                viewModel.isButtonCancel.value = true
                viewModel.idleAccelerationWeight.value = viewModel.currentAccelerationWeight.value
            }
        }
        buttonFlash.setOnClickListener {
            viewModel.isFlashlight.value = viewModel.isFlashlight.value?.not()
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
        viewModel.isButtonCancel.observe(this, Observer {
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            if (it) {
                buttonTrackText.text = getString(R.string.stop)
                buttonTrackText.setTextColor(getColor(context!!, R.color.colorAccent))
                buttonTrackIcon.setImageResource(R.drawable.ic_stop)
                buttonTrackBlock.setBackgroundResource(R.drawable.bg_button_stop)
                constraintSet.setVisibility(R.id.buttonFlash, View.VISIBLE)
            } else {
                buttonTrackText.text = getString(R.string.start)
                buttonTrackText.setTextColor(getColor(context!!, R.color.colorPrimaryDark))
                buttonTrackIcon.setImageResource(R.drawable.ic_start)
                buttonTrackBlock.setBackgroundResource(R.drawable.bg_button_start)
                constraintSet.setVisibility(R.id.buttonFlash, View.GONE)
            }
            val autoTransition = AutoTransition()
            autoTransition.duration = 500
            TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
            constraintSet.applyTo(constraintLayout)
        })
        viewModel.isTracking.observe(this, Observer {
            val autoTransition = AutoTransition()
            autoTransition.duration = 500
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.setVisibility(R.id.simpleChart, if (it) View.VISIBLE else View.GONE)
            constraintSet.setVisibility(R.id.circleMain, if (it) View.GONE else View.VISIBLE)
            constraintSet.setVisibility(R.id.circleInner, if (it) View.GONE else View.VISIBLE)
            val color = arrayOf(
                ColorDrawable(getColor(context!!, R.color.colorPrimary)),
                ColorDrawable(getColor(context!!, R.color.colorPrimaryDark))
            )
            val trans = TransitionDrawable(if (it) color else color.reversedArray())
            activity?.toolbar?.background = trans
            trans.startTransition(500)
            TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
            constraintSet.applyTo(constraintLayout)
        })
        viewModel.isAlarm.observe(this, Observer {
            if (it) {
                alarmStart()
                textPower.visibility = View.VISIBLE
                if (viewModel.isAutoFlashlight.value == true) {
                    viewModel.isFlashlight.value = true
                }
            } else {
                alarmStop()
                textPower.visibility = View.GONE
                viewModel.isFlashlight.value = false
            }
        })
        viewModel.isFlashlight.observe(this, Observer {
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
        })
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
        viewModel.currentAccelerationWeight.value = dataset.sum()
        if (viewModel.isTracking.value == true) {
            simpleChart.update(dataset)
            val diff = abs(viewModel.currentAccelerationWeight.value!! - viewModel.idleAccelerationWeight.value!!)
            if (diff > (viewModel.idleAccelerationWeight.value!! / 60)) {
                when {
                    diff > (viewModel.idleAccelerationWeight.value!! / 40) -> {
                        textPower.text = "HIGH"
                        textPower.setTextColor(Color.RED)
                        viewModel.vibrationLevel.value = 3
                    }
                    diff > (viewModel.idleAccelerationWeight.value!! / 50) -> {
                        textPower.text = "MEDIUM"
                        textPower.setTextColor(Color.YELLOW)
                        viewModel.vibrationLevel.value = 2
                    }
                    else -> {
                        textPower.text = "LOW"
                        textPower.setTextColor(Color.GREEN)
                        viewModel.vibrationLevel.value = 1
                    }
                }
                if (viewModel.isAlarm.value != true) {
                    viewModel.isAlarm.value = true
                }
            }
        } else {
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
    }

    private fun alarmStart() {
        GlobalScope.launch(Dispatchers.Default) {
            sleep(300)
            withContext(Dispatchers.Main) {
                viewModel.newDetectedActivity(
                    DetectedActivity(
                        dataset = dataset,
                        startDate = Date(),
                        level = viewModel.vibrationLevel.value ?: 1
                    )
                )
            }
        }
        if (viewModel.isVibration.value == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500), 0)
                )
            } else {
                vibrator.vibrate(longArrayOf(500), -1)
            }
        }
    }

    private fun alarmStop() {
        vibrator.cancel()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraId?.let {
                cameraManager?.setTorchMode(it, false)
            }
        } else {
            camera?.stopPreview()
            camera?.release()
            camera = null
        }
        alarmStop()
        sensorManager.unregisterListener(this)
        job?.cancel()
        job = null
        Injector.releaseHomeComponent()
    }

}
