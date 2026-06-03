package com.globaldevmax.app.imio.core.parent

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.globaldevmax.app.imio.core.preferences.ImioPreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParentModeStore(
    private val dataStore: DataStore<Preferences>
) {
    val state: Flow<ParentModeState> = dataStore.data.map { preferences ->
        ParentModeState(
            isActive = preferences[ImioPreferenceKeys.PARENT_MODE_IS_ACTIVE] ?: false,
            allowedMinutes = preferences[ImioPreferenceKeys.PARENT_MODE_ALLOWED_MINUTES].orEmpty(),
            endsAtMillis = preferences[ImioPreferenceKeys.PARENT_MODE_ENDS_AT_MILLIS] ?: 0L,
            sleepDialogVisible = preferences[ImioPreferenceKeys.PARENT_MODE_SLEEP_DIALOG_VISIBLE] ?: false,
            recentMinutes = preferences[ImioPreferenceKeys.PARENT_MODE_RECENT_MINUTES].orEmpty()
                .split(RECENT_MINUTES_SEPARATOR)
                .map(String::trim)
                .filter(String::isNotEmpty)
        )
    }

    suspend fun saveAllowedMinutes(minutes: String) {
        dataStore.edit { preferences ->
            preferences[ImioPreferenceKeys.PARENT_MODE_ALLOWED_MINUTES] = minutes
        }
    }

    suspend fun saveRecentMinute(minutes: String) {
        if (minutes.toLongOrNull() == null) return

        dataStore.edit { preferences ->
            val currentRecent = preferences[ImioPreferenceKeys.PARENT_MODE_RECENT_MINUTES].orEmpty()
                .split(RECENT_MINUTES_SEPARATOR)
                .map(String::trim)
                .filter(String::isNotEmpty)

            val recentMinutes = (listOf(minutes) + currentRecent)
                .distinct()
                .take(MAX_RECENT_MINUTES)
                .joinToString(RECENT_MINUTES_SEPARATOR)

            preferences[ImioPreferenceKeys.PARENT_MODE_RECENT_MINUTES] = recentMinutes
        }
    }

    suspend fun activate(allowedMinutes: String, endsAtMillis: Long) {
        dataStore.edit { preferences ->
            preferences[ImioPreferenceKeys.PARENT_MODE_IS_ACTIVE] = true
            preferences[ImioPreferenceKeys.PARENT_MODE_ALLOWED_MINUTES] = allowedMinutes
            preferences[ImioPreferenceKeys.PARENT_MODE_ENDS_AT_MILLIS] = endsAtMillis
            preferences[ImioPreferenceKeys.PARENT_MODE_SLEEP_DIALOG_VISIBLE] = false
        }
    }

    suspend fun updateEndsAtMillis(endsAtMillis: Long) {
        dataStore.edit { preferences ->
            preferences[ImioPreferenceKeys.PARENT_MODE_ENDS_AT_MILLIS] = endsAtMillis
            preferences[ImioPreferenceKeys.PARENT_MODE_SLEEP_DIALOG_VISIBLE] = false
        }
    }

    suspend fun showSleepDialog() {
        dataStore.edit { preferences ->
            preferences[ImioPreferenceKeys.PARENT_MODE_SLEEP_DIALOG_VISIBLE] = true
        }
    }

    suspend fun deactivate() {
        dataStore.edit { preferences ->
            preferences[ImioPreferenceKeys.PARENT_MODE_IS_ACTIVE] = false
            preferences[ImioPreferenceKeys.PARENT_MODE_ENDS_AT_MILLIS] = 0L
            preferences[ImioPreferenceKeys.PARENT_MODE_SLEEP_DIALOG_VISIBLE] = false
        }
    }

    private companion object {
        const val RECENT_MINUTES_SEPARATOR = ","
        const val MAX_RECENT_MINUTES = 5
    }
}
