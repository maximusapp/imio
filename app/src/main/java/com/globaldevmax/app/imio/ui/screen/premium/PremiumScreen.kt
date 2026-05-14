package com.globaldevmax.app.imio.ui.screen.premium

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ImioBackButton
import com.globaldevmax.app.imio.ui.components.ImioPremiumButton
import com.globaldevmax.app.imio.ui.components.LottieIcon
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop

@Composable
fun PremiumScreen(
    onSubscribeClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlan by remember { mutableStateOf(PremiumPlan.Yearly) }

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

//            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.premium_title),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )

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
                price = stringResource(R.string.premium_plan_monthly_price),
                selected = selectedPlan == PremiumPlan.Monthly,
                onClick = { selectedPlan = PremiumPlan.Monthly }
            )
            Spacer(modifier = Modifier.height(10.dp))
            PremiumPlanItem(
                title = stringResource(R.string.premium_plan_yearly_title),
                price = stringResource(R.string.premium_plan_yearly_price),
                selected = selectedPlan == PremiumPlan.Yearly,
                savingsText = stringResource(R.string.premium_plan_yearly_savings),
                onClick = { selectedPlan = PremiumPlan.Yearly }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.premium_free_trial),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.76f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        ImioPremiumButton(
            text = stringResource(R.string.premium_subscribe),
            onClick = onSubscribeClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, end = 24.dp, bottom = 48.dp)
        )
    }
}

private enum class PremiumPlan {
    Monthly,
    Yearly
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
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (savingsText != null && selected) 16.dp else 0.dp)
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
        if (savingsText != null && selected) {
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
