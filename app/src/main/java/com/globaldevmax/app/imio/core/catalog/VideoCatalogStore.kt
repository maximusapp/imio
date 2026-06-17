package com.globaldevmax.app.imio.core.catalog

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.globaldevmax.app.imio.core.preferences.ImioPreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VideoCatalogStore(
    private val dataStore: DataStore<Preferences>
) {
    val showPremiumVideos: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ImioPreferenceKeys.SHOW_PREMIUM_VIDEOS_IN_CATALOG] ?: false
    }

    suspend fun setShowPremiumVideos(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[ImioPreferenceKeys.SHOW_PREMIUM_VIDEOS_IN_CATALOG] = show
        }
    }
}
