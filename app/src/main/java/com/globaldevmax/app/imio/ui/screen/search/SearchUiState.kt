package com.globaldevmax.app.imio.ui.screen.search

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.ui.screen.home.filterBySearch
import com.globaldevmax.app.imio.ui.screen.home.forEveningMode

sealed interface SearchUiState {
    data object Loading : SearchUiState

    data object Empty : SearchUiState

    data class Ready(
        val allVideos: List<Video>,
        val searchQuery: String = "",
        val isEveningModeActive: Boolean = false
    ) : SearchUiState {
        val displayedVideos: List<Video>
            get() = allVideos
                .filterBySearch(searchQuery)
                .forEveningMode(isEveningModeActive)

        val isSearchActive: Boolean
            get() = searchQuery.isNotBlank()
    }

    data class Error(
        val message: String
    ) : SearchUiState
}
