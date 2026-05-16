package com.globaldevmax.app.imio.data.repository

import com.globaldevmax.app.imio.data.local.dao.FavoriteVideoDao
import com.globaldevmax.app.imio.data.local.mapper.toDomain
import com.globaldevmax.app.imio.data.local.mapper.toFavoriteEntity
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val favoriteVideoDao: FavoriteVideoDao
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<Video>> {
        return favoriteVideoDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeFavoriteIds(): Flow<Set<String>> {
        return favoriteVideoDao.observeFavoriteIds().map { ids ->
            ids.toSet()
        }
    }

    override suspend fun getFavoriteById(videoId: String): Video? {
        return favoriteVideoDao.getById(videoId)?.toDomain()
    }

    override suspend fun toggleFavorite(video: Video) {
        if (favoriteVideoDao.isFavorite(video.id)) {
            favoriteVideoDao.delete(video.id)
        } else {
            favoriteVideoDao.insert(video.toFavoriteEntity())
        }
    }
}
