package com.dudo.sleepsafe

import android.app.Application
import com.dudo.sleepsafe.di.Injector

class SleepSafe : Application() {

    override fun onCreate() {
        super.onCreate()
        Injector.initAppComponent(this)
    }

}
