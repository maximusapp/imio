package com.globaldevmax.app.imio.ui.screen.home

import com.globaldevmax.app.imio.domain.model.Video

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object Empty : HomeUiState

    data class Success(
        val allVideos: List<Video>,
        val selectedFilter: VideoFilter = VideoFilter.ALL,
        val isEveningModeActive: Boolean = false,
        val contentLocale: String = ""
    ) : HomeUiState {
        private val modeFilteredVideos: List<Video>
            get() = allVideos
                .forContentLocale(contentLocale)
                .forEveningMode(isEveningModeActive)

        val displayedVideos: List<Video>
            get() = modeFilteredVideos.filterBy(selectedFilter)
    }

    data class Error(
        val message: String
    ) : HomeUiState
}
