package com.globaldevmax.app.imio.data.local.mapper

import com.globaldevmax.app.imio.data.local.entity.FavoriteVideoEntity
import com.globaldevmax.app.imio.domain.model.Video

fun Video.toFavoriteEntity(addedAtMillis: Long = System.currentTimeMillis()): FavoriteVideoEntity {
    return FavoriteVideoEntity(
        id = id,
        title = title,
        format = format,
        manifestUrl = manifestUrl,
        durationMs = durationMs,
        previewImageUrl = previewImageUrl,
        locale = locale,
        isPremium = isPremium,
        addedAtMillis = addedAtMillis
    )
}

fun FavoriteVideoEntity.toDomain(): Video {
    return Video(
        id = id,
        title = title,
        format = format,
        manifestUrl = manifestUrl,
        durationMs = durationMs,
        previewImageUrl = previewImageUrl,
        locale = locale,
        isPremium = isPremium
    )
}
