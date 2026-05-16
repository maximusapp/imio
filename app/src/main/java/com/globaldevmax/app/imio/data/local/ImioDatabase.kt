package com.globaldevmax.app.imio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.globaldevmax.app.imio.data.local.dao.FavoriteVideoDao
import com.globaldevmax.app.imio.data.local.entity.FavoriteVideoEntity

@Database(
    entities = [FavoriteVideoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ImioDatabase : RoomDatabase() {
    abstract fun favoriteVideoDao(): FavoriteVideoDao
}
