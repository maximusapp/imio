package com.globaldevmax.app.imio.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioOnBackground

@Composable
fun ShowPremiumVideosToggle(
    showPremiumVideos: Boolean,
    onShowPremiumVideosChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.catalog_show_premium_videos),
            color = ImioOnBackground.copy(alpha = 0.88f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = showPremiumVideos,
            onCheckedChange = onShowPremiumVideosChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = ImioGradientBottom,
                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedTrackColor = ImioOnBackground.copy(alpha = 0.28f),
                uncheckedBorderColor = ImioOnBackground.copy(alpha = 0.18f)
            )
        )
    }
}
