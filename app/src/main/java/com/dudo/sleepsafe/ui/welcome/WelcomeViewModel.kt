package com.dudo.sleepsafe.ui.welcome

import androidx.lifecycle.ViewModel
import com.dudo.sleepsafe.data.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class WelcomeViewModel @Inject constructor(private val userPreferences: UserPreferences) : ViewModel() {

    fun firstEntryDone(){
        GlobalScope.launch(Dispatchers.IO) {
            userPreferences.setFirstEntry(false)
        }
    }

}