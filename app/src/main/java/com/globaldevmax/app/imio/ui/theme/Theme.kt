package com.globaldevmax.app.imio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ImioGradientTop,
    secondary = ImioGradientBottom,
    tertiary = ImioOnBackground,
    background = ImioGradientBottom,
    surface = ImioGradientTop,
    onPrimary = ImioOnBackground,
    onSecondary = ImioOnBackground,
    onTertiary = ImioGradientBottom,
    onBackground = ImioOnBackground,
    onSurface = ImioOnBackground
)

private val LightColorScheme = lightColorScheme(
    primary = ImioGradientTop,
    secondary = ImioGradientBottom,
    tertiary = ImioOnBackground,
    background = ImioGradientBottom,
    surface = ImioGradientTop,
    onPrimary = ImioOnBackground,
    onSecondary = ImioOnBackground,
    onTertiary = ImioGradientBottom,
    onBackground = ImioOnBackground,
    onSurface = ImioOnBackground
)

@Composable
fun ImioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
    ) {
        ProvideTextStyle(
            value = LocalTextStyle.current.copy(fontFamily = FredokaFontFamily),
            content = content
        )
    }
}