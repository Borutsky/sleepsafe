package com.dudo.sleepsafe.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import com.dudo.sleepsafe.R
import com.dudo.sleepsafe.data.database.AppDatabase
import com.dudo.sleepsafe.data.model.DetectedActivity
import com.dudo.sleepsafe.data.preferences.UserPreferences
import com.dudo.sleepsafe.di.Injector
import com.dudo.sleepsafe.ui.tracking.TrackingActivity
import com.dudo.sleepsafe.utils.SoundHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

class TrackingService : Service(), SensorEventListener {

    companion object {
        const val IDLE_ACCELERATION = "idle_acc"
        const val DATASET = "dataset"
        const val ALARM = "alarm"
        const val RUN_IN_BACKGROUND = "run_in_background"
        const val NOTIFICATION_CLICK = "notif_click"
        const val NOTIFICATION_ID = 543
        const val ACTION_CANCEL = "com.dudo.sleepsafe.ACTION_CANCEL"
        var running = false
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor

    private lateinit var notificationReceiver: BroadcastReceiver
    private lateinit var vibrator: Vibrator
    private var mediaPlayer: MediaPlayer? = null
    private val dataset = mutableListOf<Float>()
    private var idleAccelerationValue = 0f
    private var currentAccelerationValue = 0f
    private var tracking = true
    private var collecting = true
    private var isAlarm = false

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun onCreate() {
        super.onCreate()
        Injector.initTrackingServiceComponent()
        Injector.trackingServiceComponent?.inject(this)
        running = true
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        notificationReceiver = NotificationReceiver()
        startServiceWithNotification()
        idleAccelerationValue = intent.getFloatExtra(IDLE_ACCELERATION, 0f)
        sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val soundResource = SoundHelper.getSoundResourceById(
            UserPreferences(
                getSharedPreferences(
                    "USER_PREF",
                    Context.MODE_PRIVATE
                )
            ).getSoundIndex()
        )
        mediaPlayer = MediaPlayer.create(applicationContext, soundResource)
        mediaPlayer?.isLooping = true
        return START_STICKY
    }


    private fun startServiceWithNotification() {
        val notificationChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My Background Service")
        } else ""
        val notificationIntent = Intent(applicationContext, TrackingActivity::class.java)
        notificationIntent.action = NOTIFICATION_CLICK
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val contentPendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val cancelIntent = Intent(ACTION_CANCEL)
        val cancelPendingIntent =
            PendingIntent.getBroadcast(applicationContext, 0, cancelIntent, PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(this, notificationChannel)
            else Notification.Builder(this)
        val notification = notificationBuilder.setContentTitle(resources.getString(R.string.app_name))
            .setTicker(resources.getString(R.string.app_name))
            .setContentText("Tracking")
            .setSmallIcon(R.drawable.sleepsafe_logo)
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_close, "Cancel", cancelPendingIntent)
            .build()
        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
        startForeground(NOTIFICATION_ID, notification)
        applicationContext.registerReceiver(notificationReceiver, IntentFilter(ACTION_CANCEL))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = abs(event.values[0])
        val y = abs(event.values[1])
        val z = abs(event.values[2])
        dataset.add(x + y + z)
        val sendIntent = Intent()
        sendIntent.action = "tracking.update"
        if (collecting && dataset.size == 60) {
            idleAccelerationValue = dataset.sum()
            collecting = false
        }
        if (dataset.size > 60) {
            dataset.removeAt(0)
        }
        sendIntent.putExtra(DATASET, dataset.toFloatArray())
        sendIntent.putExtra(ALARM, isAlarm)
        if (collecting) return
        if (!tracking) {
            sendBroadcast(sendIntent)
            return
        }
        currentAccelerationValue = dataset.sum()
        val diff = abs(currentAccelerationValue - idleAccelerationValue)
        if (diff > (idleAccelerationValue / 60)) {
            tracking = false
            isAlarm = true
            mediaPlayer?.start()
            if (UserPreferences(getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)).isVibration()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500),
                            0
                        )
                    )
                } else {
                    vibrator.vibrate(longArrayOf(500), -1)
                }
            }
            newActivityToDb()
            startTrackingActivity()
        }
        sendBroadcast(sendIntent)
    }

    inner class NotificationReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            stopSelf()
        }

    }

    private fun newActivityToDb() {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.activitiesDao()
                .insertActivity(DetectedActivity(dataset = dataset, startDate = Date(), level = 0))
        }
    }

    private fun startTrackingActivity() {
        val intent = Intent(applicationContext, TrackingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(ALARM, true)
        startActivity(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        applicationContext.unregisterReceiver(notificationReceiver)
        stopForeground(true)
        running = false
        vibrator.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        sensorManager.unregisterListener(this)
        Injector.releaseTrackingServiceComponent()
    }

}