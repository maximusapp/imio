package com.globaldevmax.app.imio.domain.model

/**
 * Active subscription details from Play [Purchase.originalJson] (expiryTimeMillis).
 */
data class PremiumSubscriptionInfo(
    val expiryTimeMillis: Long,
    val autoRenewing: Boolean
)
