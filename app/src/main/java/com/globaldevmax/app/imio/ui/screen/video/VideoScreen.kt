package com.globaldevmax.app.imio.ui.screen.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ImioBackButton
import com.globaldevmax.app.imio.ui.components.ScreenContent

@Composable
fun VideoScreen(
    videoId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        ImioBackButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 28.dp)
        )
        ScreenContent(
            title = stringResource(R.string.video_title),
            description = stringResource(R.string.video_description, videoId),
            modifier = Modifier.fillMaxSize()
        )
    }
}
