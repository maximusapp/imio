package com.globaldevmax.app.imio.network

/**
 * Normalizes remote media URLs from the catalog (trim whitespace, stray punctuation from JSON).
 */
fun String.sanitizeMediaUrl(): String = trim()
    .trimEnd(',', ';', ' ', '\t', '\n', '\r')
