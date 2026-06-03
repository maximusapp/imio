package com.globaldevmax.app.imio.ui.screen.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globaldevmax.app.imio.core.evening.EveningModeStore
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.usecase.ObserveFavoriteIdsUseCase
import com.globaldevmax.app.imio.domain.usecase.ObserveFavoritesUseCase
import com.globaldevmax.app.imio.domain.usecase.ObservePreferredVideoLocaleUseCase
import com.globaldevmax.app.imio.domain.usecase.ToggleFavoriteUseCase
import com.globaldevmax.app.imio.ui.screen.home.forContentLocale
import com.globaldevmax.app.imio.ui.screen.home.forEveningMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    observePreferredVideoLocaleUseCase: ObservePreferredVideoLocaleUseCase,
    eveningModeStore: EveningModeStore,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    val uiState: StateFlow<FavoriteUiState> = combine(
        observeFavoritesUseCase(),
        observeFavoriteIdsUseCase(),
        observePreferredVideoLocaleUseCase().filterNotNull(),
        eveningModeStore.isActive
    ) { favorites, _, contentLocale, isEveningModeActive ->
        val displayed = favorites
            .forContentLocale(contentLocale)
            .forEveningMode(isEveningModeActive)
        if (displayed.isEmpty()) {
            FavoriteUiState.Empty(isEveningModeActive = isEveningModeActive)
        } else {
            FavoriteUiState.Success(displayed)
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoriteUiState.Empty()
        )

    val favoriteIds: StateFlow<Set<String>> = observeFavoriteIdsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet()
        )

    fun toggleFavorite(video: Video) {
        viewModelScope.launch {
            toggleFavoriteUseCase(video)
        }
    }
}
