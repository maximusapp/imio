package com.globaldevmax.app.imio.ui.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.globaldevmax.app.imio.BuildConfig
import com.globaldevmax.app.imio.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun ImioBannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier,
    showAds: Boolean = true
) {
    if (!showAds) return

    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val resolvedAdUnitId = if (BuildConfig.DEBUG) {
        stringResource(R.string.ad_unit_test_banner)
    } else {
        adUnitId
    }

    // Avoid preview crashes and accidental requests in tooling.
    if (isPreview) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .background(Color.Transparent)
        )
        return
    }

    // Keep a stable size across orientation changes.
    key(resolvedAdUnitId) {
        val adView = remember {
            AdView(context).apply {
                this.adUnitId = resolvedAdUnitId
                setAdSize(AdSize.BANNER)
                loadAd(AdRequest.Builder().build())
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                adView.destroy()
            }
        }

        AndroidView(
            factory = { adView },
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
        )
    }
}
