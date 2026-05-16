package com.globaldevmax.app.imio.domain.usecase

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.repository.FavoriteRepository

class ToggleFavoriteUseCase(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(video: Video) {
        favoriteRepository.toggleFavorite(video)
    }
}
