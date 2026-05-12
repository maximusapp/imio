package com.globaldevmax.app.imio.ui.screen.favorite

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ScreenContent

@Composable
fun FavoriteScreen(modifier: Modifier = Modifier) {
    ScreenContent(
        title = stringResource(R.string.favorite_title),
        description = stringResource(R.string.favorite_description),
        modifier = modifier
    )
}
