package com.globaldevmax.app.imio.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globaldevmax.app.imio.core.catalog.VideoCatalogStore
import com.globaldevmax.app.imio.core.evening.EveningModeStore
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.repository.PremiumRepository
import com.globaldevmax.app.imio.domain.usecase.GetCachedVideosUseCase
import com.globaldevmax.app.imio.domain.usecase.GetVideosUseCase
import com.globaldevmax.app.imio.domain.usecase.ObserveFavoriteIdsUseCase
import com.globaldevmax.app.imio.domain.usecase.ObservePreferredVideoLocaleUseCase
import com.globaldevmax.app.imio.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getVideosUseCase: GetVideosUseCase,
    private val getCachedVideosUseCase: GetCachedVideosUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val eveningModeStore: EveningModeStore,
    private val videoCatalogStore: VideoCatalogStore,
    private val premiumRepository: PremiumRepository,
    observePreferredVideoLocaleUseCase: ObservePreferredVideoLocaleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var contentLocale: String = ""
    private var isEveningModeActive: Boolean = false
    private var showPremiumVideos: Boolean = false
    private var isPremiumSubscriptionActive: Boolean = false

    val favoriteIds: StateFlow<Set<String>> = observeFavoriteIdsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet()
        )

    init {
        viewModelScope.launch {
            eveningModeStore.isActive.collect { isActive ->
                setEveningModeActive(isActive)
            }
        }
        viewModelScope.launch {
            videoCatalogStore.showPremiumVideos.collect { showPremium ->
                showPremiumVideos = showPremium
                updateCatalogDisplayPreferences()
            }
        }
        viewModelScope.launch {
            premiumRepository.isPremiumActive.collect { isActive ->
                isPremiumSubscriptionActive = isActive
                updateCatalogDisplayPreferences()
            }
        }
        viewModelScope.launch {
            observePreferredVideoLocaleUseCase()
                .filterNotNull()
                .collect { locale ->
                    contentLocale = locale
                    applyContentLocale(locale)
                }
        }
        viewModelScope.launch {
            isEveningModeActive = eveningModeStore.isActive.first()
            showPremiumVideos = videoCatalogStore.showPremiumVideos.first()
            isPremiumSubscriptionActive = premiumRepository.isPremiumActive.first()
            val cachedVideos = getCachedVideosUseCase()
            if (cachedVideos.isNotEmpty()) {
                _uiState.value = successState(cachedVideos)
            } else {
                loadVideos()
            }
        }
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

    fun setShowPremiumVideos(show: Boolean) {
        viewModelScope.launch {
            videoCatalogStore.setShowPremiumVideos(show)
        }
    }

    private suspend fun fetchVideos() {
        getVideosUseCase()
            .onSuccess { videos ->
                Log.d(TAG, "Loaded ${videos.size} video(s) from KeepData")
                videos.forEach { video ->
                    Log.d(TAG, "Video: id=${video.id}, title=${video.title}, img=${video.previewImageUrl}, manifest=${video.manifestUrl}")
                }

                if (videos.isEmpty()) {
                    _uiState.value = HomeUiState.Empty
                } else {
                    _uiState.value = successState(videos)
                }
            }
            .onFailure { error ->
                Log.e(TAG, "Failed to load videos", error)
                _uiState.value = HomeUiState.Error(
                    message = error.message.orEmpty()
                )
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

    private fun applyContentLocale(locale: String) {
        _uiState.update { state ->
            when (state) {
                is HomeUiState.Success -> state.copy(contentLocale = locale)
                else -> state
            }
        }
    }

    private fun updateCatalogDisplayPreferences() {
        _uiState.update { state ->
            if (state is HomeUiState.Success) {
                state.copy(
                    showPremiumVideos = showPremiumVideos,
                    isPremiumSubscriptionActive = isPremiumSubscriptionActive
                )
            } else {
                state
            }
        }
    }

    private fun successState(videos: List<Video>): HomeUiState.Success {
        return HomeUiState.Success(
            allVideos = videos,
            isEveningModeActive = isEveningModeActive,
            contentLocale = contentLocale,
            showPremiumVideos = showPremiumVideos,
            isPremiumSubscriptionActive = isPremiumSubscriptionActive
        )
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
