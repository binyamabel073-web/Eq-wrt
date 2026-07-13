package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Elegant Dark Color Palette
val DarkBackground = Color(0xFF1C1B1F)
val DarkSurface = Color(0xFF25232A)
val DarkSurfaceVariant = Color(0xFF36343B)
val DarkSecondaryContainer = Color(0xFF4A4458)
val DarkPrimary = Color(0xFFD0BCFF)
val DarkOnPrimary = Color(0xFF381E72)
val DarkText = Color(0xFFE6E1E5)
val DarkTextSecondary = Color(0xFFCAC4D0)
val DarkBorder = Color(0xFF49454F)
val DarkTertiary = Color(0xFFEFB8C8)
val DarkOnTertiary = Color(0xFF492532)

private val ElegantDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondaryContainer,
    onSecondary = DarkText,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkText,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to Elegant Dark theme
    dynamicColor: Boolean = false, // Disable dynamic colors to enforce the specific brand theme
    content: @Composable () -> Unit,
) {
    // We enforce the ElegantDarkColorScheme to align with the "Elegant Dark" design requirements
    val colorScheme = ElegantDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

