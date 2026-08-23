package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LightForest,
    onPrimary = LightSurface,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightForest,
    secondary = LightForestLight,
    onSecondary = LightSurface,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightForestLight,
    tertiary = LightEarthAmber,
    onTertiary = LightSurface,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = LightTerracotta,
    onError = LightSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkForest,
    onPrimary = DarkBg,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkForest,
    secondary = DarkForestLight,
    onSecondary = DarkBg,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkForestLight,
    tertiary = DarkEarthAmber,
    onTertiary = DarkBg,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = DarkTerracotta,
    onError = DarkBg
)

@Composable
fun MyApplicationTheme(
    forceDarkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val themeMode by ThemeManager.themeMode.collectAsState()
    val isSystemDark = isSystemInDarkTheme()

    val isDark = when {
        forceDarkTheme != null -> forceDarkTheme
        themeMode == AppThemeMode.DARK -> true
        themeMode == AppThemeMode.LIGHT -> false
        else -> isSystemDark
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val customColors = if (isDark) {
        AppCustomColors(
            isDark = true,
            background = DarkBg,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            surfaceElevated = DarkSurfaceElevated,
            border = DarkBorder,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            textMuted = DarkTextMuted,
            primaryAccent = DarkForest,
            secondaryAccent = DarkForestLight,
            amber = DarkEarthAmber,
            red = DarkTerracotta,
            gold = DarkGold,
            pitchGrass1 = PitchGrassDark1,
            pitchGrass2 = PitchGrassDark2,
            cardBg = DarkSurface,
            cardBgElevated = DarkSurfaceElevated
        )
    } else {
        AppCustomColors(
            isDark = false,
            background = LightBg,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            surfaceElevated = LightSurfaceElevated,
            border = LightBorder,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            textMuted = LightTextMuted,
            primaryAccent = LightForest,
            secondaryAccent = LightForestLight,
            amber = LightEarthAmber,
            red = LightTerracotta,
            gold = LightGold,
            pitchGrass1 = PitchGrass,
            pitchGrass2 = PitchGrassLight,
            cardBg = LightSurface,
            cardBgElevated = LightSurfaceElevated
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = customColors.background.toArgb()
                it.navigationBarColor = customColors.background.toArgb()
                val controller = WindowCompat.getInsetsController(it, view)
                controller.isAppearanceLightStatusBars = !isDark
                controller.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(LocalAppColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
