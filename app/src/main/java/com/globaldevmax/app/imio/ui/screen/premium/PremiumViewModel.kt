package com.globaldevmax.app.imio.ui.screen.premium

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.globaldevmax.app.imio.domain.model.PremiumPlan
import com.globaldevmax.app.imio.domain.repository.PremiumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PremiumViewModel(
    private val premiumRepository: PremiumRepository
) : ViewModel() {

    val isPremiumActive = premiumRepository.isPremiumActive
    val subscriptionInfo = premiumRepository.subscriptionInfo
    val catalog = premiumRepository.catalog
    val isBillingReady = premiumRepository.isBillingReady
    val purchaseError = premiumRepository.purchaseError
    val isPurchasing = premiumRepository.billingFlowInProgress

    private val _selectedPlan = MutableStateFlow(PremiumPlan.Yearly)
    val selectedPlan: StateFlow<PremiumPlan> = _selectedPlan.asStateFlow()

    fun selectPlan(plan: PremiumPlan) {
        _selectedPlan.value = plan
    }

    fun subscribe(activity: Activity) {
        premiumRepository.clearPurchaseError()
        premiumRepository.launchPurchase(activity, _selectedPlan.value)
    }

    fun restorePurchases() {
        premiumRepository.refreshPurchases()
    }

    fun clearPurchaseError() {
        premiumRepository.clearPurchaseError()
    }
}
