package com.dudo.sleepsafe.ui.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import com.dudo.sleepsafe.R
import com.dudo.sleepsafe.di.Injector
import com.dudo.sleepsafe.service.TrackingService
import com.dudo.sleepsafe.ui.main.MainActivity
import com.dudo.sleepsafe.utils.ViewModelFactory
import com.dudo.sleepsafe.utils.injectViewModel
import kotlinx.android.synthetic.main.activity_tracking.*
import kotlinx.android.synthetic.main.home_fragment.buttonTrackBlock
import kotlinx.android.synthetic.main.home_fragment.constraintLayout
import javax.inject.Inject

class TrackingActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: TrackingViewModel
    private lateinit var receiver: BroadcastReceiver

    private var camera: Camera? = null
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
        Injector.initTrackingComponent()
        Injector.trackingComponent?.inject(this)
        viewModel = injectViewModel(viewModelFactory)
        initState()
        initShowOnLockScreen()
        initObservers()
        initBroadcastReceiver()
        initButtons()
        initCamera()
        startTrackingService()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter()
        filter.addAction("tracking.update")
        registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    private fun initState() {
        if (intent.getBooleanExtra(TrackingService.ALARM, false)) {
            buttonTrackBlock.visibility = View.VISIBLE
        }
    }

    private fun initShowOnLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }

    private fun initObservers() {
        viewModel.isAlarm.observe(this, Observer {
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.setVisibility(R.id.buttonTrackBlock, if (it) View.VISIBLE else View.GONE)
            TransitionManager.beginDelayedTransition(constraintLayout)
            constraintSet.applyTo(constraintLayout)
        })
        viewModel.isFlashlight.observe(this, Observer {
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
                buttonFlashlight.setImageResource(R.drawable.ic_flash_on)
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
                buttonFlashlight.setImageResource(R.drawable.ic_flash_off)
            }
        })
    }

    private fun initCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraId = cameraManager?.cameraIdList?.first()
        }
    }

    private fun initBroadcastReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val dataset = intent.getFloatArrayExtra(TrackingService.DATASET)
                simpleChart.update(dataset.toMutableList())
                if (intent.getBooleanExtra(TrackingService.ALARM, false)) {
                    viewModel.isAlarm.value = true
                }
            }
        }
    }

    private fun initButtons() {
        buttonTrackBlock.setOnClickListener {
            val serviceIntent = Intent(applicationContext, TrackingService::class.java)
            stopService(serviceIntent)
        }
        buttonClose.setOnClickListener {
            val foregroundColorSpan = ForegroundColorSpan(Color.BLACK)
            val title = getString(R.string.tracking)
            val ssBuilder = SpannableStringBuilder(title)
            ssBuilder.setSpan(foregroundColorSpan, 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle(ssBuilder)
                .setMessage(getString(R.string.really_stop))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    val serviceIntent = Intent(applicationContext, TrackingService::class.java)
                    stopService(serviceIntent)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    finish()
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }
        buttonFlashlight.setOnClickListener {
            viewModel.isFlashlight.value = viewModel.isFlashlight.value?.not() ?: true
        }
    }

    private fun startTrackingService() {
        if (!TrackingService.running) {
            val serviceIntent = Intent(applicationContext, TrackingService::class.java).also {
                it.putExtra(TrackingService.RUN_IN_BACKGROUND, true)
            }
            applicationContext.startService(serviceIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Injector.releaseTrackingComponent()
    }

}
