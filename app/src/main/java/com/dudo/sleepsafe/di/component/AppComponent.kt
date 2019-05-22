package com.dudo.sleepsafe.di.component

import com.dudo.sleepsafe.data.database.AppDatabase
import com.dudo.sleepsafe.data.preferences.UserPreferences
import com.dudo.sleepsafe.di.module.DatabaseModule
import com.dudo.sleepsafe.di.module.PreferencesModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [PreferencesModule::class, DatabaseModule::class])
interface AppComponent {

    fun userPreferences(): UserPreferences

    fun appDatabase(): AppDatabase

}