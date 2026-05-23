package com.globaldevmax.app.imio.domain.usecase

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.repository.VideoRepository

class GetCachedVideosUseCase(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(): List<Video> = videoRepository.getCachedVideos()
}
