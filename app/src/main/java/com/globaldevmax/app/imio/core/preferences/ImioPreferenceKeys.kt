package com.globaldevmax.app.imio.core.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object ImioPreferenceKeys {
    val LEGACY_PREFS_MIGRATED = booleanPreferencesKey("legacy_prefs_migrated")

    val VIDEO_LOCALE = stringPreferencesKey("video_locale")

    val PARENT_MODE_IS_ACTIVE = booleanPreferencesKey("parent_mode_is_active")
    val PARENT_MODE_ALLOWED_MINUTES = stringPreferencesKey("parent_mode_allowed_minutes")
    val PARENT_MODE_ENDS_AT_MILLIS = longPreferencesKey("parent_mode_ends_at_millis")
    val PARENT_MODE_SLEEP_DIALOG_VISIBLE = booleanPreferencesKey("parent_mode_sleep_dialog_visible")
    val PARENT_MODE_RECENT_MINUTES = stringPreferencesKey("parent_mode_recent_minutes")

    val EVENING_MODE_IS_ACTIVE = booleanPreferencesKey("evening_mode_is_active")
}

object VideoContentLocale {
    const val UK = "uk"
    const val EN = "en"

    val SUPPORTED = setOf(UK, EN)
}
