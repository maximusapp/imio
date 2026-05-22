package com.globaldevmax.app.imio.ui.screen.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.ui.components.LottieEmptyState
import com.globaldevmax.app.imio.ui.components.VideoListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteScreen(
    isPremiumSubscriptionActive: Boolean,
    onVideoClick: (Video) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        when (val state = uiState) {
            FavoriteUiState.Empty -> {
                LottieEmptyState(
                    animationResId = R.raw.cute_tiger,
                    message = stringResource(R.string.favorite_empty_state),
                    messageTextSize = 20.sp,
                    modifier = Modifier.fillMaxSize()
                )
            }

            is FavoriteUiState.Success -> {
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
                    items(
                        items = state.videos,
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
