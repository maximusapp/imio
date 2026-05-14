package com.globaldevmax.app.imio.core.parent

import android.content.Context

class ParentModeStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun isParentModeActive(): Boolean = preferences.getBoolean(KEY_IS_ACTIVE, false)

    fun getAllowedMinutes(): String = preferences.getString(KEY_ALLOWED_MINUTES, "").orEmpty()

    fun getEndsAtMillis(): Long = preferences.getLong(KEY_ENDS_AT_MILLIS, 0L)

    fun isSleepDialogVisible(): Boolean = preferences.getBoolean(KEY_SLEEP_DIALOG_VISIBLE, false)

    fun getRecentMinutes(): List<String> {
        return preferences.getString(KEY_RECENT_MINUTES, "").orEmpty()
            .split(RECENT_MINUTES_SEPARATOR)
            .map(String::trim)
            .filter(String::isNotEmpty)
    }

    fun saveAllowedMinutes(minutes: String) {
        preferences.edit()
            .putString(KEY_ALLOWED_MINUTES, minutes)
            .apply()
    }

    fun saveRecentMinute(minutes: String) {
        if (minutes.toLongOrNull() == null) return

        val recentMinutes = (listOf(minutes) + getRecentMinutes())
            .distinct()
            .take(MAX_RECENT_MINUTES)
            .joinToString(RECENT_MINUTES_SEPARATOR)

        preferences.edit()
            .putString(KEY_RECENT_MINUTES, recentMinutes)
            .apply()
    }

    fun activate(allowedMinutes: String, endsAtMillis: Long) {
        preferences.edit()
            .putBoolean(KEY_IS_ACTIVE, true)
            .putString(KEY_ALLOWED_MINUTES, allowedMinutes)
            .putLong(KEY_ENDS_AT_MILLIS, endsAtMillis)
            .putBoolean(KEY_SLEEP_DIALOG_VISIBLE, false)
            .apply()
    }

    fun updateEndsAtMillis(endsAtMillis: Long) {
        preferences.edit()
            .putLong(KEY_ENDS_AT_MILLIS, endsAtMillis)
            .putBoolean(KEY_SLEEP_DIALOG_VISIBLE, false)
            .apply()
    }

    fun showSleepDialog() {
        preferences.edit()
            .putBoolean(KEY_SLEEP_DIALOG_VISIBLE, true)
            .apply()
    }

    fun deactivate() {
        preferences.edit()
            .putBoolean(KEY_IS_ACTIVE, false)
            .putLong(KEY_ENDS_AT_MILLIS, 0L)
            .putBoolean(KEY_SLEEP_DIALOG_VISIBLE, false)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "parent_mode"
        const val KEY_IS_ACTIVE = "is_active"
        const val KEY_ALLOWED_MINUTES = "allowed_minutes"
        const val KEY_ENDS_AT_MILLIS = "ends_at_millis"
        const val KEY_SLEEP_DIALOG_VISIBLE = "sleep_dialog_visible"
        const val KEY_RECENT_MINUTES = "recent_minutes"
        const val RECENT_MINUTES_SEPARATOR = ","
        const val MAX_RECENT_MINUTES = 5
    }
}
