package com.globaldevmax.app.imio.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import com.globaldevmax.app.imio.ui.theme.ImioOnBackground
import com.globaldevmax.app.imio.ui.theme.Pink
import com.globaldevmax.app.imio.ui.theme.Purple40

enum class VideoListItemSize {
    Default,
    Compact
}

private data class VideoListItemDimensions(
    val previewHeight: Dp,
    val cardCorner: Dp,
    val imageCorner: Dp,
    val cardPadding: Dp,
    val contentPaddingVertical: Dp,
    val shadowElevation: Dp,
    val titleFontSize: TextUnit,
    val durationFontSize: TextUnit,
    val favoriteButtonSize: Dp,
    val favoriteIconSize: Dp,
    val premiumBadgeIconSize: Dp,
    val premiumLockedIconSize: Dp,
    val premiumLockedLabelFontSize: TextUnit,
    val premiumLockedMessageFontSize: TextUnit,
    val premiumLockedPadding: Dp
)

private fun VideoListItemSize.dimensions(): VideoListItemDimensions = when (this) {
    VideoListItemSize.Default -> VideoListItemDimensions(
        previewHeight = 176.dp,
        cardCorner = 22.dp,
        imageCorner = 18.dp,
        cardPadding = 10.dp,
        contentPaddingVertical = 10.dp,
        shadowElevation = 8.dp,
        titleFontSize = 17.sp,
        durationFontSize = 17.sp,
        favoriteButtonSize = 40.dp,
        favoriteIconSize = 26.dp,
        premiumBadgeIconSize = 40.dp,
        premiumLockedIconSize = 28.dp,
        premiumLockedLabelFontSize = 18.sp,
        premiumLockedMessageFontSize = 14.sp,
        premiumLockedPadding = 20.dp
    )
    VideoListItemSize.Compact -> VideoListItemDimensions(
        previewHeight = 88.dp,
        cardCorner = 11.dp,
        imageCorner = 9.dp,
        cardPadding = 5.dp,
        contentPaddingVertical = 5.dp,
        shadowElevation = 4.dp,
        titleFontSize = 14.sp,
        durationFontSize = 12.sp,
        favoriteButtonSize = 28.dp,
        favoriteIconSize = 18.dp,
        premiumBadgeIconSize = 20.dp,
        premiumLockedIconSize = 18.dp,
        premiumLockedLabelFontSize = 13.sp,
        premiumLockedMessageFontSize = 11.sp,
        premiumLockedPadding = 10.dp
    )
}

@Composable
fun VideoListItem(
    video: Video,
    isFavorite: Boolean,
    isPremiumSubscriptionActive: Boolean,
    onVideoClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: VideoListItemSize = VideoListItemSize.Default,
    showFavoriteButton: Boolean = true
) {
    val isPremiumLocked = video.isPremium && !isPremiumSubscriptionActive
    val dimensions = size.dimensions()
    val cardShape = RoundedCornerShape(dimensions.cardCorner)
    val imageShape = RoundedCornerShape(dimensions.imageCorner)
    val premiumGradient = remember {
        Brush.linearGradient(
            colors = listOf(
                ImioGradientBottom.copy(alpha = 0.7f),
                Purple40.copy(alpha = 0.7f),
                Pink.copy(alpha = 0.7f),
                ImioGradientTop.copy(alpha = 0.7f)
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimensions.shadowElevation,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.25f)
            )
            .clip(cardShape)
            .background(Color.White.copy(alpha = if (size == VideoListItemSize.Compact) 0.92f else 1f))
            .then(
                if (!isPremiumLocked) {
                    Modifier.clickable(onClick = onVideoClick)
                } else {
                    Modifier
                }
            )
            .padding(
                start = dimensions.cardPadding,
                end = dimensions.cardPadding,
                top = dimensions.cardPadding
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.previewHeight)
                .clip(imageShape)
        ) {
            if (video.previewImageUrl.isNotBlank()) {
                Log.d("previewImageUrl","video.previewImageUrl: ${video.previewImageUrl}")
                SubcomposeAsyncImage(
                    model = video.previewImageUrl,
                    contentDescription = video.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE2E8F0)),
                    loading = {
                        VideoPreviewPlaceholder(
                            isPremiumLocked = isPremiumLocked,
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    error = {
                        VideoPreviewPlaceholder(
                            isPremiumLocked = isPremiumLocked,
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    success = { state ->
                        Image(
                            painter = state.painter,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(if (isPremiumLocked) 0.55f else 1f)
                        )
                    }
                )
            } else {
                VideoPreviewPlaceholder(
                    isPremiumLocked = isPremiumLocked,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (isPremiumLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.32f))
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = dimensions.premiumLockedPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(if (size == VideoListItemSize.Compact) 4.dp else 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(if (size == VideoListItemSize.Compact) 8.dp else 14.dp))
                            .background(premiumGradient)
                            .padding(
                                horizontal = if (size == VideoListItemSize.Compact) 8.dp else 14.dp,
                                vertical = if (size == VideoListItemSize.Compact) 4.dp else 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(if (size == VideoListItemSize.Compact) 4.dp else 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_premium),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(dimensions.premiumLockedIconSize)
                        )
                        Text(
                            text = stringResource(R.string.home_video_premium_locked_label),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = dimensions.premiumLockedLabelFontSize
                        )
                    }
                    if (size == VideoListItemSize.Default) {
                        Text(
                            text = stringResource(R.string.home_video_premium_locked_message),
                            color = ImioOnBackground,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = dimensions.premiumLockedMessageFontSize,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            if (video.isPremium && !isPremiumLocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(if (size == VideoListItemSize.Compact) 6.dp else 10.dp))
                        .background(Color(0xE0FFFFFF))
                        .padding(if (size == VideoListItemSize.Compact) 3.dp else 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_premium),
                        contentDescription = stringResource(R.string.home_video_premium_badge),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(dimensions.premiumBadgeIconSize)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = dimensions.contentPaddingVertical),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = video.title,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    fontSize = dimensions.titleFontSize,
                    maxLines = if (size == VideoListItemSize.Compact) 1 else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (showFavoriteButton) {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(dimensions.favoriteButtonSize)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_favorite),
                            contentDescription = stringResource(
                                if (isFavorite) {
                                    R.string.home_video_remove_favorite
                                } else {
                                    R.string.home_video_add_favorite
                                }
                            ),
                            tint = if (isFavorite) Color.Unspecified else Color(0xFF94A3B8),
                            modifier = Modifier.size(dimensions.favoriteIconSize)
                        )
                    }
                }
                if (video.durationMs > 0L) {
                    Text(
                        text = formatVideoDuration(
                            durationMs = video.durationMs,
                            hoursMinutesFormat = stringResource(R.string.home_video_duration_hours_minutes),
                            hoursFormat = stringResource(R.string.home_video_duration_hours),
                            minutesFormat = stringResource(R.string.home_video_duration_minutes)
                        ),
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = dimensions.durationFontSize,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoPreviewPlaceholder(
    isPremiumLocked: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .alpha(if (isPremiumLocked) 0.55f else 1f)
            .background(Color(0xFFE2E8F0)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.home_video_preview_placeholder),
            color = Color(0xFF64748B),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun formatVideoDuration(
    durationMs: Long,
    hoursMinutesFormat: String,
    hoursFormat: String,
    minutesFormat: String
): String {
    val totalMinutes = (durationMs / 60_000).coerceAtLeast(0)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> hoursMinutesFormat.format(hours, minutes)
        hours > 0 -> hoursFormat.format(hours)
        minutes > 0 -> minutesFormat.format(minutes)
        else -> minutesFormat.format(0)
    }
}

