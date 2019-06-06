package com.dudo.sleepsafe.ui.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    val isAlarm = MutableLiveData<Boolean>()
    val isReady = MutableLiveData<Boolean>()

    fun init() {
        isReady.value = false
    }



}
