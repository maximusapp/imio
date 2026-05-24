package com.globaldevmax.app.imio.ui.screen.home

import com.globaldevmax.app.imio.domain.model.Video

enum class VideoFilter {
    ALL,
    PREMIUM,
    STANDARD;

    fun matches(video: Video): Boolean = when (this) {
        ALL -> true
        PREMIUM -> video.isPremium
        STANDARD -> !video.isPremium
    }
}

fun List<Video>.filterBy(filter: VideoFilter): List<Video> =
    filter { video -> filter.matches(video) }

fun List<Video>.filterBySearch(query: String): List<Video> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isEmpty()) return this

    return filter { video ->
        video.title.contains(trimmedQuery, ignoreCase = true)
    }
}
