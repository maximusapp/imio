package com.globaldevmax.app.imio.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import com.globaldevmax.app.imio.ui.theme.Pink

@Composable
fun ImioBlinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 150.dp,
    textStyle: TextStyle = LocalTextStyle.current,
    containerColor: Color = Pink,
    contentColor: Color = MaterialTheme.colorScheme.secondary,
    shakeEnabled: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "imio_button_animation")
    val buttonPulse by transition.animateFloat(
        initialValue = 0.78f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "imio_button_alpha"
    )
    val shakeOffset by transition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 90),
            repeatMode = RepeatMode.Reverse
        ),
        label = "imio_button_shake"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .width(width)
            .scale(0.98f + buttonPulse * 0.02f)
            .graphicsLayer {
                translationX = if (shakeEnabled) shakeOffset else 0f
            },
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor.copy(alpha = buttonPulse),
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
fun ImioBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = stringResource(R.string.action_back),
            tint = Color.Unspecified,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun ImioPremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current
) {
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .shadow(
                elevation = 18.dp,
                shape = shape,
                ambientColor = Color(0xFF38BDF8),
                spotColor = Color(0xFF38BDF8)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF38BDF8), ImioGradientTop, ImioGradientBottom)
                ),
                shape = shape
            )
            .border(
                border = BorderStroke(2.dp, Color(0xFF67E8F9)),
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = textStyle,
        )
    }
}

