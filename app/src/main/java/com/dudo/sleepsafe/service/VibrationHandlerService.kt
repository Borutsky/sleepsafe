package com.dudo.sleepsafe.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.os.Vibrator
import kotlin.math.abs

class VibrationHandlerService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var vibrator: Vibrator

    private val dataset = mutableListOf<Float>()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = abs(event.values[0])
        val y = abs(event.values[1])
        val z = abs(event.values[2])
        dataset.add(x + y + z)
        if (dataset.size > 60) {
            dataset.removeAt(0)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}
