package com.globaldevmax.app.imio.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.ui.components.ImioLoadingIndicator
import com.globaldevmax.app.imio.ui.components.LottieEmptyState
import com.globaldevmax.app.imio.ui.components.VideoListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onVideoClick: (Video) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
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
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 12.dp,
                        bottom = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    items(
                        items = state.videos,
                        key = { video -> video.id }
                    ) { video ->
                        VideoListItem(
                            video = video,
                            isFavorite = video.id in favoriteIds,
                            onVideoClick = { onVideoClick(video) },
                            onFavoriteClick = { viewModel.toggleFavorite(video) }
                        )
                    }
                }
            }
        }
    }
}
