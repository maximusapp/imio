package com.globaldevmax.app.imio.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.globaldevmax.app.imio.core.evening.EveningModeStore
import com.globaldevmax.app.imio.core.preferences.LegacySharedPreferencesMigration
import com.globaldevmax.app.imio.core.locale.VideoLocaleStore
import com.globaldevmax.app.imio.core.parent.ParentModeStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val IMIO_PREFERENCES_DATA_STORE = "imio_preferences"

val dataStoreModule = module {
    single<DataStore<Preferences>> {
        val context = androidContext()
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(IMIO_PREFERENCES_DATA_STORE) },
            migrations = listOf(LegacySharedPreferencesMigration(context))
        )
    }
    single { VideoLocaleStore(get()) }
    single { ParentModeStore(get()) }
    single { EveningModeStore(get()) }
}
