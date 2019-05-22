package com.dudo.sleepsafe.data.preferences

import android.content.SharedPreferences

class UserPreferences(private val preferences: SharedPreferences) {

    companion object {
        private const val IS_FIRST_ENTRY = "is_first_entry"
        private const val IS_VIBRATION = "is_vibration"
        private const val IS_FLASHLIGHT = "is_flashlight"
        private const val SOUND_INDEX = "sound_index"
    }

    fun setFirstEntry(first: Boolean){
        preferences.edit()
            .putBoolean(IS_FIRST_ENTRY, first)
            .apply()
    }

    fun isFirstEntry(): Boolean = preferences.getBoolean(IS_FIRST_ENTRY, true)

    fun setVibration(vibration: Boolean) {
        preferences.edit()
            .putBoolean(IS_VIBRATION, vibration)
            .apply()
    }

    fun isVibration(): Boolean = preferences.getBoolean(IS_VIBRATION, false)

    fun setFlashlight(flashlight: Boolean) {
        preferences.edit()
            .putBoolean(IS_FLASHLIGHT, flashlight)
            .apply()
    }

    fun isFlashlight(): Boolean = preferences.getBoolean(IS_FLASHLIGHT, false)

    fun setSoundIndex(soundIndex: Int) {
        preferences.edit()
            .putInt(SOUND_INDEX, soundIndex)
            .apply()
    }

    fun getSoundIndex(): Int = preferences.getInt(SOUND_INDEX, 0)

}