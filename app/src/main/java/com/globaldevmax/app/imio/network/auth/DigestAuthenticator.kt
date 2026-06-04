package com.globaldevmax.app.imio.network.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicInteger

class DigestAuthenticator(
    private val username: String,
    private val password: String
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 3) return null

        val wwwAuthenticate = response.header("WWW-Authenticate") ?: return null
        if (!wwwAuthenticate.startsWith("Digest", ignoreCase = true)) return null

        val params = parseDigestParams(wwwAuthenticate)
        val realm = params["realm"] ?: return null
        val nonce = params["nonce"] ?: return null
        val qop = params["qop"]
        val opaque = params["opaque"]
        val algorithm = params["algorithm"] ?: "MD5"

        val method = response.request.method
        val uri = response.request.url.digestUri()
        val nc = nonceCount.incrementAndGet().toString(HEX_RADIX).padStart(8, '0')
        val cnonce = System.nanoTime().toString(HEX_RADIX)

        val ha1 = md5("$username:$realm:$password")
        val ha2 = md5("$method:$uri")
        val responseHash = if (qop != null) {
            md5("$ha1:$nonce:$nc:$cnonce:$qop:$ha2")
        } else {
            md5("$ha1:$nonce:$ha2")
        }

        val authorization = buildString {
            append("Digest username=\"$username\"")
            append(", realm=\"$realm\"")
            append(", nonce=\"$nonce\"")
            append(", uri=\"$uri\"")
            append(", response=\"$responseHash\"")
            if (qop != null) {
                append(", qop=$qop")
                append(", nc=$nc")
                append(", cnonce=\"$cnonce\"")
            }
            opaque?.let { append(", opaque=\"$it\"") }
            if (algorithm.isNotEmpty()) {
                append(", algorithm=$algorithm")
            }
        }

        return response.request.newBuilder()
            .header("Authorization", authorization)
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }

    private fun parseDigestParams(header: String): Map<String, String> {
        val params = mutableMapOf<String, String>()
        val digestSection = header.substringAfter("Digest", header).trim()
        val regex = Regex("""(\w+)=(?:"([^"]*)"|([^,]*))""")
        regex.findAll(digestSection).forEach { match ->
            val key = match.groupValues[1]
            val value = match.groupValues[2].ifEmpty { match.groupValues[3] }.trim()
            params[key] = value
        }
        return params
    }

    private fun md5(input: String): String {
        val digest = MessageDigest.getInstance("MD5")
            .digest(input.toByteArray(Charsets.ISO_8859_1))
        return digest.joinToString(separator = "") { byte ->
            "%02x".format(byte)
        }
    }

    private companion object {
        const val HEX_RADIX = 16
        private val nonceCount = AtomicInteger(0)
    }
}

private fun okhttp3.HttpUrl.digestUri(): String {
    val path = encodedPath
    val query = encodedQuery
    return if (query != null) "$path?$query" else path
}
