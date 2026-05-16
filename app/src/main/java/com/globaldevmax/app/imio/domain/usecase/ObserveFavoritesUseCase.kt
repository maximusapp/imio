package com.globaldevmax.app.imio.domain.usecase

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<Video>> = favoriteRepository.observeFavorites()
}
