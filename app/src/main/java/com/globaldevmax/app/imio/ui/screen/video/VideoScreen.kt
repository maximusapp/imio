package com.globaldevmax.app.imio.ui.screen.video

import android.app.Activity
import android.content.res.Configuration
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.res.painterResource
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
import androidx.media3.ui.R as Media3UiR
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.ui.ads.ImioBannerAd
import com.globaldevmax.app.imio.ui.components.ImioBackButton
import com.globaldevmax.app.imio.ui.components.ImioLoadingIndicator
import com.globaldevmax.app.imio.ui.components.VideoListItem
import com.globaldevmax.app.imio.ui.components.VideoListItemSize
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import com.globaldevmax.app.imio.ui.theme.ImioOnBackground
import com.globaldevmax.app.imio.ui.theme.Pink
import com.globaldevmax.app.imio.ui.theme.Purple40
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val PlayerCardOuterShape = RoundedCornerShape(24.dp)
private val PlayerCardInnerShape = RoundedCornerShape(22.dp)

@androidx.annotation.OptIn(UnstableApi::class)
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
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    var isExiting by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner, uiState) {
        if (uiState !is VideoUiState.Ready) {
            return@DisposableEffect onDispose { }
        }
        val observer = LifecycleEventObserver { _, event ->
            if (activity?.isChangingConfigurations == true) return@LifecycleEventObserver
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                viewModel.pausePlayback()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(isExiting) {
        if (isExiting) {
            // Ensure player is fully stopped before we reveal the previous screen.
            viewModel.releasePlayer()
            onBackClick()
        }
    }

    val handleBack: () -> Unit = {
        if (!isExiting) {
            isExiting = true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            VideoUiState.Loading -> {
                ImioBackButton(
                    onClick = handleBack,
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
                    onClick = handleBack,
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
                val isLandscape =
                    LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                var arePlayerControlsVisible by remember { mutableStateOf(false) }
                val onControllerVisibilityChanged: (Boolean) -> Unit = { visible ->
                    arePlayerControlsVisible = visible
                }
                val onCreatePlayer: (android.content.Context) -> PlayerView = { factoryContext ->
                    buildPlayerView(
                        context = factoryContext,
                        viewModel = viewModel,
                        appContext = context,
                        landscapeFullscreen = isLandscape,
                        onControllerVisibilityChanged = onControllerVisibilityChanged
                    )
                }
                val onUpdatePlayer: (PlayerView) -> Unit = { playerView ->
                    bindPlayer(
                        playerView = playerView,
                        viewModel = viewModel,
                        appContext = context,
                        landscapeFullscreen = isLandscape,
                        onControllerVisibilityChanged = onControllerVisibilityChanged
                    )
                }
                val onReleasePlayer: (PlayerView) -> Unit = { playerView ->
                    detachPlayerView(playerView)
                }

                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    PortraitVideoContent(
                        video = state.video,
                        otherVideos = state.otherVideos,
                        isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                        onBackClick = handleBack,
                        onRelatedVideoClick = viewModel::switchToVideo,
                        onCreatePlayerView = onCreatePlayer,
                        onUpdatePlayerView = onUpdatePlayer,
                        onReleasePlayerView = onReleasePlayer
                    )
                } else {
                    LandscapeVideoContent(
                        otherVideos = state.otherVideos,
                        isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                        showPlayerChrome = arePlayerControlsVisible,
                        onBackClick = handleBack,
                        onRelatedVideoClick = viewModel::switchToVideo,
                        onCreatePlayerView = onCreatePlayer,
                        onUpdatePlayerView = onUpdatePlayer,
                        onReleasePlayerView = onReleasePlayer
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

        VideoPlayerCard(
            premiumBorderBrush = premiumBorderBrush,
            onCreatePlayerView = onCreatePlayerView,
            onUpdatePlayerView = onUpdatePlayerView,
            onReleasePlayerView = onReleasePlayerView
        )

        ImioBannerAd(
            adUnitId = stringResource(R.string.ad_unit_video_banner),
            showAds = !isPremiumSubscriptionActive,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )

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
private fun VideoPlayerCard(
    premiumBorderBrush: Brush,
    onCreatePlayerView: (android.content.Context) -> PlayerView,
    onUpdatePlayerView: (PlayerView) -> Unit,
    onReleasePlayerView: (PlayerView) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
}

@Composable
private fun LandscapeVideoContent(
    otherVideos: List<Video>,
    isPremiumSubscriptionActive: Boolean,
    showPlayerChrome: Boolean,
    onBackClick: () -> Unit,
    onRelatedVideoClick: (Video) -> Unit,
    onCreatePlayerView: (android.content.Context) -> PlayerView,
    onUpdatePlayerView: (PlayerView) -> Unit,
    onReleasePlayerView: (PlayerView) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            VideoPlayerView(
                onCreatePlayerView = onCreatePlayerView,
                onUpdatePlayerView = onUpdatePlayerView,
                onReleasePlayerView = onReleasePlayerView,
                modifier = Modifier.fillMaxSize()
            )

            if (showPlayerChrome) {
                ImioBackButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(start = 4.dp, top = 4.dp)
                )
            }
        }

        if (showPlayerChrome && otherVideos.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(Color.Black.copy(alpha = 0.88f))
                    .padding(vertical = 10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = otherVideos,
                    key = { relatedVideo -> relatedVideo.id }
                ) { relatedVideo ->
                    val isPremiumLocked = relatedVideo.isPremium && !isPremiumSubscriptionActive
                    LandscapeRelatedVideoItem(
                        video = relatedVideo,
                        isPremiumLocked = isPremiumLocked,
                        onClick = {
                            if (!isPremiumLocked) {
                                onRelatedVideoClick(relatedVideo)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LandscapeRelatedVideoItem(
    video: Video,
    isPremiumLocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbnailShape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier
            .width(132.dp)
            .then(
                if (!isPremiumLocked) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .clip(thumbnailShape)
                .background(Color(0xFF1E293B))
        ) {
            if (video.previewImageUrl.isNotBlank()) {
                AsyncImage(
                    model = video.previewImageUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isPremiumLocked) 0.5f else 1f)
                )
            }
            if (isPremiumLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_video_premium_locked_label),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            } else if (video.isPremium) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xE0FFFFFF))
                        .padding(3.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_premium),
                        contentDescription = stringResource(R.string.home_video_premium_badge),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Text(
            text = video.title,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 15.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp)
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
private fun buildPlayerView(
    context: android.content.Context,
    viewModel: VideoViewModel,
    appContext: android.content.Context,
    landscapeFullscreen: Boolean,
    onControllerVisibilityChanged: (Boolean) -> Unit
): PlayerView {
    return PlayerView(context).apply {
        player = viewModel.getOrCreatePlayer(appContext)
        useController = true
        controllerShowTimeoutMs = 10_000
        resizeMode = playerResizeMode(landscapeFullscreen)
        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
        setControllerVisibilityListener(
            PlayerView.ControllerVisibilityListener { visibility ->
                onControllerVisibilityChanged(visibility == View.VISIBLE)
            }
        )
        post { hideSettingsButton() }
    }
}

@UnstableApi
private fun bindPlayer(
    playerView: PlayerView,
    viewModel: VideoViewModel,
    appContext: android.content.Context,
    landscapeFullscreen: Boolean,
    onControllerVisibilityChanged: (Boolean) -> Unit
) {
    val player = viewModel.getOrCreatePlayer(appContext)
    if (playerView.player != player) {
        playerView.player = player
    }
    playerView.resizeMode = playerResizeMode(landscapeFullscreen)
    playerView.setControllerVisibilityListener(
        PlayerView.ControllerVisibilityListener { visibility ->
            onControllerVisibilityChanged(visibility == View.VISIBLE)
        }
    )
    playerView.post { playerView.hideSettingsButton() }
}

@UnstableApi
private fun detachPlayerView(playerView: PlayerView) {
    playerView.setControllerVisibilityListener(null as PlayerView.ControllerVisibilityListener?)
    playerView.player = null
}

private fun PlayerView.hideSettingsButton() {
    findViewById<View>(Media3UiR.id.exo_settings)?.visibility = View.GONE
}

@UnstableApi
private fun playerResizeMode(landscapeFullscreen: Boolean): Int =
    if (landscapeFullscreen) {
        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    } else {
        AspectRatioFrameLayout.RESIZE_MODE_FIT
    }
