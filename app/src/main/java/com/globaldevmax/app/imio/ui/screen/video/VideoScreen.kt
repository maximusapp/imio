package com.globaldevmax.app.imio.ui.screen.video

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.ui.components.ImioBackButton
import com.globaldevmax.app.imio.ui.components.ImioLoadingIndicator
import com.globaldevmax.app.imio.ui.components.VideoListItem
import com.globaldevmax.app.imio.ui.components.VideoListItemSize
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import com.globaldevmax.app.imio.ui.theme.ImioOnBackground
import com.globaldevmax.app.imio.ui.theme.Pink
import com.globaldevmax.app.imio.ui.theme.Purple40
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val PlayerCardOuterShape = RoundedCornerShape(24.dp)
private val PlayerCardInnerShape = RoundedCornerShape(22.dp)

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
fun VideoScreen(
    videoId: String,
    isPremiumSubscriptionActive: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoViewModel = koinViewModel { parametersOf(videoId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            VideoUiState.Loading -> {
                ImioBackButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(start = 4.dp, top = 4.dp)
                )
                ImioLoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is VideoUiState.Error -> {
                ImioBackButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(start = 4.dp, top = 4.dp)
                )
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
                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    PortraitVideoContent(
                        video = state.video,
                        otherVideos = state.otherVideos,
                        isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                        onBackClick = onBackClick,
                        onRelatedVideoClick = viewModel::switchToVideo,
                        onCreatePlayerView = { factoryContext ->
                            createPlayerView(
                                context = factoryContext,
                                viewModel = viewModel,
                                appContext = context
                            )
                        },
                        onUpdatePlayerView = { playerView ->
                            bindPlayer(
                                playerView = playerView,
                                viewModel = viewModel,
                                appContext = context
                            )
                        },
                        onReleasePlayerView = { playerView ->
                            playerView.player = null
                        }
                    )
                } else {
                    LandscapeVideoContent(
                        onBackClick = onBackClick,
                        onCreatePlayerView = { factoryContext ->
                            createPlayerView(
                                context = factoryContext,
                                viewModel = viewModel,
                                appContext = context
                            )
                        },
                        onUpdatePlayerView = { playerView ->
                            bindPlayer(
                                playerView = playerView,
                                viewModel = viewModel,
                                appContext = context
                            )
                        },
                        onReleasePlayerView = { playerView ->
                            playerView.player = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PortraitVideoContent(
    video: Video,
    otherVideos: List<Video>,
    isPremiumSubscriptionActive: Boolean,
    onBackClick: () -> Unit,
    onRelatedVideoClick: (Video) -> Unit,
    onCreatePlayerView: (android.content.Context) -> PlayerView,
    onUpdatePlayerView: (PlayerView) -> Unit,
    onReleasePlayerView: (PlayerView) -> Unit
) {
    val premiumBorderBrush = remember {
        Brush.linearGradient(
            colors = listOf(ImioGradientBottom, Purple40, Pink, ImioGradientTop)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImioBackButton(onClick = onBackClick)
            Text(
                text = video.title,
                color = ImioOnBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = PlayerCardOuterShape,
                    ambientColor = Color.Black.copy(alpha = 0.28f),
                    spotColor = Color.Black.copy(alpha = 0.35f)
                )
                .clip(PlayerCardOuterShape)
                .background(premiumBorderBrush)
                .padding(2.dp)
                .clip(PlayerCardInnerShape)
                .background(Color.Black)
                .aspectRatio(16f / 9f)
        ) {
            VideoPlayerView(
                onCreatePlayerView = onCreatePlayerView,
                onUpdatePlayerView = onUpdatePlayerView,
                onReleasePlayerView = onReleasePlayerView,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (otherVideos.isNotEmpty()) {
            Text(
                text = stringResource(R.string.video_related_title),
                color = ImioOnBackground.copy(alpha = 0.72f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = otherVideos,
                    key = { relatedVideo -> relatedVideo.id }
                ) { relatedVideo ->
                    val isPremiumLocked = relatedVideo.isPremium && !isPremiumSubscriptionActive
                    VideoListItem(
                        video = relatedVideo,
                        isFavorite = false,
                        isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                        onVideoClick = {
                            if (!isPremiumLocked) {
                                onRelatedVideoClick(relatedVideo)
                            }
                        },
                        onFavoriteClick = {},
                        size = VideoListItemSize.Compact,
                        showFavoriteButton = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun LandscapeVideoContent(
    onBackClick: () -> Unit,
    onCreatePlayerView: (android.content.Context) -> PlayerView,
    onUpdatePlayerView: (PlayerView) -> Unit,
    onReleasePlayerView: (PlayerView) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        VideoPlayerView(
            onCreatePlayerView = onCreatePlayerView,
            onUpdatePlayerView = onUpdatePlayerView,
            onReleasePlayerView = onReleasePlayerView,
            modifier = Modifier.fillMaxSize()
        )
        ImioBackButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 8.dp, top = 12.dp)
        )
    }
}

@Composable
private fun VideoPlayerView(
    onCreatePlayerView: (android.content.Context) -> PlayerView,
    onUpdatePlayerView: (PlayerView) -> Unit,
    onReleasePlayerView: (PlayerView) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = onCreatePlayerView,
        update = onUpdatePlayerView,
        onRelease = onReleasePlayerView,
        modifier = modifier
    )
}

@UnstableApi
private fun createPlayerView(
    context: android.content.Context,
    viewModel: VideoViewModel,
    appContext: android.content.Context
): PlayerView {
    return PlayerView(context).apply {
        player = viewModel.getOrCreatePlayer(appContext)
        useController = true
        controllerShowTimeoutMs = 3_000
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
    }
}

@UnstableApi
private fun bindPlayer(
    playerView: PlayerView,
    viewModel: VideoViewModel,
    appContext: android.content.Context
) {
    val player = viewModel.getOrCreatePlayer(appContext)
    if (playerView.player != player) {
        playerView.player = player
    }
}
