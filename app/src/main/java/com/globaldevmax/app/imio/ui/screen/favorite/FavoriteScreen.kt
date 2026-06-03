package com.globaldevmax.app.imio.ui.screen.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.ui.ads.ImioBannerAd
import com.globaldevmax.app.imio.ui.components.LottieEmptyState
import com.globaldevmax.app.imio.ui.components.VideoListItem
import com.globaldevmax.app.imio.ui.theme.ImioOnBackground
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 20.dp)
        ) {
            ImioBannerAd(
                adUnitId = stringResource(R.string.ad_unit_favorite_banner),
                showAds = !isPremiumSubscriptionActive,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))

        when (val state = uiState) {
            is FavoriteUiState.Empty -> {
                LottieEmptyState(
                    animationResId = R.raw.cute_tiger,
                    message = stringResource(
                        if (state.isEveningModeActive) {
                            R.string.evening_mode_no_videos
                        } else {
                            R.string.favorite_empty_state
                        }
                    ),
                    messageTextSize = 20.sp,
                    modifier = Modifier.fillMaxSize()
                )
            }

            is FavoriteUiState.Success -> {
                val displayedVideos = state.videos

                if (displayedVideos.isEmpty()) {
                    Text(
                        text = stringResource(R.string.favorite_empty_state),
                        style = MaterialTheme.typography.bodyLarge,
                        color = ImioOnBackground.copy(alpha = 0.88f),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        items(
                            items = displayedVideos,
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
