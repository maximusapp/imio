package com.globaldevmax.app.imio.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Logs failed KeepData media responses in debug builds (preview images, HLS segments).
 */
class KeepDataMediaLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (request.url.host.contains(KEEPDATA_HOST_MARKER) &&
            !response.isSuccessful &&
            isMediaPath(request.url.encodedPath)
        ) {
            Log.w(
                TAG,
                "HTTP ${response.code} ${response.message} for ${request.url} (Content-Type: ${response.header("Content-Type")})"
            )
        }
        return response
    }

    private fun isMediaPath(path: String): Boolean {
        return path.contains("placeholder", ignoreCase = true) ||
            path.endsWith(".m3u8", ignoreCase = true) ||
            path.endsWith(".ts", ignoreCase = true) ||
            path.endsWith(".jpeg", ignoreCase = true) ||
            path.endsWith(".jpg", ignoreCase = true) ||
            path.endsWith(".png", ignoreCase = true)
    }

    private companion object {
        const val TAG = "KeepDataMedia"
        const val KEEPDATA_HOST_MARKER = "keepdata"
    }
}
