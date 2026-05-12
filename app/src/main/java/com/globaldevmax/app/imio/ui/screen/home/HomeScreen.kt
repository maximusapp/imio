package com.globaldevmax.app.imio.ui.screen.home

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ScreenContent

@Composable
fun HomeScreen(
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleVideoId = stringResource(R.string.sample_video_id)

    ScreenContent(
        title = stringResource(R.string.home_title),
        description = stringResource(R.string.home_description),
        modifier = modifier
    ) {
        Button(onClick = { onVideoClick(sampleVideoId) }) {
            Text(text = stringResource(R.string.home_open_video))
        }
    }
}
