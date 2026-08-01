package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
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

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006064), // Darker cyan for light mode
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF006064),
    secondary = Color(0xFF4527A0),
    onSecondary = Color.White,
    background = Color(0xFFF5F5F5), // Light gray background
    onBackground = Color(0xFF212121),
    surface = Color.White,
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF616161),
    outline = Color(0xFFBDBDBD),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Ignored here to keep custom theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


