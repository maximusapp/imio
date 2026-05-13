package com.globaldevmax.app.imio.ui.screen.privacy

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.globaldevmax.app.imio.R

@Composable
fun PrivacyPolicyScreen(
    url: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.privacy_policy_loading),
            modifier = Modifier.align(Alignment.Center)
        )
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = false
                    loadUrl(url)
                }
            },
            update = { webView ->
                if (webView.url != url) {
                    webView.loadUrl(url)
                }
            }
        )
    }
}
