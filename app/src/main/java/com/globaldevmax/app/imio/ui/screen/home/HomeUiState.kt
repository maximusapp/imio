package com.globaldevmax.app.imio.ui.screen.home

import com.globaldevmax.app.imio.domain.model.Video

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object Empty : HomeUiState

    data class Success(
        val videos: List<Video>
    ) : HomeUiState

    data class Error(
        val message: String
    ) : HomeUiState
}
