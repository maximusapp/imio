package com.globaldevmax.app.imio.ui.screen.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ScreenContent

@Composable
fun VideoScreen(
    videoId: String,
    modifier: Modifier = Modifier
) {
    ScreenContent(
        title = stringResource(R.string.video_title),
        description = stringResource(R.string.video_description, videoId),
        modifier = modifier
    )
}
