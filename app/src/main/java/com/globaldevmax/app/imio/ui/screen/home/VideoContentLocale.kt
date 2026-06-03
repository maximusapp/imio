package com.globaldevmax.app.imio.ui.screen.home

import com.globaldevmax.app.imio.domain.model.Video

fun List<Video>.forContentLocale(locale: String): List<Video> {
    if (locale.isBlank()) return this
    return filter { video -> video.locale.equals(locale, ignoreCase = true) }
}
