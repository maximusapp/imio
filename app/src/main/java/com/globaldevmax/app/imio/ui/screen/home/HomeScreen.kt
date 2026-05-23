package com.globaldevmax.app.imio.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.ui.components.HomeVideoFilterBar
import com.globaldevmax.app.imio.ui.components.ImioLoadingIndicator
import com.globaldevmax.app.imio.ui.components.LottieEmptyState
import com.globaldevmax.app.imio.ui.components.VideoListItem
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import com.globaldevmax.app.imio.ui.theme.ImioOnBackground
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isPremiumSubscriptionActive: Boolean,
    onVideoClick: (Video) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

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
                HomeUiState.Loading -> {
                    ImioLoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                HomeUiState.Empty -> {
                    LottieEmptyState(
                        animationResId = R.raw.cat_playing_anim,
                        message = stringResource(R.string.home_empty_state),
                        actionText = stringResource(R.string.action_retry),
                        messageTextSize = 20.sp,
                        buttonShakeEnable = true,
                        onActionClick = viewModel::loadVideos,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is HomeUiState.Error -> {
                    LottieEmptyState(
                        animationResId = R.raw.cat_playing_anim,
                        message = state.message.ifBlank {
                            stringResource(R.string.home_load_error)
                        },
                        actionText = stringResource(R.string.action_retry),
                        messageTextSize = 20.sp,
                        buttonShakeEnable = true,
                        onActionClick = viewModel::loadVideos,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is HomeUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 12.dp,
                            bottom = 20.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        item(key = "video_filter") {
                            HomeVideoFilterBar(
                                selectedFilter = state.selectedFilter,
                                filterCounts = state.filterCounts,
                                onFilterSelected = viewModel::onFilterSelected,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (state.displayedVideos.isEmpty()) {
                            item(key = "video_filter_empty") {
                                Text(
                                    text = stringResource(R.string.home_filter_empty),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = ImioOnBackground.copy(alpha = 0.88f),
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 32.dp, bottom = 16.dp)
                                )
                            }
                        } else {
                            items(
                                items = state.displayedVideos,
                                key = { video -> video.id }
                            ) { video ->
                                VideoListItem(
                                    video = video,
                                    isFavorite = video.id in favoriteIds,
                                    isPremiumSubscriptionActive = isPremiumSubscriptionActive,
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
