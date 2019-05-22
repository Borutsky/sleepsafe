package com.dudo.sleepsafe.data.dao

import androidx.room.*
import com.dudo.sleepsafe.data.model.DetectedActivity


@Dao
interface ActivitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivity(detectedActivity: DetectedActivity)

    @Update
    fun updateActivity(detectedActivity: DetectedActivity)

    @Delete
    fun deleteActivity(detectedActivity: DetectedActivity)

    @Query("SELECT * FROM DetectedActivity")
    fun getAll(): List<DetectedActivity>

}