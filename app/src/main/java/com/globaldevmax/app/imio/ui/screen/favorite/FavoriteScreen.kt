package com.globaldevmax.app.imio.ui.screen.favorite

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.LottieEmptyState

@Composable
fun FavoriteScreen(modifier: Modifier = Modifier) {
    LottieEmptyState(
        animationResId = R.raw.cute_tiger,
        message = stringResource(R.string.favorite_empty_state),
        messageTextSize = 20.sp,
        modifier = modifier
    )
}
