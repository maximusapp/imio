package com.globaldevmax.app.imio.ui.screen.home

data class VideoFilterCounts(
    val all: Int,
    val premium: Int,
    val standard: Int
) {
    fun countFor(filter: VideoFilter): Int = when (filter) {
        VideoFilter.ALL -> all
        VideoFilter.PREMIUM -> premium
        VideoFilter.STANDARD -> standard
    }
}
