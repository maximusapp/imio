package com.globaldevmax.app.imio.ui.screen.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globaldevmax.app.imio.core.evening.EveningModeStore
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.usecase.GetCachedVideosUseCase
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

class SearchViewModel(
    private val getVideosUseCase: GetVideosUseCase,
    private val getCachedVideosUseCase: GetCachedVideosUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val eveningModeStore: EveningModeStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

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
        val cachedVideos = getCachedVideosUseCase()
        if (cachedVideos.isNotEmpty()) {
            _uiState.value = SearchUiState.Ready(
                allVideos = cachedVideos,
                isEveningModeActive = isEveningModeActive
            )
        } else {
            loadVideos()
        }
    }

    fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
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

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            if (state is SearchUiState.Ready) {
                state.copy(searchQuery = query)
            } else {
                state
            }
        }
    }

    fun clearSearch() {
        onSearchQueryChange("")
    }

    fun setEveningModeActive(isActive: Boolean) {
        isEveningModeActive = isActive
        _uiState.update { state ->
            if (state is SearchUiState.Ready) {
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

    private suspend fun fetchVideos() {
        val previousState = _uiState.value as? SearchUiState.Ready
        val previousSearchQuery = previousState?.searchQuery.orEmpty()

        getVideosUseCase()
            .onSuccess { videos ->
                Log.d(TAG, "Loaded ${videos.size} video(s) for search")
                applyVideos(
                    videos = videos,
                    searchQuery = previousSearchQuery,
                    isEveningModeActive = isEveningModeActive
                )
            }
            .onFailure { error ->
                Log.e(TAG, "Failed to load videos for search", error)
                val cachedVideos = getCachedVideosUseCase()
                if (cachedVideos.isNotEmpty()) {
                    applyVideos(
                        videos = cachedVideos,
                        searchQuery = previousSearchQuery,
                        isEveningModeActive = isEveningModeActive
                    )
                } else {
                    _uiState.value = SearchUiState.Error(message = error.message.orEmpty())
                }
            }
    }

    private fun applyVideos(
        videos: List<Video>,
        searchQuery: String,
        isEveningModeActive: Boolean
    ) {
        if (videos.isEmpty()) {
            _uiState.value = SearchUiState.Empty
        } else {
            _uiState.value = SearchUiState.Ready(
                allVideos = videos,
                searchQuery = searchQuery,
                isEveningModeActive = isEveningModeActive
            )
        }
    }

    private companion object {
        const val TAG = "SearchViewModel"
    }
}
