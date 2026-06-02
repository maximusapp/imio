package com.globaldevmax.app.imio.ui.screen.eveningmode

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.ads.ImioBannerAd
import com.globaldevmax.app.imio.ui.components.ImioActionButton
import com.globaldevmax.app.imio.ui.components.ImioTopHeader
import com.globaldevmax.app.imio.ui.components.LottieIcon
import com.globaldevmax.app.imio.ui.components.ParentVerificationDialog

private val EveningModeHintShape = RoundedCornerShape(18.dp)
private val EveningModeActiveGreen = Color(0xFF22C55E)

@Composable
fun EveningModeScreen(
    isEveningModeActive: Boolean,
    onEveningModeActiveChange: (Boolean) -> Unit,
    showAds: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showParentVerification by remember { mutableStateOf(false) }

    if (showParentVerification) {
        ParentVerificationDialog(
            onConfirmed = {
                showParentVerification = false
                onEveningModeActiveChange(false)
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        ImioTopHeader(
            title = stringResource(R.string.evening_mode_title),
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 86.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.35f))

            LottieIcon(
                animationResId = R.raw.sloth_sleeping,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Text(
                text = stringResource(R.string.evening_mode_description),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 20.dp)
            )

            // Place banner below the main text (and away from back + primary action).
            Spacer(modifier = Modifier.height(16.dp))
            ImioBannerAd(
                adUnitId = stringResource(R.string.ad_unit_evening_mode_banner),
                showAds = showAds,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.weight(1f))

            if (isEveningModeActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(EveningModeHintShape)
                        .background(EveningModeActiveGreen.copy(alpha = 0.16f))
                        .border(
                            width = 1.dp,
                            color = EveningModeActiveGreen.copy(alpha = 0.55f),
                            shape = EveningModeHintShape
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.evening_mode_active_hint),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.92f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            ImioActionButton(
                text = stringResource(
                    if (isEveningModeActive) {
                        R.string.evening_mode_deactivate
                    } else {
                        R.string.evening_mode_activate
                    }
                ),
                onClick = {
                    if (isEveningModeActive) {
                        showParentVerification = true
                    } else {
                        onEveningModeActiveChange(true)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp),
                width = 320.dp,
                containerColor = if (isEveningModeActive) {
                    Color(0xFFEF4444)
                } else {
                    EveningModeActiveGreen
                }
            )
        }
    }
}
