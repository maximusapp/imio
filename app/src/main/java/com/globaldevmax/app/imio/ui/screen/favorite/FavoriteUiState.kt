package com.globaldevmax.app.imio.ui.screen.favorite

import com.globaldevmax.app.imio.domain.model.Video

sealed interface FavoriteUiState {
    data class Empty(
        val isEveningModeActive: Boolean = false
    ) : FavoriteUiState

    data class Success(
        val videos: List<Video>
    ) : FavoriteUiState
}
