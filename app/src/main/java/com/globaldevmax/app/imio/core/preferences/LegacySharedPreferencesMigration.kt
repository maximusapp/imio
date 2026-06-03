package com.globaldevmax.app.imio.core.preferences

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
internal class LegacySharedPreferencesMigration(
    private val context: Context
) : DataMigration<Preferences> {

    override suspend fun shouldMigrate(currentData: Preferences): Boolean {
        if (currentData[ImioPreferenceKeys.LEGACY_PREFS_MIGRATED] == true) {
            return false
        }
        val parentPrefs = context.getSharedPreferences(PARENT_PREFS_NAME, Context.MODE_PRIVATE)
        val eveningPrefs = context.getSharedPreferences(EVENING_PREFS_NAME, Context.MODE_PRIVATE)
        return parentPrefs.all.isNotEmpty() || eveningPrefs.all.isNotEmpty()
    }

    override suspend fun migrate(currentData: Preferences): Preferences {
        val parentPrefs = context.getSharedPreferences(PARENT_PREFS_NAME, Context.MODE_PRIVATE)
        val eveningPrefs = context.getSharedPreferences(EVENING_PREFS_NAME, Context.MODE_PRIVATE)
        val mutable = currentData.toMutablePreferences()

        if (parentPrefs.all.isNotEmpty()) {
            mutable[ImioPreferenceKeys.PARENT_MODE_IS_ACTIVE] =
                parentPrefs.getBoolean(KEY_IS_ACTIVE, false)
            mutable[ImioPreferenceKeys.PARENT_MODE_ALLOWED_MINUTES] =
                parentPrefs.getString(KEY_ALLOWED_MINUTES, "").orEmpty()
            mutable[ImioPreferenceKeys.PARENT_MODE_ENDS_AT_MILLIS] =
                parentPrefs.getLong(KEY_ENDS_AT_MILLIS, 0L)
            mutable[ImioPreferenceKeys.PARENT_MODE_SLEEP_DIALOG_VISIBLE] =
                parentPrefs.getBoolean(KEY_SLEEP_DIALOG_VISIBLE, false)
            mutable[ImioPreferenceKeys.PARENT_MODE_RECENT_MINUTES] =
                parentPrefs.getString(KEY_RECENT_MINUTES, "").orEmpty()
        }

        if (eveningPrefs.all.isNotEmpty()) {
            mutable[ImioPreferenceKeys.EVENING_MODE_IS_ACTIVE] =
                eveningPrefs.getBoolean(KEY_IS_ACTIVE, false)
        }

        mutable[ImioPreferenceKeys.LEGACY_PREFS_MIGRATED] = true
        return mutable
    }

    override suspend fun cleanUp() {
        context.deleteSharedPreferences(PARENT_PREFS_NAME)
        context.deleteSharedPreferences(EVENING_PREFS_NAME)
    }

    private companion object {
        const val PARENT_PREFS_NAME = "parent_mode"
        const val EVENING_PREFS_NAME = "evening_mode"
        const val KEY_IS_ACTIVE = "is_active"
        const val KEY_ALLOWED_MINUTES = "allowed_minutes"
        const val KEY_ENDS_AT_MILLIS = "ends_at_millis"
        const val KEY_SLEEP_DIALOG_VISIBLE = "sleep_dialog_visible"
        const val KEY_RECENT_MINUTES = "recent_minutes"
    }
}
