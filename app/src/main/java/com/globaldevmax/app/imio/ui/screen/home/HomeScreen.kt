package com.globaldevmax.app.imio.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ImioLoadingIndicator
import com.globaldevmax.app.imio.ui.components.LottieEmptyState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onRetryClick: () -> Unit,
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

    LottieEmptyState(
        animationResId = R.raw.cat_playing_anim,
        message = stringResource(R.string.home_empty_state),
        actionText = stringResource(R.string.action_retry),
        messageTextSize = 20.sp,
        onActionClick = {
            isRetrying = true
            onRetryClick()

            coroutineScope.launch {
                delay(SIMULATED_RETRY_DELAY_MILLIS)
                isRetrying = false
            }
        },
        modifier = modifier
    )
}

private const val SIMULATED_RETRY_DELAY_MILLIS = 3_000L
