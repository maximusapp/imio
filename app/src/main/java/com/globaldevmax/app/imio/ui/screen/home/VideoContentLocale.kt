package com.globaldevmax.app.imio.ui.screen.home

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.model.matchesContentLocale

fun List<Video>.forContentLocale(locale: String): List<Video> {
    if (locale.isBlank()) return this
    return filter { video -> video.matchesContentLocale(locale) }
}
