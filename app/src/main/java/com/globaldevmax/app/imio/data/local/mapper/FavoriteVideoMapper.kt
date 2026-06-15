package com.globaldevmax.app.imio.data.local.mapper

import com.globaldevmax.app.imio.data.local.VideoLocalizationsJsonCodec
import com.globaldevmax.app.imio.data.local.entity.FavoriteVideoEntity
import com.globaldevmax.app.imio.domain.model.Video

fun Video.toFavoriteEntity(addedAtMillis: Long = System.currentTimeMillis()): FavoriteVideoEntity {
    return FavoriteVideoEntity(
        id = id,
        title = title,
        description = description,
        format = format,
        manifestUrl = manifestUrl,
        durationMs = durationMs,
        previewImageUrl = previewImageUrl,
        locale = locale,
        isPremium = isPremium,
        isBedtime = isBedtime,
        localizationsJson = VideoLocalizationsJsonCodec.encode(localizations),
        addedAtMillis = addedAtMillis
    )
}

fun FavoriteVideoEntity.toDomain(): Video {
    return Video(
        id = id,
        title = title,
        description = description,
        format = format,
        manifestUrl = manifestUrl,
        durationMs = durationMs,
        previewImageUrl = previewImageUrl,
        locale = locale,
        isPremium = isPremium,
        isBedtime = isBedtime,
        localizations = VideoLocalizationsJsonCodec.decode(localizationsJson)
    )
}
