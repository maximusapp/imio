package com.globaldevmax.app.imio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import com.globaldevmax.app.imio.ui.theme.Pink
import com.globaldevmax.app.imio.ui.theme.Purple40

@Composable
fun VideoListItem(
    video: Video,
    isFavorite: Boolean,
    onVideoClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(22.dp)
    val imageShape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.25f)
            )
            .clip(cardShape)
            .background(Color.White)
            .clickable(onClick = onVideoClick)
            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(VIDEO_PREVIEW_HEIGHT)
                .clip(imageShape)
        ) {
            if (video.previewImageUrl.isNotBlank()) {
                AsyncImage(
                    model = video.previewImageUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE2E8F0))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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

            if (video.isPremium) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(listOf(ImioGradientBottom, Purple40, Pink, ImioGradientTop)))
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_premium),
                        contentDescription = stringResource(R.string.home_video_premium_badge),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 10.dp),
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
                    fontSize = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(40.dp)
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
                        modifier = Modifier.size(26.dp)
                    )
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
                        fontSize = 17.sp,
                        maxLines = 1
                    )
                }
            }
        }
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

private val VIDEO_PREVIEW_HEIGHT = 176.dp
