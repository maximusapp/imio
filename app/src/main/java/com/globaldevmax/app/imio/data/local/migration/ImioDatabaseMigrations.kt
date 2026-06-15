package com.globaldevmax.app.imio.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object ImioDatabaseMigrations {
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                ALTER TABLE favorite_videos
                ADD COLUMN description TEXT NOT NULL DEFAULT ''
                """.trimIndent()
            )
            db.execSQL(
                """
                ALTER TABLE favorite_videos
                ADD COLUMN localizationsJson TEXT
                """.trimIndent()
            )
        }
    }
}
