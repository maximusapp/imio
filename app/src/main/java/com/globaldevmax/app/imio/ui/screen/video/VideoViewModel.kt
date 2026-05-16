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
import com.globaldevmax.app.imio.domain.usecase.GetVideoByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

@UnstableApi
class VideoViewModel(
    private val videoId: String,
    private val getVideoByIdUseCase: GetVideoByIdUseCase,
    okHttpClient: OkHttpClient
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    private val dataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
        .setUserAgent(USER_AGENT)

    private var exoPlayer: ExoPlayer? = null

    init {
        viewModelScope.launch {
            val video = getVideoByIdUseCase(videoId)
            if (video == null) {
                Log.e(TAG, "Video not found in cache: $videoId")
                _uiState.value = VideoUiState.Error(message = "")
                return@launch
            }

            Log.d(TAG, "Preparing playback for ${video.manifestUrl}")
            _uiState.value = VideoUiState.Ready(video)
        }
    }

    fun getOrCreatePlayer(context: android.content.Context): ExoPlayer {
        val currentState = _uiState.value
        val manifestUrl = (currentState as? VideoUiState.Ready)?.video?.manifestUrl
            ?: error("Player requested before video is ready")

        exoPlayer?.let { return it }

        return ExoPlayer.Builder(context.applicationContext).build().also { player ->
            val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(manifestUrl))

            player.setMediaSource(mediaSource)
            player.prepare()
            player.playWhenReady = true
            player.repeatMode = Player.REPEAT_MODE_OFF
            exoPlayer = player
        }
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }

    private companion object {
        const val TAG = "VideoViewModel"
        const val USER_AGENT = "Imio/1.0"
    }
}
