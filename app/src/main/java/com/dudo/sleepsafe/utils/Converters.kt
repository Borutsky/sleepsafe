package com.dudo.sleepsafe.utils

import androidx.room.TypeConverter
import java.util.*


class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long): Date = Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time

    @TypeConverter
    fun fromStringDataset(value: String): List<Float> =
        value.split("|").map { it.toFloat() }

    @TypeConverter
    fun toStringDataset(dataset: List<Float>) =
            dataset.toList().joinToString("|")
}