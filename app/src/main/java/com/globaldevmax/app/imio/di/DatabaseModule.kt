package com.globaldevmax.app.imio.di

import androidx.room.Room
import com.globaldevmax.app.imio.data.local.ImioDatabase
import com.globaldevmax.app.imio.data.local.migration.ImioDatabaseMigrations
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            ImioDatabase::class.java,
            "imio_database"
        )
            .addMigrations(ImioDatabaseMigrations.MIGRATION_3_4)
            .build()
    }

    single { get<ImioDatabase>().favoriteVideoDao() }
}
