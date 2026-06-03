package com.globaldevmax.app.imio.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.core.preferences.VideoContentLocale

@Composable
fun videoLocaleDisplayName(locale: String): String {
    return when (locale) {
        VideoContentLocale.UK -> stringResource(R.string.video_locale_ukrainian)
        VideoContentLocale.EN -> stringResource(R.string.video_locale_english)
        else -> locale
    }
}
