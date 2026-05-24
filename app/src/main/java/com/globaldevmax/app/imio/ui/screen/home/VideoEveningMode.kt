package com.globaldevmax.app.imio.ui.screen.home

import com.globaldevmax.app.imio.domain.model.Video

fun List<Video>.forEveningMode(isEveningModeActive: Boolean): List<Video> {
    if (!isEveningModeActive) return this
    return filter { video -> video.isBedtime }
}
