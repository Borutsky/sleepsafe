package com.dudo.sleepsafe.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class DetectedActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val dataset: List<Float>,
    val startDate: Date,
    val level: Int
)