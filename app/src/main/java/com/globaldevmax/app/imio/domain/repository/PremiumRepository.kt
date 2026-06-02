package com.globaldevmax.app.imio.domain.repository

import android.app.Activity
import com.globaldevmax.app.imio.domain.model.PremiumCatalog
import com.globaldevmax.app.imio.domain.model.PremiumPlan
import com.globaldevmax.app.imio.domain.model.PremiumSubscriptionInfo
import kotlinx.coroutines.flow.StateFlow

interface PremiumRepository {
    val isPremiumActive: StateFlow<Boolean>
    val subscriptionInfo: StateFlow<PremiumSubscriptionInfo?>
    val catalog: StateFlow<PremiumCatalog?>
    val isBillingReady: StateFlow<Boolean>
    val purchaseError: StateFlow<String?>
    val billingFlowInProgress: StateFlow<Boolean>

    fun start()
    fun refreshPurchases()
    fun loadProducts()
    fun launchPurchase(activity: Activity, plan: PremiumPlan)
    fun clearPurchaseError()
}
