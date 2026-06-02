package com.globaldevmax.app.imio.ui.screen.privacy

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.network.connectivity.ConnectivityChecker
import com.globaldevmax.app.imio.ui.components.ImioTopHeader
import org.koin.compose.koinInject

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    connectivityChecker: ConnectivityChecker = koinInject()
) {
    val remoteUrl = stringResource(R.string.privacy_policy_url)
    val assetFileName = stringResource(R.string.privacy_policy_asset_file)
    val localAssetUrl = "file:///android_asset/$assetFileName"

    var pageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(remoteUrl, localAssetUrl) {
        isLoading = true
        pageUrl = if (connectivityChecker.isOnline()) {
            remoteUrl
        } else {
            localAssetUrl
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ImioTopHeader(
            title = stringResource(R.string.privacy_policy_title),
            onBackClick = onBackClick
        )

        if (isLoading) {
            Text(
                text = stringResource(R.string.privacy_policy_loading),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        pageUrl?.let { url ->
            key(url) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 86.dp),
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(
                                    view: WebView?,
                                    startedUrl: String?,
                                    favicon: Bitmap?
                                ) {
                                    isLoading = true
                                }

                                override fun onPageFinished(view: WebView?, finishedUrl: String?) {
                                    isLoading = false
                                }

                                @Suppress("DEPRECATION")
                                override fun onReceivedError(
                                    view: WebView,
                                    request: WebResourceRequest,
                                    error: WebResourceError
                                ) {
                                    if (request.isForMainFrame && url == remoteUrl) {
                                        view.loadUrl(localAssetUrl)
                                    }
                                }
                            }
                            settings.javaScriptEnabled = false
                            loadUrl(url)
                        }
                    }
                )
            }
        }
    }
}
