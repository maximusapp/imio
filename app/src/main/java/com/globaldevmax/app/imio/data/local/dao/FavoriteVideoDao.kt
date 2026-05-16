package com.globaldevmax.app.imio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.globaldevmax.app.imio.data.local.entity.FavoriteVideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteVideoDao {

    @Query("SELECT * FROM favorite_videos ORDER BY addedAtMillis DESC")
    fun observeAll(): Flow<List<FavoriteVideoEntity>>

    @Query("SELECT id FROM favorite_videos")
    fun observeFavoriteIds(): Flow<List<String>>

    @Query("SELECT * FROM favorite_videos WHERE id = :videoId LIMIT 1")
    suspend fun getById(videoId: String): FavoriteVideoEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_videos WHERE id = :videoId)")
    suspend fun isFavorite(videoId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: FavoriteVideoEntity)

    @Query("DELETE FROM favorite_videos WHERE id = :videoId")
    suspend fun delete(videoId: String)
}
