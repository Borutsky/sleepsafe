package com.dudo.sleepsafe.di

import android.app.Application
import com.dudo.sleepsafe.di.component.*
import com.dudo.sleepsafe.di.module.DatabaseModule
import com.dudo.sleepsafe.di.module.PreferencesModule

object Injector {

    var appComponent: AppComponent? = null
    var welcomeComponent: WelcomeComponent? = null
    var splashComponent: SplashComponent? = null
    var homeComponent: HomeComponent? = null
    var settingsComponent: SettingsComponent? = null
    var historyComponent: HistoryComponent? = null

    fun initAppComponent(application: Application) {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                .preferencesModule(PreferencesModule(application.applicationContext))
                .databaseModule(DatabaseModule(application.applicationContext))
                .build()
        }
    }

    fun initWelcomeComponent() {
        if (welcomeComponent == null) {
            welcomeComponent = DaggerWelcomeComponent.builder()
                .appComponent(appComponent)
                .build()
        }
    }

    fun initSplashComponent() {
        if (splashComponent == null) {
            splashComponent = DaggerSplashComponent.builder()
                .appComponent(appComponent)
                .build()
        }
    }

    fun initHomeComponent() {
        if (homeComponent == null) {
            homeComponent = DaggerHomeComponent.builder()
                .appComponent(appComponent)
                .build()
        }
    }

    fun initSettingsComponent() {
        if (settingsComponent == null) {
            settingsComponent = DaggerSettingsComponent.builder()
                .appComponent(appComponent)
                .build()
        }
    }

    fun initHistoryComponent() {
        if(historyComponent == null) {
            historyComponent = DaggerHistoryComponent.builder()
                .appComponent(appComponent)
                .build()
        }
    }

    fun releaseAppComponent() {
        appComponent = null
    }

    fun releaseWelcomeComponent() {
        welcomeComponent = null
    }

    fun releaseSplashComponent() {
        splashComponent = null
    }

    fun releaseHomeComponent() {
        homeComponent = null
    }

    fun releaseSettingsComponent() {
        settingsComponent = null
    }

    fun releaseHistoryComponent() {
        historyComponent = null
    }

}