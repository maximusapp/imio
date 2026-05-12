package com.globaldevmax.app.imio.ui.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ImioLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = LinearEasing
            )
        ),
        label = ""
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val colors = listOf(
        Color(0xFFFFD54F),
        Color(0xFFFF8A65),
        Color(0xFFFF5EEA),
        Color(0xFF7C4DFF),
        Color(0xFF40C4FF),
        Color(0xFF64FFDA),
        Color(0xFFB2FF59),
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = rotation
                    scaleX = pulse
                    scaleY = pulse
                }
        ) {

            val strokeWidth = 15.dp.toPx()
            val radius = minOf(this.size.width, this.size.height) / 3.0f

            for (i in colors.indices) {

                drawArc(
                    brush = Brush.sweepGradient(colors),
                    startAngle = i * 50f,
                    sweepAngle = 20f,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    ),
                    size = Size(
                        radius * 2,
                        radius * 2
                    ),
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    )
                )
            }
        }

//        Text(
//            text = stringResource(R.string.app_name),
//            fontSize = 28.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.White,
//            modifier = Modifier.graphicsLayer {
//                scaleX = pulse
//                scaleY = pulse
//            }
//        )
    }
}