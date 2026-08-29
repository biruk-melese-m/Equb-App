package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EqubPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = EqubPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = EqubPrimary,
    onSecondary = Color.White,
    background = Color(0xFF130F21),
    surface = Color(0xFF1B162E),
    onBackground = Color(0xFFF3F1FA),
    onSurface = Color(0xFFF3F1FA),
    outline = Color(0xFF413861)
)

private val LightColorScheme = lightColorScheme(
    primary = EqubPrimary,
    onPrimary = Color.White,
    primaryContainer = EqubPrimaryContainer,
    onPrimaryContainer = EqubOnPrimaryContainer,
    secondary = EqubPrimaryDark,
    onSecondary = Color.White,
    background = EqubBackground,
    surface = EqubSurface,
    onBackground = EqubTextPrimary,
    onSurface = EqubTextPrimary,
    onSurfaceVariant = EqubTextSecondary,
    surfaceVariant = EqubSurface,
    outline = EqubCardBorder,
    outlineVariant = EqubBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Enforce crisp Equb light branding by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
