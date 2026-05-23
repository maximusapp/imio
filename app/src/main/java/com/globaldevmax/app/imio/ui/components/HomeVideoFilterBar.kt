package com.globaldevmax.app.imio.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.screen.home.VideoFilter
import com.globaldevmax.app.imio.ui.screen.home.VideoFilterCounts
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import com.globaldevmax.app.imio.ui.theme.ImioOnBackground
import com.globaldevmax.app.imio.ui.theme.Pink
import com.globaldevmax.app.imio.ui.theme.Purple40

private val FilterBarShape = RoundedCornerShape(22.dp)
private val SegmentShape = RoundedCornerShape(18.dp)
private val CountBadgeShape = RoundedCornerShape(10.dp)

@Composable
fun HomeVideoFilterBar(
    selectedFilter: VideoFilter,
    filterCounts: VideoFilterCounts,
    onFilterSelected: (VideoFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val premiumGradient = remember {
        Brush.linearGradient(colors = listOf(ImioGradientBottom, Purple40, Pink, ImioGradientTop))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = FilterBarShape,
                ambientColor = Color.Black.copy(alpha = 0.18f),
                spotColor = Color.Black.copy(alpha = 0.22f)
            )
            .clip(FilterBarShape)
            .background(Color.White.copy(alpha = 0.14f))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            VideoFilter.entries.forEach { filter ->
                FilterSegment(
                    filter = filter,
                    count = filterCounts.countFor(filter),
                    selected = filter == selectedFilter,
                    premiumGradient = premiumGradient,
                    onClick = { onFilterSelected(filter) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FilterSegment(
    filter: VideoFilter,
    count: Int,
    selected: Boolean,
    premiumGradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPremiumSegment = filter == VideoFilter.PREMIUM
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.98f,
        animationSpec = tween(durationMillis = 180),
        label = "filterSegmentScale"
    )
    val labelColor by animateColorAsState(
        targetValue = when {
            selected && isPremiumSegment -> Color.White
            selected -> ImioGradientBottom
            else -> ImioOnBackground.copy(alpha = 0.78f)
        },
        animationSpec = tween(durationMillis = 200),
        label = "filterSegmentLabel"
    )

    Box(
        modifier = modifier
            .height(58.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(SegmentShape)
            .then(
                if (selected && isPremiumSegment) {
                    Modifier.background(premiumGradient)
                } else if (selected) {
                    Modifier
                        .shadow(
                            elevation = 2.dp,
                            shape = SegmentShape,
                            ambientColor = Color.Black.copy(alpha = 0.08f),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        )
                        .background(Color.White, SegmentShape)
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPremiumSegment) {
                    Icon(
                        painter = painterResource(R.drawable.ic_premium),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(18.dp)
                            .alpha(if (selected) 1f else 0.72f)
                    )
                }
                Text(
                    text = filter.label(),
                    color = labelColor,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
            FilterCountBadge(
                count = count,
                selected = selected,
                isPremiumSegment = isPremiumSegment
            )
        }
    }
}

@Composable
private fun FilterCountBadge(
    count: Int,
    selected: Boolean,
    isPremiumSegment: Boolean,
) {
    val countColor by animateColorAsState(
        targetValue = when {
            selected && isPremiumSegment -> Color.White
            selected -> ImioGradientBottom
            else -> ImioOnBackground.copy(alpha = 0.65f)
        },
        animationSpec = tween(durationMillis = 200),
        label = "filterCountText"
    )
    val badgeModifier = when {
        selected && isPremiumSegment -> Modifier.background(Color.White.copy(alpha = 0.22f))
        selected -> Modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(
                    ImioGradientBottom.copy(alpha = 0.14f),
                    ImioGradientTop.copy(alpha = 0.18f)
                )
            )
        )
        else -> Modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.1f),
                    Color.White.copy(alpha = 0.06f)
                )
            )
        )
    }

    Box(
        modifier = Modifier
            .clip(CountBadgeShape)
            .then(badgeModifier)
            .padding(horizontal = 9.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = countColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            lineHeight = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun VideoFilter.label(): String = when (this) {
    VideoFilter.ALL -> stringResource(R.string.home_filter_all)
    VideoFilter.PREMIUM -> stringResource(R.string.home_filter_premium)
    VideoFilter.STANDARD -> stringResource(R.string.home_filter_standard)
}
