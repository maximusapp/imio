package com.globaldevmax.app.imio.ui.screen.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.BuildConfig
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop

@Composable
fun ProfileScreen(
    onPremiumClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onParentModeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        ProfileMenuItem(
            iconResId = R.drawable.ic_premium,
            title = stringResource(R.string.profile_imio_premium),
            description = stringResource(R.string.profile_imio_premium_description),
            onClick = onPremiumClick
        )
        ProfileMenuItem(
            iconResId = R.drawable.ic_time,
            title = stringResource(R.string.profile_parent_mode),
            description = stringResource(R.string.profile_parent_mode_description),
            onClick = onParentModeClick
        )
        ProfileMenuItem(
            iconResId = R.drawable.ic_privacy_policy,
            title = stringResource(R.string.profile_privacy_policy),
            description = stringResource(R.string.profile_privacy_policy_description),
            onClick = onPrivacyPolicyClick
        )
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(
                R.string.profile_footer,
                stringResource(R.string.app_name),
                BuildConfig.VERSION_NAME
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.68f)
        )
    }
}

@Composable
private fun ProfileMenuItem(
    @DrawableRes iconResId: Int,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(34.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                fontSize = 16.sp
            )
        }
    }
}
