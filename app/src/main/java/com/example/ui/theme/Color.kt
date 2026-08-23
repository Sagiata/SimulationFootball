package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ==========================================
// Light Palette (Crisp Modern Natural Tone)
// ==========================================
val LightBg = Color(0xFFF4F6F1)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE8EDE4)
val LightSurfaceElevated = Color(0xFFF1F5EE)
val LightBorder = Color(0xFFCDD7C8)

val LightForest = Color(0xFF1B4332)
val LightForestLight = Color(0xFF2D6A4F)
val LightSage = Color(0xFF52796F)
val LightEarthAmber = Color(0xFFD97706)
val LightTerracotta = Color(0xFFC85A32)
val LightGold = Color(0xFFB45309)

val LightTextPrimary = Color(0xFF1A2621)
val LightTextSecondary = Color(0xFF475850)
val LightTextMuted = Color(0xFF76887E)

// ==========================================
// Dark Palette (Deep Stadium Twilight Slate)
// ==========================================
val DarkBg = Color(0xFF0C130F)
val DarkSurface = Color(0xFF141E18)
val DarkSurfaceVariant = Color(0xFF1D2A23)
val DarkSurfaceElevated = Color(0xFF24352D)
val DarkBorder = Color(0xFF2B3E34)

val DarkForest = Color(0xFF38D39F)
val DarkForestLight = Color(0xFF52B788)
val DarkSage = Color(0xFF74C69D)
val DarkEarthAmber = Color(0xFFF59E0B)
val DarkTerracotta = Color(0xFFEF4444)
val DarkGold = Color(0xFFFBBF24)

val DarkTextPrimary = Color(0xFFF0F6F3)
val DarkTextSecondary = Color(0xFFA2B5AB)
val DarkTextMuted = Color(0xFF72857C)

// ==========================================
// Pitch Grass Turf
// ==========================================
val PitchGrass = Color(0xFF2E6349)
val PitchGrassLight = Color(0xFF387658)
val PitchGrassDark1 = Color(0xFF193B2B)
val PitchGrassDark2 = Color(0xFF204835)
val PitchGrassLines = Color(0x99FFFFFF)

// ==========================================
// Static Fallbacks for backwards compatibility
// ==========================================
val NaturalBg = LightBg
val NaturalSurface = LightSurface
val NaturalSurfaceVariant = LightSurfaceVariant
val NaturalBorder = LightBorder
val NaturalForest = LightForest
val NaturalForestLight = LightForestLight
val NaturalSage = LightSage
val NaturalEarthAmber = LightEarthAmber
val NaturalTerracotta = LightTerracotta
val NaturalGold = LightGold
val NaturalSand = Color(0xFFF0F4EC)
val NaturalTextPrimary = LightTextPrimary
val NaturalTextSecondary = LightTextSecondary
val NaturalTextMuted = LightTextMuted

val NeonCyan = LightForestLight
val NeonGreen = Color(0xFF2D6A4F)
val NeonAmber = LightEarthAmber
val NeonRed = LightTerracotta
val NeonPurple = LightSage
val NeonGold = LightGold

val RatingHigh = Color(0xFF2D6A4F)
val RatingMed = LightEarthAmber
val RatingLow = LightTerracotta

// ==========================================
// Dynamic Semantic Colors Data Structure
// ==========================================
class AppCustomColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val amber: Color,
    val red: Color,
    val gold: Color,
    val pitchGrass1: Color,
    val pitchGrass2: Color,
    val cardBg: Color,
    val cardBgElevated: Color
)

val LocalAppColors = staticCompositionLocalOf {
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

object AppTheme {
    val colors: AppCustomColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

// Top-level dynamic properties for instant reactive UI updates
val StadiumDark: Color
    @Composable
    @ReadOnlyComposable
    get() = AppTheme.colors.background

val StadiumSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = AppTheme.colors.surface

val StadiumSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = AppTheme.colors.surfaceVariant

val StadiumBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = AppTheme.colors.border

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = AppTheme.colors.textPrimary

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = AppTheme.colors.textSecondary

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = AppTheme.colors.textMuted

val CardBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = AppTheme.colors.cardBg

val CardBackgroundElevated: Color
    @Composable
    @ReadOnlyComposable
    get() = AppTheme.colors.cardBgElevated
