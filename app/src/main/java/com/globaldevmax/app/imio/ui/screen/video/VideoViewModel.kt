package com.globaldevmax.app.imio.ui.screen.video

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.model.relatedVideosFor
import com.globaldevmax.app.imio.core.evening.EveningModeStore
import com.globaldevmax.app.imio.core.parent.ParentModeStore
import com.globaldevmax.app.imio.domain.repository.PremiumRepository
import com.globaldevmax.app.imio.domain.usecase.GetCachedVideosUseCase
import com.globaldevmax.app.imio.domain.usecase.GetVideoByIdUseCase
import com.globaldevmax.app.imio.domain.usecase.ObservePreferredVideoLocaleUseCase
import com.globaldevmax.app.imio.ui.screen.home.forContentLocale
import com.globaldevmax.app.imio.ui.screen.home.forEveningMode
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

@UnstableApi
class VideoViewModel(
    private val videoId: String,
    private val getVideoByIdUseCase: GetVideoByIdUseCase,
    private val getCachedVideosUseCase: GetCachedVideosUseCase,
    observePreferredVideoLocaleUseCase: ObservePreferredVideoLocaleUseCase,
    private val eveningModeStore: EveningModeStore,
    private val parentModeStore: ParentModeStore,
    private val premiumRepository: PremiumRepository,
    okHttpClient: OkHttpClient
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    private var contentLocale: String = ""
    private var isEveningModeActive: Boolean = false
    private var isPremiumActive: Boolean = false

    private val dataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
        .setUserAgent(USER_AGENT)

    private var exoPlayer: ExoPlayer? = null
    private var isParentModeBlockingPlayback: Boolean = false

    private fun shouldPauseForParentMode(
        sleepDialogVisible: Boolean,
        isActive: Boolean,
        endsAtMillis: Long
    ): Boolean {
        return sleepDialogVisible ||
            (isActive && endsAtMillis > 0L && endsAtMillis <= System.currentTimeMillis())
    }

    init {
        viewModelScope.launch {
            eveningModeStore.isActive.collect { isActive ->
                isEveningModeActive = isActive
                (_uiState.value as? VideoUiState.Ready)?.video?.let { updateReadyState(it) }
            }
        }
        viewModelScope.launch {
            premiumRepository.isPremiumActive.collect { isActive ->
                isPremiumActive = isActive
                (_uiState.value as? VideoUiState.Ready)?.video?.let { updateReadyState(it) }
            }
        }
        viewModelScope.launch {
            observePreferredVideoLocaleUseCase()
                .filterNotNull()
                .collect { locale ->
                    contentLocale = locale
                    (_uiState.value as? VideoUiState.Ready)?.video?.let { updateReadyState(it) }
                }
        }
        viewModelScope.launch {
            loadVideo(videoId)
        }
        viewModelScope.launch {
            parentModeStore.state.collect { state ->
                isParentModeBlockingPlayback = shouldPauseForParentMode(
                    sleepDialogVisible = state.sleepDialogVisible,
                    isActive = state.isActive,
                    endsAtMillis = state.endsAtMillis
                )
                if (isParentModeBlockingPlayback) {
                    pausePlayback()
                    exoPlayer?.playWhenReady = false
                }
            }
        }
    }

    fun switchToVideo(video: Video) {
        val currentVideo = (_uiState.value as? VideoUiState.Ready)?.video ?: return
        if (currentVideo.id == video.id) return

        viewModelScope.launch {
            val resolvedVideo = getVideoByIdUseCase(video.id) ?: video
            updateReadyState(resolvedVideo)
            preparePlayerForVideo(resolvedVideo.manifestUrl)
            Log.d(TAG, "Switched playback to ${resolvedVideo.title}")
        }
    }

    fun getOrCreatePlayer(context: android.content.Context): ExoPlayer {
        val manifestUrl = (uiState.value as? VideoUiState.Ready)?.video?.manifestUrl
            ?: error("Player requested before video is ready")

        exoPlayer?.let { return it }

        return ExoPlayer.Builder(context.applicationContext).build().also { player ->
            applyMediaSource(player, manifestUrl)
            exoPlayer = player
        }
    }

    fun pausePlayback() {
        exoPlayer?.pause()
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }

    private suspend fun loadVideo(id: String) {
        val video = getVideoByIdUseCase(id)
        if (video == null) {
            Log.e(TAG, "Video not found in cache: $id")
            _uiState.value = VideoUiState.Error(message = "")
            return
        }

        Log.d(TAG, "Preparing playback for ${video.manifestUrl}")
        updateReadyState(video)
    }

    private fun updateReadyState(video: Video) {
        val allVideos = getCachedVideosUseCase()
            .forContentLocale(contentLocale)
            .forEveningMode(isEveningModeActive)
        val otherVideos = allVideos.relatedVideosFor(
            currentVideo = video,
            isPremiumActive = isPremiumActive
        )

        _uiState.value = VideoUiState.Ready(
            video = video,
            otherVideos = otherVideos,
            contentLocale = contentLocale
        )
    }

    private fun preparePlayerForVideo(manifestUrl: String) {
        val player = exoPlayer ?: return
        applyMediaSource(player, manifestUrl)
    }

    private fun applyMediaSource(player: ExoPlayer, manifestUrl: String) {
        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(manifestUrl))

        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady = !isParentModeBlockingPlayback
        player.repeatMode = Player.REPEAT_MODE_OFF
    }

    private companion object {
        const val TAG = "VideoViewModel"
        const val USER_AGENT = "Imio/1.0"
    }
}
