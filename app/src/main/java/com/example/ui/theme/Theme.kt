package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NaturalTonesColorScheme = lightColorScheme(
    primary = NaturalForest,
    onPrimary = NaturalSurface,
    primaryContainer = NaturalSurfaceVariant,
    onPrimaryContainer = NaturalForest,
    secondary = NaturalForestLight,
    onSecondary = NaturalSurface,
    secondaryContainer = NaturalSurfaceVariant,
    onSecondaryContainer = NaturalForestLight,
    tertiary = NaturalEarthAmber,
    onTertiary = NaturalSurface,
    background = NaturalBg,
    onBackground = NaturalTextPrimary,
    surface = NaturalSurface,
    onSurface = NaturalTextPrimary,
    surfaceVariant = NaturalSurfaceVariant,
    onSurfaceVariant = NaturalTextSecondary,
    outline = NaturalBorder,
    error = NaturalTerracotta,
    onError = NaturalSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = NaturalTonesColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = NaturalBg.toArgb()
                it.navigationBarColor = NaturalBg.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = true
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
