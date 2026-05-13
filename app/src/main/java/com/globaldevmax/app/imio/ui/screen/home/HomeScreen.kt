package com.globaldevmax.app.imio.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ImioLoadingIndicator
import com.globaldevmax.app.imio.ui.components.LottieEmptyState
import com.globaldevmax.app.imio.ui.components.LottieIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onRetryClick: () -> Unit,
    hasActiveNotification: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isRetrying by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (isRetrying) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ImioLoadingIndicator()
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        LottieIcon(
            animationResId = R.raw.bell_notification,
            isPlaying = hasActiveNotification,
            isGrayscale = !hasActiveNotification,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 30.dp, end = 20.dp)
                .size(50.dp)
        )
        LottieEmptyState(
            animationResId = R.raw.cat_playing_anim,
            message = stringResource(R.string.home_empty_state),
            actionText = stringResource(R.string.action_retry),
            messageTextSize = 20.sp,
            buttonShakeEnable = true,
            onActionClick = {
                isRetrying = true
                onRetryClick()

                coroutineScope.launch {
                    delay(SIMULATED_RETRY_DELAY_MILLIS)
                    isRetrying = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private const val SIMULATED_RETRY_DELAY_MILLIS = 3_000L
