package com.globaldevmax.app.imio.network.interceptor

import com.globaldevmax.app.imio.network.auth.DigestAuthenticator
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Ensures Digest auth retry for KeepData (WebDAV) when the server returns 401.
 * Coil image loads use the same [okhttp3.OkHttpClient] as Retrofit/ExoPlayer.
 */
class KeepDataRequestInterceptor(
    username: String,
    password: String
) : Interceptor {

    private val digestAuthenticator = DigestAuthenticator(username, password)
    private val isConfigured = username.isNotBlank() && password.isNotBlank()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!isConfigured || !request.url.host.contains(KEEPDATA_HOST_MARKER)) {
            return chain.proceed(request)
        }

        val requestWithAgent = request.newBuilder()
            .header(USER_AGENT_HEADER, USER_AGENT_VALUE)
            .build()

        var response = chain.proceed(requestWithAgent)
        if (response.code == HTTP_UNAUTHORIZED) {
            val authenticatedRequest = digestAuthenticator.authenticate(
                route = chain.connection()?.route(),
                response = response
            )
            response.close()
            if (authenticatedRequest != null) {
                response = chain.proceed(
                    authenticatedRequest.newBuilder()
                        .header(USER_AGENT_HEADER, USER_AGENT_VALUE)
                        .build()
                )
            }
        }
        return response
    }

    private companion object {
        const val KEEPDATA_HOST_MARKER = "keepdata"
        const val USER_AGENT_HEADER = "User-Agent"
        const val USER_AGENT_VALUE = "Imio/1.0"
        const val HTTP_UNAUTHORIZED = 401
    }
}
