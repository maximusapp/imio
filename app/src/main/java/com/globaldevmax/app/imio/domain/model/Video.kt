package com.globaldevmax.app.imio.domain.model

import com.globaldevmax.app.imio.core.preferences.VideoContentLocale

data class Video(
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
    val sortOrder: Int = 0,
    val publishedAt: String? = null,
    val ageMin: Int = 2,
    val ageMax: Int = 6,
    val categories: List<String> = emptyList(),
    val searchKeywords: List<String> = emptyList(),
    val relatedVideoIds: List<String> = emptyList(),
    val localizations: List<VideoLocalization> = emptyList()
)

fun Video.matchesContentLocale(preferredLocale: String): Boolean {
    if (preferredLocale.isBlank()) return true
    return locale.equals(VideoContentLocale.ALL, ignoreCase = true) ||
        locale.equals(preferredLocale, ignoreCase = true)
}

fun Video.displayTitle(preferredLocale: String): String =
    localizations.resolveText(preferredLocale)?.title ?: title

fun Video.displayDescription(preferredLocale: String): String =
    localizations.resolveText(preferredLocale)?.description ?: description

private fun List<VideoLocalization>.resolveText(preferredLocale: String): VideoLocalization? {
    if (isEmpty() || preferredLocale.isBlank()) return null
    return find { it.locale.equals(preferredLocale, ignoreCase = true) }
}

enum class VideoSortMode {
    /** One-time shuffle per app session (current default behaviour). */
    RANDOM,
    SORT_ORDER_ASC,
    SORT_ORDER_DESC,
    PUBLISHED_AT_DESC,
    PUBLISHED_AT_ASC
}

fun List<Video>.sortedByMode(mode: VideoSortMode): List<Video> = when (mode) {
    VideoSortMode.RANDOM -> this
    VideoSortMode.SORT_ORDER_ASC -> sortedBy { it.sortOrder }
    VideoSortMode.SORT_ORDER_DESC -> sortedByDescending { it.sortOrder }
    VideoSortMode.PUBLISHED_AT_DESC -> sortedByDescending { it.publishedAt.orEmpty() }
    VideoSortMode.PUBLISHED_AT_ASC -> sortedBy { it.publishedAt.orEmpty() }
}

private const val RELATED_VIDEOS_LIMIT = 25

fun List<Video>.relatedVideosFor(
    currentVideo: Video,
    limit: Int = RELATED_VIDEOS_LIMIT
): List<Video> {
    val byId = associateBy { it.id }

    val related = currentVideo.relatedVideoIds
        .mapNotNull { relatedId -> byId[relatedId] }
        .filter { it.id != currentVideo.id }

    val relatedIds = related.map { it.id }.toSet()
    val others = filter { video ->
        video.id != currentVideo.id && video.id !in relatedIds
    }

    return (related + others).take(limit)
}
