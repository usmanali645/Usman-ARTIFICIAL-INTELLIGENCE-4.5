package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val UsmanAiColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = Color.White,
    primaryContainer = NeonVioletDark,
    onPrimaryContainer = NeonVioletLight,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    secondaryContainer = NeonCyanDark,
    onSecondaryContainer = NeonCyanLight,
    tertiary = CyberBlue,
    onTertiary = Color.Black,
    tertiaryContainer = CyberIndigo,
    onTertiaryContainer = Color.White,
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CyberBorder,
    outlineVariant = GlassBorder,
    error = CyberRose,
    onError = Color.White
)

@Composable
fun UsmanAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Usman AI is crafted specifically with a premium dark futuristic aesthetic
    MaterialTheme(
        colorScheme = UsmanAiColorScheme,
        typography = Typography,
        content = content
    )
}
