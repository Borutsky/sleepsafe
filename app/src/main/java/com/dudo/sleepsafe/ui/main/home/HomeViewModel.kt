package com.dudo.sleepsafe.ui.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.dudo.sleepsafe.data.database.AppDatabase
import com.dudo.sleepsafe.data.model.DetectedActivity
import com.dudo.sleepsafe.data.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    val userPreferences: UserPreferences,
    val appDatabase: AppDatabase
) : ViewModel() {

    val isTracking = MutableLiveData<Boolean>()
    val isAlarm = MutableLiveData<Boolean>()
    val isReady = MutableLiveData<Boolean>()
    val isButtonCancel = MutableLiveData<Boolean>()
    val isVibration = MutableLiveData<Boolean>()
    val isAutoFlashlight = MutableLiveData<Boolean>()
    val isFlashlight = MutableLiveData<Boolean>()
    val currentAccelerationWeight = MutableLiveData<Float>()
    val idleAccelerationWeight = MutableLiveData<Float>()
    val vibrationLevel = MutableLiveData<Int>()

    fun init() {
        isTracking.value = false
        isReady.value = false
        isButtonCancel.value = false
        isFlashlight.value = false
        isVibration.value = userPreferences.isVibration()
        isAutoFlashlight.value = userPreferences.isFlashlight()
        currentAccelerationWeight.value = 0f
        idleAccelerationWeight.value = 0f
        vibrationLevel.value = 0
    }

    fun newDetectedActivity(detectedActivity: DetectedActivity) {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.activitiesDao()
                .insertActivity(detectedActivity)
        }
    }

}
