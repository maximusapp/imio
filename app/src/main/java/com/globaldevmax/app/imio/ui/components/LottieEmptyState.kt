package com.globaldevmax.app.imio.ui.components

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieEmptyState(
    @RawRes animationResId: Int,
    message: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    messageTextSize: TextUnit = TextUnit.Unspecified,
    buttonShakeEnable: Boolean = false,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(animationResId)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = message,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = style,
            fontSize = messageTextSize
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))

            ImioBlinkButton(
                text = actionText,
                onClick = onActionClick,
                textStyle = style,
                shakeEnabled = buttonShakeEnable
            )
        }
    }
}
