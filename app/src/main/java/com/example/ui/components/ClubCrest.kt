package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

enum class CrestSize(val dp: Dp, val fontSize: Int, val starSize: Dp) {
    SMALL(36.dp, 10, 8.dp),
    MEDIUM(54.dp, 13, 11.dp),
    LARGE(78.dp, 18, 14.dp),
    HERO(110.dp, 24, 18.dp)
}

/**
 * Modern dynamic Football Club Crest component.
 * Renders custom shields, badges, stripes, club colors, stars, and monograms.
 */
@Composable
fun ClubCrest(
    clubName: String,
    primaryColor: Color = Color(0xFF00E5FF),
    secondaryColor: Color = Color(0xFF14241B),
    stars: Int = 3,
    size: CrestSize = CrestSize.MEDIUM,
    modifier: Modifier = Modifier
) {
    val acronym = when {
        clubName.length <= 3 -> clubName.uppercase()
        clubName.contains(" ") -> clubName.split(" ").take(3).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
        else -> clubName.take(3).uppercase()
    }

    val seed = abs(clubName.hashCode())
    val crestShapeType = seed % 3 // 0: Traditional Shield, 1: Modern Roundel, 2: Diamond Gothic

    Box(
        modifier = modifier
            .size(size.dp)
            .testTag("club_crest_${clubName.replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        when (crestShapeType) {
            0 -> {
                // Traditional Football Shield
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height

                    val shieldPath = Path().apply {
                        moveTo(w * 0.10f, h * 0.10f)
                        lineTo(w * 0.90f, h * 0.10f)
                        lineTo(w * 0.90f, h * 0.58f)
                        quadraticBezierTo(w * 0.90f, h * 0.92f, w * 0.50f, h * 0.98f)
                        quadraticBezierTo(w * 0.10f, h * 0.92f, w * 0.10f, h * 0.58f)
                        close()
                    }

                    // Shield background gradient
                    drawPath(
                        path = shieldPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor, secondaryColor)
                        )
                    )

                    // Inner stripes
                    val stripePath = Path().apply {
                        moveTo(w * 0.38f, h * 0.10f)
                        lineTo(w * 0.62f, h * 0.10f)
                        lineTo(w * 0.62f, h * 0.88f)
                        lineTo(w * 0.38f, h * 0.88f)
                        close()
                    }
                    drawPath(stripePath, color = Color.White.copy(alpha = 0.25f))

                    // Gold / Platinum border
                    drawPath(
                        path = shieldPath,
                        color = Color(0xFFFFD700),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.045f)
                    )
                }
            }
            1 -> {
                // Modern European Roundel Badge
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(primaryColor, secondaryColor, primaryColor)
                            )
                        )
                        .border(
                            width = if (size == CrestSize.HERO) 4.dp else 2.dp,
                            brush = Brush.linearGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFFFF2A1))
                            ),
                            shape = CircleShape
                        )
                )
            }
            else -> {
                // Diamond Crest
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height

                    val diamondPath = Path().apply {
                        moveTo(w * 0.50f, h * 0.06f)
                        lineTo(w * 0.92f, h * 0.50f)
                        lineTo(w * 0.50f, h * 0.94f)
                        lineTo(w * 0.08f, h * 0.50f)
                        close()
                    }
                    drawPath(
                        path = diamondPath,
                        brush = Brush.radialGradient(
                            colors = listOf(primaryColor, secondaryColor)
                        )
                    )
                    drawPath(
                        path = diamondPath,
                        color = Color(0xFFFFD700),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.045f)
                    )
                }
            }
        }

        // Monogram & Stars
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Stars on top of crest
            if (stars > 0 && size != CrestSize.SMALL) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(stars.coerceAtMost(5)) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(size.starSize)
                        )
                    }
                }
            }

            // Monogram acronym
            Text(
                text = acronym,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = size.fontSize.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}
