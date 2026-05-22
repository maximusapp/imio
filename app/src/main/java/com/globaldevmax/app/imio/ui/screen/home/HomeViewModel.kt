package com.globaldevmax.app.imio.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globaldevmax.app.imio.domain.model.Video
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
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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

            getVideosUseCase()
                .onSuccess { videos ->
                    Log.d(TAG, "Loaded ${videos.size} video(s) from KeepData")
                    videos.forEach { video ->
                        Log.d(TAG, "Video: id=${video.id}, title=${video.title}, manifest=${video.manifestUrl}")
                    }

                    if (videos.isEmpty()) {
                        _uiState.value = HomeUiState.Empty
                    } else {
                        val previousFilter = (_uiState.value as? HomeUiState.Success)?.selectedFilter
                            ?: VideoFilter.ALL
                        _uiState.value = HomeUiState.Success(
                            allVideos = videos,
                            selectedFilter = previousFilter
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

    fun toggleFavorite(video: Video) {
        viewModelScope.launch {
            toggleFavoriteUseCase(video)
        }
    }

    private companion object {
        const val TAG = "HomeViewModel"
    }
}
