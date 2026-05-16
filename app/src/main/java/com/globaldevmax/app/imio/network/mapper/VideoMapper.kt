package com.globaldevmax.app.imio.network.mapper

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.network.dto.VideoDto

fun VideoDto.toDomain(): Video {
    return Video(
        id = id,
        title = title,
        format = format,
        manifestUrl = manifestUrl,
        durationMs = durationMs,
        previewImageUrl = previewImage.orEmpty(),
        locale = locale,
        isPremium = isPremium
    )
}
