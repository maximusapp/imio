package com.globaldevmax.app.imio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R

private val PremiumBannerShape = RoundedCornerShape(20.dp)
private val PremiumGold = Color(0xFFFBBF24)
private val PremiumGoldDark = Color(0xFFF59E0B)
private val PremiumAmberBg = Color(0xFFFEF3C7)

@Composable
fun ImioPremiumRequiredBanner(
    message: String,
    onGetPremiumClick: () -> Unit,
    modifier: Modifier = Modifier,
    showGetPremiumButton: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(PremiumBannerShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PremiumGold.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.10f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        PremiumGold.copy(alpha = 0.85f),
                        PremiumGoldDark.copy(alpha = 0.65f)
                    )
                ),
                shape = PremiumBannerShape
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = stringResource(R.string.premium_feature_badge_title),
                color = PremiumAmberBg,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Text(
            text = message,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.94f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (showGetPremiumButton) {
            ImioPremiumButton(
                text = stringResource(R.string.premium_get_subscription),
                onClick = onGetPremiumClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
