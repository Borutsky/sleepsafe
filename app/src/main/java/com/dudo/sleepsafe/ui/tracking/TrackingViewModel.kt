package com.dudo.sleepsafe.ui.tracking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dudo.sleepsafe.data.database.AppDatabase
import com.dudo.sleepsafe.data.preferences.UserPreferences
import javax.inject.Inject

class TrackingViewModel @Inject constructor(val appDatabase: AppDatabase,
                                            val userPreferences: UserPreferences): ViewModel() {

    val isFlashlight = MutableLiveData<Boolean>()
    val isAlarm = MutableLiveData<Boolean>()
    val isAutoFlashlight = MutableLiveData<Boolean>()

    init {
        isAutoFlashlight.value = userPreferences.isFlashlight()
    }

}