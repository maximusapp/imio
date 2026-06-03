package com.globaldevmax.app.imio.ui.screen.videolocale

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.core.preferences.VideoContentLocale
import com.globaldevmax.app.imio.ui.components.ImioActionButton
import com.globaldevmax.app.imio.ui.components.ImioScrollableScreen
import com.globaldevmax.app.imio.ui.components.ImioScrollableTopHeaderScreen
import com.globaldevmax.app.imio.ui.components.LottieIcon
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val LocaleCardShape = RoundedCornerShape(18.dp)
private val LocaleSelectedBlue = Color(0xFF60A5FA)
private val LocaleSelectedBlueDark = Color(0xFF3B82F6)

@Composable
fun VideoLocaleSetupScreen(
    fromProfile: Boolean,
    onContinue: () -> Unit,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    viewModel: VideoLocaleSetupViewModel = koinViewModel { parametersOf(fromProfile) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!fromProfile) {
        BackHandler { }
    }

    val setupContent: @Composable ColumnScope.() -> Unit = {
        Spacer(modifier = Modifier.height(16.dp))

        LottieIcon(
            animationResId = R.raw.ic_rabbit_with_balloon,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(
                if (fromProfile) {
                    R.string.video_locale_description_from_profile
                } else {
                    R.string.video_locale_description
                }
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            VideoLocaleOptionCard(
                title = stringResource(R.string.video_locale_ukrainian),
                subtitle = stringResource(R.string.video_locale_ukrainian_subtitle),
                isSelected = uiState.selectedLocale == VideoContentLocale.UK,
                onClick = { viewModel.onLocaleSelected(VideoContentLocale.UK) }
            )
            VideoLocaleOptionCard(
                title = stringResource(R.string.video_locale_english),
                subtitle = stringResource(R.string.video_locale_english_subtitle),
                isSelected = uiState.selectedLocale == VideoContentLocale.EN,
                onClick = { viewModel.onLocaleSelected(VideoContentLocale.EN) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        ImioActionButton(
            text = stringResource(R.string.video_locale_continue),
            onClick = { viewModel.saveSelectedLocale(onContinue) },
            enabled = uiState.canContinue,
            width = Dp.Unspecified,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (fromProfile && onBackClick != null) {
        ImioScrollableTopHeaderScreen(
            title = stringResource(R.string.video_locale_title),
            onBackClick = onBackClick,
            modifier = modifier,
            content = setupContent
        )
    } else {
        Spacer(modifier = Modifier.height(20.dp))
        ImioScrollableScreen(modifier = modifier) {
            Text(
                text = stringResource(R.string.video_locale_title),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            setupContent()
        }
    }
}

@Composable
private fun VideoLocaleOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) LocaleSelectedBlue else Color.White.copy(alpha = 0.22f)
    val backgroundColor = if (isSelected) {
        LocaleSelectedBlue.copy(alpha = 0.18f)
    } else {
        Color.White.copy(alpha = 0.08f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(LocaleCardShape)
            .background(backgroundColor)
            .border(width = 2.dp, color = borderColor, shape = LocaleCardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }
        if (isSelected) {
            Text(
                text = "✓",
                color = LocaleSelectedBlueDark,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
