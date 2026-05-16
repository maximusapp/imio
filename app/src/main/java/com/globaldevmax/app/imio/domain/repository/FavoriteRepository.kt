package com.globaldevmax.app.imio.domain.repository

import com.globaldevmax.app.imio.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<Video>>
    fun observeFavoriteIds(): Flow<Set<String>>
    suspend fun getFavoriteById(videoId: String): Video?
    suspend fun toggleFavorite(video: Video)
}
