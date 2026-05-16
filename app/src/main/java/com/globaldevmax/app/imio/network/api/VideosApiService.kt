package com.globaldevmax.app.imio.network.api

import com.globaldevmax.app.imio.network.dto.VideosResponseDto
import retrofit2.http.GET

interface VideosApiService {

    @GET("webdav/maximus09/videos.json")
    suspend fun getVideos(): VideosResponseDto
}
