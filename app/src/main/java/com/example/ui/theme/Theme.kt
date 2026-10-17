package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoseGoldPrimary,
    onPrimary = ObsidianDark,
    primaryContainer = DeepBurgundy,
    onPrimaryContainer = RoseGoldLight,
    secondary = ChampagneGold,
    onSecondary = ObsidianDark,
    secondaryContainer = VelvetPlum,
    onSecondaryContainer = ChampagneGoldLight,
    tertiary = RoseGoldLight,
    background = ObsidianDark,
    onBackground = OnDarkText,
    surface = SurfaceDarkCard,
    onSurface = OnDarkText,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = OnDarkTextSecondary,
    outline = Color(0xFF4A3842)
)

private val LightColorScheme = lightColorScheme(
    primary = RoseGoldDark,
    onPrimary = Color.White,
    primaryContainer = RoseGoldLight,
    onPrimaryContainer = VelvetPlum,
    secondary = ChampagneGold,
    onSecondary = Color(0xFF2C2010),
    secondaryContainer = ChampagneGoldLight,
    onSecondaryContainer = VelvetPlum,
    tertiary = DeepBurgundy,
    background = SilkIvory,
    onBackground = OnLightText,
    surface = SurfaceLightCard,
    onSurface = OnLightText,
    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = OnLightTextSecondary,
    outline = Color(0xFFDAC7C1)
)

@Composable
fun BanootTheme(
    darkTheme: Boolean = true, // Default to stunning dark luxury mode for Banoot Atelier
    dynamicColor: Boolean = false, // Keep high fashion brand colors consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
