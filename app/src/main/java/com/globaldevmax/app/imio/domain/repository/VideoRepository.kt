package com.globaldevmax.app.imio.domain.repository

import com.globaldevmax.app.imio.domain.model.Video

interface VideoRepository {
    suspend fun getVideos(): Result<List<Video>>
    fun getCachedVideoById(id: String): Video?
}
