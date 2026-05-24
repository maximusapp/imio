package com.globaldevmax.app.imio.network.dto

import com.google.gson.annotations.SerializedName

data class VideosResponseDto(
    @SerializedName("videos")
    val videos: List<VideoDto> = emptyList()
)

data class VideoDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("manifest_url")
    val manifestUrl: String,
    @SerializedName("duration_ms")
    val durationMs: Long,
    @SerializedName("preview_image")
    val previewImage: String?,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("isPremium")
    val isPremium: Boolean,
    @SerializedName("isBedtime")
    val isBedtime: Boolean = false
)
