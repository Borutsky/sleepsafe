package com.dudo.sleepsafe.di.module

import android.content.Context
import com.dudo.sleepsafe.data.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferencesModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideUserPreferences(): UserPreferences =
        UserPreferences(context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE))

}