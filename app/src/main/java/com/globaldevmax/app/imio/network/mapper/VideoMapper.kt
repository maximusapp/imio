package com.globaldevmax.app.imio.network.mapper

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.model.VideoLocalization
import com.globaldevmax.app.imio.network.dto.VideoDto
import com.globaldevmax.app.imio.network.dto.VideoLocalizationDto
import com.globaldevmax.app.imio.network.sanitizeMediaUrl

fun VideoDto.toDomain(): Video {
    return Video(
        id = id,
        title = title,
        description = description.orEmpty(),
        format = format,
        manifestUrl = manifestUrl.sanitizeMediaUrl(),
        durationMs = durationMs,
        previewImageUrl = previewImage.orEmpty().sanitizeMediaUrl(),
        locale = locale,
        isPremium = isPremium,
        isBedtime = isBedtime,
        sortOrder = sortOrder,
        publishedAt = publishedAt,
        ageMin = ageMin,
        ageMax = ageMax,
        categories = categories.orEmpty(),
        searchKeywords = searchKeywords.orEmpty(),
        relatedVideoIds = relatedVideoIds.orEmpty(),
        localizations = localizations.orEmpty().map { it.toDomain() }
    )
}

private fun VideoLocalizationDto.toDomain(): VideoLocalization {
    return VideoLocalization(
        locale = locale,
        title = title,
        description = description.orEmpty()
    )
}
