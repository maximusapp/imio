package com.globaldevmax.app.imio.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ScreenContent

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    ScreenContent(
        title = stringResource(R.string.profile_title),
        description = stringResource(R.string.profile_description),
        modifier = modifier
    )
}
