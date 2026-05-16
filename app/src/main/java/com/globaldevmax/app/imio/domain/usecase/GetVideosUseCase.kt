package com.globaldevmax.app.imio.domain.usecase

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.repository.VideoRepository

class GetVideosUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(): Result<List<Video>> {
        return videoRepository.getVideos()
    }
}
