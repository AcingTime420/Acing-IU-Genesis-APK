package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HighDensityColorScheme = lightColorScheme(
    primary = AegisPrimaryCyan,
    onPrimary = AegisSurface,
    primaryContainer = AegisBadgeIndigoBg,
    onPrimaryContainer = AegisBadgeIndigoText,
    secondary = AegisBadgeIndigoBg,
    onSecondary = AegisBadgeIndigoText,
    background = AegisDarkBg,
    onBackground = AegisTextPrimary,
    surface = AegisSurface,
    onSurface = AegisTextPrimary,
    surfaceVariant = AegisSurfaceVariant,
    onSurfaceVariant = AegisTextSecondary,
    outline = AegisBorder,
    error = AegisDangerRed,
    onError = AegisSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HighDensityColorScheme,
        typography = Typography,
        content = content
    )
}


