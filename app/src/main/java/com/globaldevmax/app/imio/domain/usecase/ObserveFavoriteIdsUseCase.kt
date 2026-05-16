package com.globaldevmax.app.imio.domain.usecase

import com.globaldevmax.app.imio.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteIdsUseCase(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<Set<String>> = favoriteRepository.observeFavoriteIds()
}
