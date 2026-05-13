package com.globaldevmax.app.imio.ui.components

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.Modifier
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieIcon(
    @RawRes animationResId: Int,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    isGrayscale: Boolean = false
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(animationResId)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = SimpleColorFilter(Color.Gray.toArgb()),
            keyPath = arrayOf("**")
        )
    )

    LottieAnimation(
        composition = composition,
        progress = { if (isPlaying) progress else 0f },
        dynamicProperties = if (isGrayscale) dynamicProperties else null,
        modifier = modifier
    )
}
