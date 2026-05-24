package com.globaldevmax.app.imio.ui.screen.home

import com.globaldevmax.app.imio.domain.model.Video

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object Empty : HomeUiState

    data class Success(
        val allVideos: List<Video>,
        val selectedFilter: VideoFilter = VideoFilter.ALL,
        val isEveningModeActive: Boolean = false
    ) : HomeUiState {
        private val modeFilteredVideos: List<Video>
            get() = allVideos.forEveningMode(isEveningModeActive)

        val displayedVideos: List<Video>
            get() = modeFilteredVideos.filterBy(selectedFilter)

        val filterCounts: VideoFilterCounts
            get() = VideoFilterCounts(
                all = modeFilteredVideos.size,
                premium = modeFilteredVideos.count { it.isPremium },
                standard = modeFilteredVideos.count { !it.isPremium }
            )
    }

    data class Error(
        val message: String
    ) : HomeUiState
}
