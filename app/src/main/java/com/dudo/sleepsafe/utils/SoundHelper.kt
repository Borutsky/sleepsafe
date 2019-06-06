package com.dudo.sleepsafe.utils

import com.dudo.sleepsafe.R

object SoundHelper {

    fun getSoundResourceById(id: Int): Int = when(id) {
        0 -> R.raw.alarm1
        1 -> R.raw.alarm2
        2 -> R.raw.alarm3
        else -> R.raw.alarm1
    }
}