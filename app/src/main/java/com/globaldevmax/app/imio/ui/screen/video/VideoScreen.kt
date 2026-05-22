package com.globaldevmax.app.imio.ui.screen.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ImioBackButton
import com.globaldevmax.app.imio.ui.components.ImioLoadingIndicator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
fun VideoScreen(
    videoId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoViewModel = koinViewModel { parametersOf(videoId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        ImioBackButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 28.dp)
        )

        when (val state = uiState) {
            VideoUiState.Loading -> {
                ImioLoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is VideoUiState.Error -> {
                Text(
                    text = state.message.ifBlank {
                        stringResource(R.string.video_not_found)
                    },
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp)
                )
            }

            is VideoUiState.Ready -> {
                AndroidView(
                    factory = { factoryContext ->
                        PlayerView(factoryContext).apply {
                            player = viewModel.getOrCreatePlayer(context)
                            useController = true
                        }
                    },
                    update = { playerView ->
                        val player = viewModel.getOrCreatePlayer(context)
                        if (playerView.player != player) {
                            playerView.player = player
                        }
                    },
                    onRelease = { playerView ->
                        playerView.player = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
        }
    }
}
