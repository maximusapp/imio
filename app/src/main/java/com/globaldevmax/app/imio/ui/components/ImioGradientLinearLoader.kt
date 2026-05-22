package com.globaldevmax.app.imio.ui.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import com.globaldevmax.app.imio.ui.theme.ImioOnBackground
import com.globaldevmax.app.imio.ui.theme.Pink
import com.globaldevmax.app.imio.ui.theme.Pink40
import com.globaldevmax.app.imio.ui.theme.Purple40

private val LoaderShape = RoundedCornerShape(50)

@Composable
fun ImioGradientLinearLoader(
    modifier: Modifier = Modifier,
    width: Dp = 220.dp,
    height: Dp = 6.dp
) {
    val gradientBrush = remember {
        Brush.horizontalGradient(
            colors = listOf(
                ImioGradientBottom,
                Purple40,
                Pink,
                Pink40
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "gradientLinearLoader")
    val slideProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slideProgress"
    )

    BoxWithConstraints(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(LoaderShape)
            .background(Color.White.copy(alpha = 0.18f))
    ) {
        val segmentWidth = maxWidth * 0.42f
        val travelDistance = maxWidth - segmentWidth

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(segmentWidth)
                .offset(x = travelDistance * slideProgress)
                .clip(LoaderShape)
                .background(gradientBrush)
        )
    }
}
