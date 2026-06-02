package com.globaldevmax.app.imio.ui.screen.premium

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.domain.model.PremiumPlan
import com.globaldevmax.app.imio.domain.model.PremiumSubscriptionInfo
import com.globaldevmax.app.imio.ui.components.ImioBackButton
import com.globaldevmax.app.imio.ui.components.ImioPremiumButton
import com.globaldevmax.app.imio.ui.components.LottieIcon
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.core.net.toUri

@Composable
fun PremiumScreen(
    onBackClick: () -> Unit,
    onSubscriptionActivated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PremiumViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val isPremiumActive by viewModel.isPremiumActive.collectAsStateWithLifecycle()
    val subscriptionInfo by viewModel.subscriptionInfo.collectAsStateWithLifecycle()
    val catalog by viewModel.catalog.collectAsStateWithLifecycle()
    val isBillingReady by viewModel.isBillingReady.collectAsStateWithLifecycle()
    val purchaseError by viewModel.purchaseError.collectAsStateWithLifecycle()
    val selectedPlan by viewModel.selectedPlan.collectAsStateWithLifecycle()
    val isPurchasing by viewModel.isPurchasing.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var awaitingPurchaseCompletion by remember { mutableStateOf(false) }

    val monthlyPrice = catalog?.monthlyPrice
        ?: stringResource(R.string.premium_plan_monthly_price)
    val yearlyPrice = catalog?.yearlyPrice
        ?: stringResource(R.string.premium_plan_yearly_price)

    val errorBillingNotReady = stringResource(R.string.premium_error_billing_not_ready)
    val errorBillingLaunchFailed = stringResource(R.string.premium_error_launch_failed)
    val errorBillingPurchaseFailed = stringResource(R.string.premium_error_purchase_failed)

    LaunchedEffect(isPremiumActive, awaitingPurchaseCompletion) {
        if (isPremiumActive && awaitingPurchaseCompletion) {
            awaitingPurchaseCompletion = false
            onSubscriptionActivated()
        }
    }

    LaunchedEffect(isPurchasing) {
        if (!isPurchasing && !isPremiumActive) {
            awaitingPurchaseCompletion = false
        }
    }

    LaunchedEffect(purchaseError) {
        val errorKey = purchaseError ?: return@LaunchedEffect
        val message = when (errorKey) {
            "billing_not_ready" -> errorBillingNotReady
            "billing_launch_failed" -> errorBillingLaunchFailed
            "billing_purchase_failed" -> errorBillingPurchaseFailed
            else -> errorKey
        }
        snackbarHostState.showSnackbar(message)
        viewModel.clearPurchaseError()
    }

    Box(modifier = modifier.fillMaxSize()) {
        ImioBackButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 28.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 15.dp)
                .padding(bottom = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            LottieIcon(
                animationResId = R.raw.ic_premium_crown,
                modifier = Modifier.size(150.dp)
            )

            Text(
                text = stringResource(R.string.premium_title),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )

            if (isPremiumActive) {
                Spacer(modifier = Modifier.height(14.dp))
                PremiumActiveStatusCard(
                    subscriptionInfo = subscriptionInfo
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                PremiumFeature(
                    iconResId = R.drawable.ic_sun_with_glasses,
                    text = stringResource(R.string.premium_feature_no_ads)
                )
                PremiumFeatureDivider()
                PremiumFeature(
                    iconResId = R.drawable.ic_premium,
                    text = stringResource(R.string.premium_feature_more_content)
                )
                PremiumFeatureDivider()
                PremiumFeature(
                    iconResId = R.drawable.ic_arrow_circle,
                    text = stringResource(R.string.premium_feature_priority)
                )
            }

            if (!isPremiumActive) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.premium_choose_plan),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                PremiumPlanItem(
                    title = stringResource(R.string.premium_plan_monthly_title),
                    price = monthlyPrice,
                    selected = selectedPlan == PremiumPlan.Monthly,
                    enabled = !isPurchasing,
                    onClick = { viewModel.selectPlan(PremiumPlan.Monthly) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                PremiumPlanItem(
                    title = stringResource(R.string.premium_plan_yearly_title),
                    price = yearlyPrice,
                    selected = selectedPlan == PremiumPlan.Yearly,
                    enabled = !isPurchasing,
                    savingsText = stringResource(R.string.premium_plan_yearly_savings),
                    onClick = { viewModel.selectPlan(PremiumPlan.Yearly) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = { viewModel.restorePurchases() },
                    enabled = !isPurchasing
                ) {
                    Text(
                        text = stringResource(R.string.premium_restore_purchases),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                    )
                }
            }
        }

        if (isPremiumActive) {
            ImioPremiumButton(
                text = stringResource(R.string.premium_manage_subscription),
                onClick = {
                    val packageName = context.packageName
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://play.google.com/store/account/subscriptions?package=$packageName".toUri()
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 24.dp, end = 24.dp, bottom = 48.dp)
            )
        } else {
            ImioPremiumButton(
                text = when {
                    isPurchasing -> stringResource(R.string.premium_subscribe_processing)
                    !isBillingReady -> stringResource(R.string.premium_subscribe_loading)
                    else -> stringResource(R.string.premium_subscribe)
                },
                onClick = {
                    activity?.let {
                        awaitingPurchaseCompletion = true
                        viewModel.subscribe(it)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 24.dp, end = 24.dp, bottom = 48.dp),
                enabled = isBillingReady && !isPurchasing && activity != null
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun PremiumActiveStatusCard(
    subscriptionInfo: PremiumSubscriptionInfo?
) {
    val cardShape = RoundedCornerShape(22.dp)
    val accentCyan = Color(0xFF67E8F9)
    val activeGreen = Color(0xFF22C55E)

    val expiryMillis = subscriptionInfo?.expiryTimeMillis
    val formattedExpiry = if (expiryMillis != null) {
        remember(expiryMillis) {
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm")
                .withLocale(Locale.getDefault())
                .format(
                    Instant.ofEpochMilli(expiryMillis).atZone(ZoneId.systemDefault())
                )
        }
    } else {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.08f)
                    )
                ),
                shape = cardShape
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        accentCyan.copy(alpha = 0.85f),
                        ImioGradientTop.copy(alpha = 0.6f)
                    )
                ),
                shape = cardShape
            )
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(activeGreen, CircleShape)
            )
            Text(
                text = stringResource(R.string.premium_active_status),
                color = activeGreen,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )
        }

        val info = subscriptionInfo
        if (formattedExpiry != null && info != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.18f))
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (info.autoRenewing) {
                    stringResource(R.string.premium_next_billing_label)
                } else {
                    stringResource(R.string.premium_valid_until_label)
                },
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                letterSpacing = 0.3.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = formattedExpiry,
                color = accentCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
        }
    }
}

@Composable
private fun PremiumFeature(
    @DrawableRes iconResId: Int,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF67E8F9), ImioGradientTop)
                    ),
                    shape = RoundedCornerShape(15.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(29.dp)
            )
        }
        Box(
            modifier = Modifier
                .height(38.dp)
                .width(1.dp)
                .background(Color.White.copy(alpha = 0.20f))
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PremiumFeatureDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.Gray.copy(alpha = 0.45f))
    )
}

@Composable
private fun PremiumPlanItem(
    title: String,
    price: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    savingsText: String? = null
) {
    val shape = RoundedCornerShape(20.dp)
    val borderColor = if (selected) Color(0xFF67E8F9) else Color.Transparent
    val background = if (selected) {
        Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.24f),
                Color.White.copy(alpha = 0.12f)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.08f)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (savingsText != null) 16.dp else 0.dp)
                .background(background, shape)
                .border(width = 2.dp, color = borderColor, shape = shape)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = price,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )
        }
        if (savingsText != null) {
            Text(
                text = savingsText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(
                        color = Color(0xFF9B8CF7),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            )
        }
    }
}
