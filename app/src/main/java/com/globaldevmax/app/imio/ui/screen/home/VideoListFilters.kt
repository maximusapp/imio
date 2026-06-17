package com.globaldevmax.app.imio.ui.screen.home

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.model.displayDescription
import com.globaldevmax.app.imio.domain.model.displayTitle

fun List<Video>.filterPremiumVisibility(
    isPremiumSubscriptionActive: Boolean,
    showPremiumVideos: Boolean
): List<Video> = when {
    isPremiumSubscriptionActive || showPremiumVideos -> this
    else -> filter { !it.isPremium }
}

fun List<Video>.filterBySearch(query: String, preferredLocale: String = ""): List<Video> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isEmpty()) return this

    return filter { video ->
        video.displayTitle(preferredLocale).contains(trimmedQuery, ignoreCase = true) ||
            video.displayDescription(preferredLocale).contains(trimmedQuery, ignoreCase = true) ||
            video.searchKeywords.any { keyword ->
                keyword.contains(trimmedQuery, ignoreCase = true)
            } ||
            video.categories.any { category ->
                category.contains(trimmedQuery, ignoreCase = true)
            }
    }
}
