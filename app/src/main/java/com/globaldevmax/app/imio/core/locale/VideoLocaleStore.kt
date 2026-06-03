package com.globaldevmax.app.imio.core.locale

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.globaldevmax.app.imio.core.preferences.ImioPreferenceKeys
import com.globaldevmax.app.imio.core.preferences.VideoContentLocale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VideoLocaleStore(
    private val dataStore: DataStore<Preferences>
) {
    val preferredVideoLocale: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ImioPreferenceKeys.VIDEO_LOCALE]
            ?.takeIf { it.isNotBlank() && it in VideoContentLocale.SUPPORTED }
    }

    val hasSelectedVideoLocale: Flow<Boolean> = preferredVideoLocale.map { it != null }

    suspend fun setPreferredVideoLocale(locale: String) {
        require(locale in VideoContentLocale.SUPPORTED) {
            "Unsupported video locale: $locale"
        }
        dataStore.edit { preferences ->
            preferences[ImioPreferenceKeys.VIDEO_LOCALE] = locale
        }
    }

    suspend fun clearPreferredVideoLocale() {
        dataStore.edit { preferences ->
            preferences.remove(ImioPreferenceKeys.VIDEO_LOCALE)
        }
    }
}
