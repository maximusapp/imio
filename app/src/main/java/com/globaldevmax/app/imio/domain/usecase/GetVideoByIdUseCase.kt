package com.globaldevmax.app.imio.domain.usecase

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.repository.FavoriteRepository
import com.globaldevmax.app.imio.domain.repository.VideoRepository

class GetVideoByIdUseCase(
    private val videoRepository: VideoRepository,
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(videoId: String): Video? {
        return videoRepository.getCachedVideoById(videoId)
            ?: favoriteRepository.getFavoriteById(videoId)
    }
}
