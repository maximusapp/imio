package com.globaldevmax.app.imio.ui.screen.video

import com.globaldevmax.app.imio.domain.model.Video

sealed interface VideoUiState {
    data object Loading : VideoUiState

    data class Ready(
        val video: Video,
        val otherVideos: List<Video> = emptyList(),
        val contentLocale: String = ""
    ) : VideoUiState

    data class Error(
        val message: String
    ) : VideoUiState
}
