package com.globaldevmax.app.imio.ui.screen.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ImioBackButton
import com.globaldevmax.app.imio.ui.components.ImioPremiumButton
import com.globaldevmax.app.imio.ui.components.LottieIcon

@Composable
fun PremiumScreen(
    onSubscribeClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .padding(24.dp)
                .padding(bottom = 92.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            LottieIcon(
                animationResId = R.raw.ic_premium_crown,
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.premium_title),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumFeature(text = stringResource(R.string.premium_feature_no_ads))
                PremiumFeature(text = stringResource(R.string.premium_feature_more_content))
                PremiumFeature(text = stringResource(R.string.premium_feature_priority))
            }
        }

        ImioPremiumButton(
            text = stringResource(R.string.premium_subscribe),
            onClick = onSubscribeClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, end = 24.dp, bottom = 48.dp),
            textStyle = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun PremiumFeature(text: String) {
    Text(
        text = stringResource(R.string.premium_feature_item, text),
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Thin,
        fontSize = 20.sp
    )
}
