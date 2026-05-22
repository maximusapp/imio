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
