package com.globaldevmax.app.imio.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.core.evening.EveningModeStore
import com.globaldevmax.app.imio.domain.usecase.GetVideosUseCase
import com.globaldevmax.app.imio.domain.usecase.ObserveFavoriteIdsUseCase
import com.globaldevmax.app.imio.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getVideosUseCase: GetVideosUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val eveningModeStore: EveningModeStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var isEveningModeActive = eveningModeStore.isEveningModeActive()

    val favoriteIds: StateFlow<Set<String>> = observeFavoriteIdsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet()
        )

    init {
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            fetchVideos()
        }
    }

    fun refreshVideos() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchVideos()
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchVideos() {
        val previousState = _uiState.value as? HomeUiState.Success
        val previousFilter = previousState?.selectedFilter ?: VideoFilter.ALL

        getVideosUseCase()
            .onSuccess { videos ->
                Log.d(TAG, "Loaded ${videos.size} video(s) from KeepData")
                videos.forEach { video ->
                    Log.d(TAG, "Video: id=${video.id}, title=${video.title}, manifest=${video.manifestUrl}")
                }

                if (videos.isEmpty()) {
                    _uiState.value = HomeUiState.Empty
                } else {
                    _uiState.value = HomeUiState.Success(
                        allVideos = videos,
                        selectedFilter = previousFilter,
                        isEveningModeActive = isEveningModeActive
                    )
                }
            }
            .onFailure { error ->
                Log.e(TAG, "Failed to load videos", error)
                _uiState.value = HomeUiState.Error(
                    message = error.message.orEmpty()
                )
            }
    }

    fun onFilterSelected(filter: VideoFilter) {
        _uiState.update { state ->
            if (state is HomeUiState.Success) {
                state.copy(selectedFilter = filter)
            } else {
                state
            }
        }
    }

    fun setEveningModeActive(isActive: Boolean) {
        isEveningModeActive = isActive
        _uiState.update { state ->
            if (state is HomeUiState.Success) {
                state.copy(isEveningModeActive = isActive)
            } else {
                state
            }
        }
    }

    fun syncEveningModeFromStore() {
        setEveningModeActive(eveningModeStore.isEveningModeActive())
    }

    fun toggleFavorite(video: Video) {
        viewModelScope.launch {
            toggleFavoriteUseCase(video)
        }
    }

    private companion object {
        const val TAG = "HomeViewModel"
    }
}
