package com.globaldevmax.app.imio.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.globaldevmax.app.imio.core.premium.PremiumProductIds
import com.globaldevmax.app.imio.domain.model.PremiumCatalog
import com.globaldevmax.app.imio.domain.model.PremiumPlan
import com.globaldevmax.app.imio.domain.model.PremiumSubscriptionInfo
import com.globaldevmax.app.imio.domain.repository.PremiumRepository
import org.json.JSONObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class PlayBillingPremiumRepository(
    context: Context
) : PremiumRepository, PurchasesUpdatedListener {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _isPremiumActive = MutableStateFlow(false)
    override val isPremiumActive: StateFlow<Boolean> = _isPremiumActive.asStateFlow()

    private val _subscriptionInfo = MutableStateFlow<PremiumSubscriptionInfo?>(null)
    override val subscriptionInfo: StateFlow<PremiumSubscriptionInfo?> =
        _subscriptionInfo.asStateFlow()

    private val _catalog = MutableStateFlow<PremiumCatalog?>(null)
    override val catalog: StateFlow<PremiumCatalog?> = _catalog.asStateFlow()

    private val _isBillingReady = MutableStateFlow(false)
    override val isBillingReady: StateFlow<Boolean> = _isBillingReady.asStateFlow()

    private val _purchaseError = MutableStateFlow<String?>(null)
    override val purchaseError: StateFlow<String?> = _purchaseError.asStateFlow()

    private val _billingFlowInProgress = MutableStateFlow(false)
    override val billingFlowInProgress: StateFlow<Boolean> = _billingFlowInProgress.asStateFlow()

    private var billingClient: BillingClient? = null
    private var subscriptionProductDetails: ProductDetails? = null
    private var monthlyOfferToken: String? = null
    private var yearlyOfferToken: String? = null

    private var started = false

    override fun start() {
        if (started) return
        started = true

        billingClient = BillingClient.newBuilder(appContext)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isBillingReady.value = true
                    scope.launch {
                        loadProducts()
                        refreshPurchases()
                    }
                } else {
                    _isBillingReady.value = false
                }
            }

            override fun onBillingServiceDisconnected() {
                _isBillingReady.value = false
            }
        })
    }

    override fun refreshPurchases() {
        scope.launch {
            val client = billingClient ?: return@launch
            if (!client.isReady) return@launch

            val purchases = querySubscriptionPurchases(client)
            val premiumPurchase = purchases.firstOrNull { purchase ->
                purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                    purchase.products.contains(PremiumProductIds.SUBSCRIPTION_ID)
            }
            _isPremiumActive.value = premiumPurchase != null
            _subscriptionInfo.value = premiumPurchase?.toPremiumSubscriptionInfo()

            purchases.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                    acknowledgePurchase(client, purchase)
                }
            }
        }
    }

    override fun loadProducts() {
        scope.launch {
            val client = billingClient ?: return@launch
            if (!client.isReady) return@launch

            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PremiumProductIds.SUBSCRIPTION_ID)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            val result = client.queryProductDetails(params)
            if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                return@launch
            }

            val productDetails = result.productDetailsList?.firstOrNull() ?: return@launch
            subscriptionProductDetails = productDetails

            val offers = productDetails.subscriptionOfferDetails.orEmpty()
            val monthlyOffer = offers.firstOrNull { it.basePlanId == PremiumProductIds.BASE_PLAN_MONTHLY }
            val yearlyOffer = offers.firstOrNull { it.basePlanId == PremiumProductIds.BASE_PLAN_YEARLY }

            monthlyOfferToken = monthlyOffer?.offerToken
            yearlyOfferToken = yearlyOffer?.offerToken

            val monthlyPrice = monthlyOffer?.pricingPhases
                ?.pricingPhaseList
                ?.firstOrNull()
                ?.formattedPrice
            val yearlyPrice = yearlyOffer?.pricingPhases
                ?.pricingPhaseList
                ?.firstOrNull()
                ?.formattedPrice

            if (monthlyPrice != null && yearlyPrice != null) {
                _catalog.value = PremiumCatalog(
                    monthlyPrice = monthlyPrice,
                    yearlyPrice = yearlyPrice
                )
            }
        }
    }

    override fun launchPurchase(activity: Activity, plan: PremiumPlan) {
        scope.launch {
            _purchaseError.value = null
            val client = billingClient
            val productDetails = subscriptionProductDetails
            val offerToken = when (plan) {
                PremiumPlan.Monthly -> monthlyOfferToken
                PremiumPlan.Yearly -> yearlyOfferToken
            }

            if (client == null || !client.isReady || productDetails == null || offerToken == null) {
                _purchaseError.value = BILLING_NOT_READY_ERROR
                return@launch
            }

            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()

            _billingFlowInProgress.value = true
            val launchResult = client.launchBillingFlow(activity, billingFlowParams)
            if (launchResult.responseCode != BillingClient.BillingResponseCode.OK) {
                _billingFlowInProgress.value = false
                _purchaseError.value = launchResult.debugMessage.ifBlank {
                    BILLING_LAUNCH_FAILED_ERROR
                }
            }
        }
    }

    override fun clearPurchaseError() {
        _purchaseError.value = null
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        _billingFlowInProgress.value = false
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    scope.launch {
                        val client = billingClient ?: return@launch
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            if (!purchase.isAcknowledged) {
                                acknowledgePurchase(client, purchase)
                            }
                            refreshPurchases()
                        }
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> Unit
            else -> {
                _purchaseError.value = billingResult.debugMessage.ifBlank {
                    BILLING_PURCHASE_FAILED_ERROR
                }
            }
        }
    }

    private suspend fun querySubscriptionPurchases(client: BillingClient): List<Purchase> {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        val result = client.queryPurchasesAsync(params)
        return if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            result.purchasesList
        } else {
            emptyList()
        }
    }

    private suspend fun acknowledgePurchase(client: BillingClient, purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        client.acknowledgePurchase(params)
    }

    private fun Purchase.toPremiumSubscriptionInfo(): PremiumSubscriptionInfo? {
        val expiryMillis = JSONObject(originalJson).optLong("expiryTimeMillis", 0L)
        if (expiryMillis <= 0L) return null
        return PremiumSubscriptionInfo(
            expiryTimeMillis = expiryMillis,
            autoRenewing = isAutoRenewing
        )
    }

    private companion object {
        const val BILLING_NOT_READY_ERROR = "billing_not_ready"
        const val BILLING_LAUNCH_FAILED_ERROR = "billing_launch_failed"
        const val BILLING_PURCHASE_FAILED_ERROR = "billing_purchase_failed"
    }
}
