package com.globaldevmax.app.imio.core.evening

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.globaldevmax.app.imio.core.preferences.ImioPreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EveningModeStore(
    private val dataStore: DataStore<Preferences>
) {
    val isActive: Flow<Boolean> = dataStore.data.map {
        it[ImioPreferenceKeys.EVENING_MODE_IS_ACTIVE] ?: false
    }

    suspend fun activate() {
        dataStore.edit { preferences ->
            preferences[ImioPreferenceKeys.EVENING_MODE_IS_ACTIVE] = true
        }
    }

    suspend fun deactivate() {
        dataStore.edit { preferences ->
            preferences[ImioPreferenceKeys.EVENING_MODE_IS_ACTIVE] = false
        }
    }
}
