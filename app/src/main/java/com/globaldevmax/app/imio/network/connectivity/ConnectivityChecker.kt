package com.globaldevmax.app.imio.network.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ConnectivityChecker(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    suspend fun hasInternetConnection(): Boolean = withContext(Dispatchers.IO) {
        delay(SPLASH_CHECK_DELAY_MILLIS)
        isOnline()
    }

    suspend fun isOnline(): Boolean = withContext(Dispatchers.IO) {
        val network = connectivityManager.activeNetwork ?: return@withContext false
        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return@withContext false

        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private companion object {
        const val SPLASH_CHECK_DELAY_MILLIS = 900L
    }
}
