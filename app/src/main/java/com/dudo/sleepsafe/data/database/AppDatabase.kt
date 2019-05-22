package com.dudo.sleepsafe.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dudo.sleepsafe.data.dao.ActivitiesDao
import com.dudo.sleepsafe.data.model.DetectedActivity
import com.dudo.sleepsafe.utils.Converters

@Database(entities = [DetectedActivity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activitiesDao(): ActivitiesDao

}