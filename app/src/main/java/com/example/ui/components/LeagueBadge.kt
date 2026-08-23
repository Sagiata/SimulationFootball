package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Visual badge for Football Leagues (Premier League, La Liga, Serie A, Champions Cup, World Cup).
 */
@Composable
fun LeagueBadge(
    leagueName: String,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    showTitle: Boolean = false
) {
    val (badgeBgGradient, badgeBorder, iconEmoji) = when {
        leagueName.contains("Premier", ignoreCase = true) -> Triple(
            listOf(Color(0xFF38003C), Color(0xFF1E0020)), // Premier League Purple
            Color(0xFF00FF87),
            "🦁"
        )
        leagueName.contains("Liga", ignoreCase = true) || leagueName.contains("Spain", ignoreCase = true) -> Triple(
            listOf(Color(0xFFFF3344), Color(0xFF990011)), // La Liga Crimson
            Color(0xFFFFD700),
            "👑"
        )
        leagueName.contains("Serie", ignoreCase = true) || leagueName.contains("Italy", ignoreCase = true) -> Triple(
            listOf(Color(0xFF004488), Color(0xFF001F44)), // Serie A Blue
            Color(0xFF00E5FF),
            "⭐"
        )
        leagueName.contains("Bundesliga", ignoreCase = true) || leagueName.contains("Germany", ignoreCase = true) -> Triple(
            listOf(Color(0xFFD3010C), Color(0xFF6B0006)), // Bundesliga Red
            Color(0xFFFFFFFF),
            "⚡"
        )
        leagueName.contains("Indonesia", ignoreCase = true) || leagueName.contains("BRI", ignoreCase = true) -> Triple(
            listOf(Color(0xFFE53935), Color(0xFFB71C1C)), // Garuda Red
            Color(0xFFFFD700),
            "🦅"
        )
        leagueName.contains("World Cup", ignoreCase = true) || leagueName.contains("FIFA", ignoreCase = true) -> Triple(
            listOf(Color(0xFFD4AF37), Color(0xFF6B5500)), // Gold Trophy
            Color(0xFFFFF2A1),
            "🏆"
        )
        else -> Triple(
            listOf(Color(0xFF1B3828), Color(0xFF0A1A10)), // Pitch Green
            Color(0xFF00E5FF),
            "⚽"
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(badgeBgGradient))
                .border(1.dp, badgeBorder, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iconEmoji,
                fontSize = (size.value * 0.55f).sp
            )
        }

        if (showTitle) {
            Text(
                text = leagueName,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

/**
 * National Team Flag and World Cup Badge component.
 */
@Composable
fun NationalFlagBadge(
    countryName: String,
    flagEmoji: String,
    rating: Int = 80,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    showName: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color(0xFF14241B))
                .border(1.5.dp, Color(0xFFFFD700), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = flagEmoji,
                fontSize = (size.value * 0.55f).sp
            )
        }

        if (showName) {
            Column {
                Text(
                    text = countryName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "OVR: $rating • World Cup Track",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFFFD700),
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}
