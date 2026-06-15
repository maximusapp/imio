package com.globaldevmax.app.imio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_videos")
data class FavoriteVideoEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String = "",
    val format: String,
    val manifestUrl: String,
    val durationMs: Long,
    val previewImageUrl: String,
    val locale: String,
    val isPremium: Boolean,
    val isBedtime: Boolean = false,
    val localizationsJson: String? = null,
    val addedAtMillis: Long
)
