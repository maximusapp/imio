package com.globaldevmax.app.imio.domain.model

data class Video(
    val id: String,
    val title: String,
    val format: String,
    val manifestUrl: String,
    val durationMs: Long,
    val previewImageUrl: String,
    val locale: String,
    val isPremium: Boolean,
    val isBedtime: Boolean = false
)
