package com.globaldevmax.app.imio.ui.screen.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.ui.ads.ImioBannerAd
import com.globaldevmax.app.imio.ui.components.HomeVideoSearchBar
import com.globaldevmax.app.imio.ui.components.ImioLoadingIndicator
import com.globaldevmax.app.imio.ui.components.LottieEmptyState
import com.globaldevmax.app.imio.ui.components.ShowPremiumVideosToggle
import com.globaldevmax.app.imio.ui.components.VideoListItem
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import com.globaldevmax.app.imio.ui.theme.ImioOnBackground
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    isPremiumSubscriptionActive: Boolean,
    onVideoClick: (Video) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.clearSearch()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refreshVideos,
        state = pullToRefreshState,
        modifier = modifier.fillMaxSize(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                state = pullToRefreshState,
                color = ImioOnBackground,
                containerColor = ImioGradientTop.copy(alpha = 0.35f)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            when (val state = uiState) {
                SearchUiState.Loading -> {
                    ImioLoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                SearchUiState.Empty -> {
                    LottieEmptyState(
                        animationResId = R.raw.cat_playing_anim,
                        message = stringResource(R.string.search_empty_catalog),
                        actionText = stringResource(R.string.action_retry),
                        messageTextSize = 20.sp,
                        buttonShakeEnable = true,
                        onActionClick = viewModel::loadVideos,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is SearchUiState.Error -> {
                    LottieEmptyState(
                        animationResId = R.raw.cat_playing_anim,
                        message = state.message.ifBlank {
                            stringResource(R.string.search_load_error)
                        },
                        actionText = stringResource(R.string.action_retry),
                        messageTextSize = 20.sp,
                        buttonShakeEnable = true,
                        onActionClick = viewModel::loadVideos,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is SearchUiState.Ready -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .padding(top = 12.dp, bottom = 20.dp)
                    ) {
                        HomeVideoSearchBar(
                            query = state.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChange,
                            onClearClick = viewModel::clearSearch,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (!isPremiumSubscriptionActive) {
                            ShowPremiumVideosToggle(
                                showPremiumVideos = state.showPremiumVideos,
                                onShowPremiumVideosChange = viewModel::setShowPremiumVideos,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = if (isPremiumSubscriptionActive) 14.dp else 6.dp),
                            contentPadding = PaddingValues(bottom = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            if (state.displayedVideos.isEmpty()) {
                                item(key = "search_results_empty") {
                                    Text(
                                        text = stringResource(
                                            when {
                                                state.isEveningModeActive && state.isSearchActive ->
                                                    R.string.evening_mode_no_videos
                                                state.isSearchActive ->
                                                    R.string.home_search_empty
                                                state.isEveningModeActive ->
                                                    R.string.evening_mode_no_videos
                                                else ->
                                                    R.string.home_search_empty
                                            }
                                        ),
                                        color = ImioOnBackground.copy(alpha = 0.88f),
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 32.dp, bottom = 16.dp)
                                    )
                                }
                            } else {
                                val videos = state.displayedVideos
                                item(key = "search_video_0") {
                                    val video = videos.first()
                                    VideoListItem(
                                        video = video,
                                        isFavorite = video.id in favoriteIds,
                                        isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                                        preferredLocale = state.contentLocale,
                                        onVideoClick = {
                                            if (!video.isPremium || isPremiumSubscriptionActive) {
                                                onVideoClick(video)
                                            }
                                        },
                                        onFavoriteClick = { viewModel.toggleFavorite(video) }
                                    )
                                }

                                item(key = "search_banner_after_1") {
                                    ImioBannerAd(
                                        adUnitId = stringResource(R.string.ad_unit_search_banner),
                                        showAds = !isPremiumSubscriptionActive,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                items(
                                    items = videos.drop(1),
                                    key = { video -> video.id }
                                ) { video ->
                                    VideoListItem(
                                        video = video,
                                        isFavorite = video.id in favoriteIds,
                                        isPremiumSubscriptionActive = isPremiumSubscriptionActive,
                                        preferredLocale = state.contentLocale,
                                        onVideoClick = {
                                            if (!video.isPremium || isPremiumSubscriptionActive) {
                                                onVideoClick(video)
                                            }
                                        },
                                        onFavoriteClick = { viewModel.toggleFavorite(video) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
