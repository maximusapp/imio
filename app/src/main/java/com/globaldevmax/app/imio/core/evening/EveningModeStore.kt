package com.globaldevmax.app.imio.core.evening

import android.content.Context

class EveningModeStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun isEveningModeActive(): Boolean = preferences.getBoolean(KEY_IS_ACTIVE, false)

    fun activate() {
        preferences.edit()
            .putBoolean(KEY_IS_ACTIVE, true)
            .apply()
    }

    fun deactivate() {
        preferences.edit()
            .putBoolean(KEY_IS_ACTIVE, false)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "evening_mode"
        const val KEY_IS_ACTIVE = "is_active"
    }
}
