package com.dudo.sleepsafe.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dudo.sleepsafe.data.preferences.UserPreferences
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val userPreferences: UserPreferences): ViewModel() {

    val isFirst = MutableLiveData<Boolean>()

    init {
        isFirst.value = userPreferences.isFirstEntry()
    }

}