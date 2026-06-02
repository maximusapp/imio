package com.globaldevmax.app.imio.ui.screen.parentmode

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.ads.ImioBannerAd
import com.globaldevmax.app.imio.ui.components.ImioActionButton
import com.globaldevmax.app.imio.ui.components.ImioTopHeader
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop

@Composable
fun ParentModeScreen(
    isParentModeActive: Boolean,
    allowedMinutes: String,
    recentMinutes: List<String>,
    onParentModeActiveChange: (Boolean) -> Unit,
    onAllowedMinutesChange: (String) -> Unit,
    showAds: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        ImioTopHeader(
            title = stringResource(R.string.parent_mode_title),
            onBackClick = onBackClick
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 86.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ParentModeMinutesField(
                value = allowedMinutes,
                recentMinutes = recentMinutes,
                showAds = showAds,
                bannerAdUnitId = stringResource(R.string.ad_unit_parent_mode_banner),
                onValueChange = { value ->
                    onAllowedMinutesChange(value.filter(Char::isDigit))
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            ImioActionButton(
                text = stringResource(
                    if (isParentModeActive) {
                        R.string.parent_mode_deactivate
                    } else {
                        R.string.parent_mode_activate
                    }
                ),
                onClick = { onParentModeActiveChange(!isParentModeActive) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp),
                width = 320.dp,
                containerColor = if (isParentModeActive) {
                    Color(0xFFEF4444)
                } else {
                    Color(0xFF22C55E)
                }
            )
        }
    }
}

@Composable
private fun ParentModeMinutesField(
    value: String,
    recentMinutes: List<String>,
    showAds: Boolean,
    bannerAdUnitId: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.parent_mode_minutes_label),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        ImioBannerAd(
            adUnitId = bannerAdUnitId,
            showAds = showAds,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ImioGradientTop.copy(alpha = 0.58f),
                            Color.White.copy(alpha = 0.12f)
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 20.dp, vertical = 18.dp),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.parent_mode_minutes_label),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.46f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 20.sp
                    )
                }
                innerTextField()
            }
        )

        Text(
            text = stringResource(R.string.parent_mode_minutes_support),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (recentMinutes.isNotEmpty()) {
            ParentModeSectionDivider()

            Text(
                text = stringResource(R.string.parent_mode_recent_minutes),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recentMinutes.forEach { minutes ->
                    RecentMinuteChip(
                        minutes = minutes,
                        selected = value == minutes,
                        onClick = { onValueChange(minutes) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ParentModeSectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ImioGradientBottom.copy(alpha = 0.45f))
    )
}

@Composable
private fun RecentMinuteChip(
    minutes: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.parent_mode_recent_minute_item, minutes),
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        modifier = Modifier
            .background(
                color = if (selected) {
                    Color(0xFF22C55E).copy(alpha = 0.72f)
                } else {
                    Color.White.copy(alpha = 0.14f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    )
}
