package com.dudo.sleepsafe.ui.main.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dudo.sleepsafe.data.preferences.UserPreferences
import javax.inject.Inject

class SettingsViewModel @Inject constructor(userPreferences: UserPreferences) : ViewModel() {

    var flashlight = MutableLiveData<Boolean>()
    var vibration = MutableLiveData<Boolean>()
    var soundIndex = MutableLiveData<Int>()

    init {
        flashlight.value = userPreferences.isFlashlight()
        vibration.value = userPreferences.isVibration()
        soundIndex.value = userPreferences.getSoundIndex()
        flashlight.observeForever {
            userPreferences.setFlashlight(it)
        }
        vibration.observeForever {
            userPreferences.setVibration(it)
        }
        soundIndex.observeForever {
            userPreferences.setSoundIndex(it)
        }
    }

}
