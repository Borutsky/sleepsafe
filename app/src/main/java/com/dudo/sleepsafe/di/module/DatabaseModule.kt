package com.dudo.sleepsafe.di.module

import android.content.Context
import androidx.room.Room
import com.dudo.sleepsafe.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(val context: Context) {

    @Singleton
    @Provides
    fun provideAppDatabase(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "activities")
            .build()

}