package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// High-Contrast SecOps Dark Theme Color Scheme (Reduces eye strain during firmware sessions)
private val DarkColorScheme = darkColorScheme(
    primary = AegisPrimaryCyan,
    onPrimary = Color(0xFF00363A),
    primaryContainer = AegisBadgeIndigoBg,
    onPrimaryContainer = AegisPrimaryCyan,
    secondary = AegisSecondaryTeal,
    onSecondary = Color(0xFF003730),
    background = AegisDarkBg,
    onBackground = AegisTextPrimary,
    surface = AegisSurface,
    onSurface = AegisTextPrimary,
    surfaceVariant = AegisSurfaceVariant,
    onSurfaceVariant = AegisTextSecondary,
    outline = AegisBorder,
    error = AegisDangerRed,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme(
    primary = AegisPrimaryCyan,
    onPrimary = Color(0xFF00363A),
    primaryContainer = AegisBadgeIndigoBg,
    onPrimaryContainer = AegisPrimaryCyan,
    secondary = AegisSecondaryTeal,
    onSecondary = Color(0xFF003730),
    background = AegisDarkBg,
    onBackground = AegisTextPrimary,
    surface = AegisSurface,
    onSurface = AegisTextPrimary,
    surfaceVariant = AegisSurfaceVariant,
    onSurfaceVariant = AegisTextSecondary,
    outline = AegisBorder,
    error = AegisDangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to SecOps Dark Theme for low eye strain
    dynamicColor: Boolean = false,
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
