package com.globaldevmax.app.imio.data.repository

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.repository.VideoRepository
import com.globaldevmax.app.imio.network.api.VideosApiService
import com.globaldevmax.app.imio.network.mapper.toDomain

class VideoRepositoryImpl(
    private val videosApiService: VideosApiService
) : VideoRepository {

    @Volatile
    private var cachedVideos: List<Video> = emptyList()

    override suspend fun getVideos(): Result<List<Video>> {
        return runCatching {
            val videos = videosApiService.getVideos().videos.map { it.toDomain() }
            cachedVideos = videos
            videos
        }
    }

    override fun getCachedVideoById(id: String): Video? {
        return cachedVideos.find { it.id == id }
    }
}
