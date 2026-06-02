package com.globaldevmax.app.imio.network.dto

import com.google.gson.annotations.SerializedName

data class VideosResponseDto(
    @SerializedName("schema_version")
    val schemaVersion: Int = 1,
    @SerializedName("videos")
    val videos: List<VideoDto> = emptyList()
)

data class VideoDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null,
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
    @SerializedName("is_premium")
    val isPremium: Boolean,
    @SerializedName("is_bedtime")
    val isBedtime: Boolean = false,
    @SerializedName("sort_order")
    val sortOrder: Int = 0,
    @SerializedName("published_at")
    val publishedAt: String? = null,
    @SerializedName("age_min")
    val ageMin: Int = 2,
    @SerializedName("age_max")
    val ageMax: Int = 6,
    @SerializedName("categories")
    val categories: List<String> = emptyList(),
    @SerializedName("search_keywords")
    val searchKeywords: List<String> = emptyList(),
    @SerializedName("related_video_ids")
    val relatedVideoIds: List<String> = emptyList()
)
