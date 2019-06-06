package com.dudo.sleepsafe.ui.main.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dudo.sleepsafe.data.database.AppDatabase
import com.dudo.sleepsafe.data.model.DetectedActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HistoryViewModel @Inject constructor(val appDatabase: AppDatabase) : ViewModel() {

    val activities = MutableLiveData<List<DetectedActivity>>()

    fun refresh(){
        GlobalScope.launch(Dispatchers.Main) {
            activities.value = withContext(Dispatchers.IO) {
                appDatabase.activitiesDao().getAll()
            }
        }
    }

}
