package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.model.Player
import com.example.model.PlayerRole
import com.example.ui.theme.*
import kotlin.math.abs

enum class AvatarSize(val dp: Dp, val fontSize: Int, val badgeSize: Dp) {
    TINY(32.dp, 8, 12.dp),
    SMALL(44.dp, 10, 16.dp),
    MEDIUM(60.dp, 12, 20.dp),
    LARGE(80.dp, 14, 26.dp),
    HERO(110.dp, 18, 34.dp)
}

/**
 * Modern Football Player Avatar component that renders real player face photos via Coil
 * from 13299.json asset database, with automatic procedural fallback, kit jerseys,
 * player numbers, nationality flags, and overall rating badges.
 */
@Composable
fun PlayerAvatar(
    player: Player,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.MEDIUM,
    showRatingBadge: Boolean = true,
    showRoleBadge: Boolean = true,
    showFlag: Boolean = true
) {
    PlayerAvatarVisual(
        name = player.name,
        overallRating = player.overallRating,
        role = player.primaryRole,
        flagEmoji = player.flagEmoji,
        number = player.number,
        imageUrl = player.imageUrl,
        seed = player.id.hashCode(),
        size = size,
        showRatingBadge = showRatingBadge,
        showRoleBadge = showRoleBadge,
        showFlag = showFlag,
        modifier = modifier
    )
}

@Composable
fun PlayerAvatarVisual(
    name: String,
    overallRating: Int,
    role: PlayerRole = PlayerRole.CM,
    flagEmoji: String = "⚽",
    number: Int = 10,
    imageUrl: String? = null,
    seed: Int = name.hashCode(),
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.MEDIUM,
    showRatingBadge: Boolean = true,
    showRoleBadge: Boolean = true,
    showFlag: Boolean = true
) {
    val positiveSeed = abs(seed)

    // Deterministic skin tones
    val skinTones = listOf(
        Color(0xFFFFDFD0), // Light Peach
        Color(0xFFF1C27D), // Fair Warm
        Color(0xFFE0AC69), // Tan
        Color(0xFFC68642), // Olive Bronze
        Color(0xFF8D5524), // Dark Chestnut
        Color(0xFF5C3317)  // Deep Mocha
    )
    val skinColor = skinTones[positiveSeed % skinTones.size]
    val skinShadow = skinColor.copy(
        red = (skinColor.red * 0.82f).coerceAtLeast(0f),
        green = (skinColor.green * 0.82f).coerceAtLeast(0f),
        blue = (skinColor.blue * 0.82f).coerceAtLeast(0f)
    )

    // Deterministic hair colors
    val hairColors = listOf(
        Color(0xFF1A1A1A), // Jet Black
        Color(0xFF3B2314), // Dark Brown
        Color(0xFF6A4E32), // Golden Brown
        Color(0xFFD4AF37), // Blonde
        Color(0xFF8A2E18), // Auburn Red
        Color(0xFF808080), // Silver Platinum
        Color(0xFF00E5FF)  // Electric Cyan Streak
    )
    val hairColor = hairColors[(positiveSeed / 7) % hairColors.size]
    val hairStyleIndex = (positiveSeed / 13) % 6

    // Jersey kit colors
    val kitGradients = listOf(
        Pair(Color(0xFF00E5FF), Color(0xFF007799)), // Cyan / Deep Navy
        Pair(Color(0xFFFF3366), Color(0xFF880022)), // Crimson Red
        Pair(Color(0xFFFFCC00), Color(0xFF996600)), // Gold / Amber
        Pair(Color(0xFF00E676), Color(0xFF006633)), // Emerald Green
        Pair(Color(0xFF7C4DFF), Color(0xFF3F1D99)), // Royal Violet
        Pair(Color(0xFFFFFFFF), Color(0xFF9E9E9E))  // Pure White / Silver
    )
    val kitColorPair = kitGradients[(positiveSeed / 17) % kitGradients.size]

    val roleColor = when (role) {
        PlayerRole.GK -> Color(0xFFFF9800)
        PlayerRole.CB, PlayerRole.LB, PlayerRole.RB, PlayerRole.LWB, PlayerRole.RWB -> Color(0xFF29B6F6)
        PlayerRole.CDM, PlayerRole.CM, PlayerRole.CAM, PlayerRole.LM, PlayerRole.RM -> Color(0xFF00E676)
        PlayerRole.LW, PlayerRole.RW, PlayerRole.CF, PlayerRole.ST -> Color(0xFFFF5252)
    }

    val ratingGlowColor = when {
        overallRating >= 90 -> Color(0xFFFFD700) // Golden Masterclass
        overallRating >= 84 -> Color(0xFF00E5FF) // Elite Cyan
        overallRating >= 78 -> Color(0xFF00E676) // Solid Green
        else -> Color(0xFFFF9800) // Developing Orange
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .testTag("player_avatar_${name.replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        // Outer avatar circle with image / gradient background and face canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1F3025),
                            Color(0xFF0B140E)
                        )
                    )
                )
                .border(
                    width = if (size == AvatarSize.HERO) 3.dp else 1.5.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            ratingGlowColor,
                            ratingGlowColor.copy(alpha = 0.3f),
                            ratingGlowColor
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!imageUrl.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size.dp * 0.35f),
                                color = ratingGlowColor,
                                strokeWidth = 1.5.dp
                            )
                        }
                    },
                    error = {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = this.size.width
                            val h = this.size.height

                            drawJersey(w, h, kitColorPair.first, kitColorPair.second, skinColor)
                            val neckW = w * 0.24f
                            val neckH = h * 0.22f
                            drawRect(skinShadow, Offset((w - neckW) / 2f, h * 0.52f), Size(neckW, neckH))
                            val faceW = w * 0.46f
                            val faceH = h * 0.50f
                            val faceTop = h * 0.16f
                            val faceLeft = (w - faceW) / 2f
                            drawOval(skinColor, Offset(faceLeft, faceTop), Size(faceW, faceH))
                            val earR = w * 0.08f
                            drawOval(skinShadow, Offset(faceLeft - earR * 0.45f, faceTop + faceH * 0.35f), Size(earR, earR * 1.3f))
                            drawOval(skinShadow, Offset(faceLeft + faceW - earR * 0.55f, faceTop + faceH * 0.35f), Size(earR, earR * 1.3f))
                            val eyeW = w * 0.07f
                            val eyeY = faceTop + faceH * 0.42f
                            drawOval(Color(0xFF1E1E1E), Offset(faceLeft + faceW * 0.20f, eyeY), Size(eyeW, eyeW * 0.8f))
                            drawOval(Color(0xFF1E1E1E), Offset(faceLeft + faceW * 0.60f, eyeY), Size(eyeW, eyeW * 0.8f))
                            drawCircle(skinShadow, w * 0.035f, Offset(w * 0.50f, faceTop + faceH * 0.58f))
                            val smilePath = Path().apply {
                                moveTo(faceLeft + faceW * 0.30f, faceTop + faceH * 0.74f)
                                quadraticBezierTo(w * 0.50f, faceTop + faceH * 0.84f, faceLeft + faceW * 0.70f, faceTop + faceH * 0.74f)
                            }
                            drawPath(smilePath, color = skinShadow)
                            drawHair(w, h, faceLeft, faceTop, faceW, hairColor, hairStyleIndex)
                        }
                    }
                )
            } else {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height

                    // 1. Draw Player Jersey / Torso
                    drawJersey(
                        w = w,
                        h = h,
                        kitColor1 = kitColorPair.first,
                        kitColor2 = kitColorPair.second,
                        skinColor = skinColor
                    )

                    // 2. Draw Neck
                    val neckW = w * 0.24f
                    val neckH = h * 0.22f
                    drawRect(
                        color = skinShadow,
                        topLeft = Offset((w - neckW) / 2f, h * 0.52f),
                        size = Size(neckW, neckH)
                    )

                    // 3. Draw Head / Face Base
                    val faceW = w * 0.46f
                    val faceH = h * 0.50f
                    val faceTop = h * 0.16f
                    val faceLeft = (w - faceW) / 2f

                    // Face shadow & base
                    drawOval(
                        color = skinColor,
                        topLeft = Offset(faceLeft, faceTop),
                        size = Size(faceW, faceH)
                    )

                    // Ears
                    val earR = w * 0.08f
                    drawOval(
                        color = skinShadow,
                        topLeft = Offset(faceLeft - earR * 0.45f, faceTop + faceH * 0.35f),
                        size = Size(earR, earR * 1.3f)
                    )
                    drawOval(
                        color = skinShadow,
                        topLeft = Offset(faceLeft + faceW - earR * 0.55f, faceTop + faceH * 0.35f),
                        size = Size(earR, earR * 1.3f)
                    )

                    // Eyes & Eyebrows
                    val eyeW = w * 0.07f
                    val eyeY = faceTop + faceH * 0.42f
                    drawOval(
                        color = Color(0xFF1E1E1E),
                        topLeft = Offset(faceLeft + faceW * 0.20f, eyeY),
                        size = Size(eyeW, eyeW * 0.8f)
                    )
                    drawOval(
                        color = Color(0xFF1E1E1E),
                        topLeft = Offset(faceLeft + faceW * 0.60f, eyeY),
                        size = Size(eyeW, eyeW * 0.8f)
                    )

                    // Nose
                    drawCircle(
                        color = skinShadow,
                        radius = w * 0.035f,
                        center = Offset(w * 0.50f, faceTop + faceH * 0.58f)
                    )

                    // Mouth / Confident Smile
                    val smilePath = Path().apply {
                        moveTo(faceLeft + faceW * 0.30f, faceTop + faceH * 0.74f)
                        quadraticBezierTo(
                            w * 0.50f,
                            faceTop + faceH * 0.84f,
                            faceLeft + faceW * 0.70f,
                            faceTop + faceH * 0.74f
                        )
                    }
                    drawPath(smilePath, color = skinShadow)

                    // 4. Draw Hair Style
                    drawHair(
                        w = w,
                        h = h,
                        faceLeft = faceLeft,
                        faceTop = faceTop,
                        faceW = faceW,
                        hairColor = hairColor,
                        styleIndex = hairStyleIndex
                    )
                }
            }
        }

        // Overall Rating Badge (Top Right)
        if (showRatingBadge && size != AvatarSize.TINY) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .background(
                        color = Color(0xFF101812),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = ratingGlowColor,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 3.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$overallRating",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ratingGlowColor,
                        fontWeight = FontWeight.Black,
                        fontSize = (size.fontSize - 1).sp
                    )
                )
            }
        }

        // Position / Role Badge (Bottom Left)
        if (showRoleBadge && size != AvatarSize.TINY) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-2).dp, y = 2.dp)
                    .background(
                        color = roleColor,
                        shape = RoundedCornerShape(3.dp)
                    )
                    .padding(horizontal = 3.dp, vertical = 0.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = role.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF0C130F),
                        fontWeight = FontWeight.Black,
                        fontSize = (size.fontSize - 2).coerceAtLeast(7).sp
                    )
                )
            }
        }

        // Nationality Flag (Bottom Right)
        if (showFlag && size != AvatarSize.TINY && flagEmoji.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .background(Color(0xFF0C130F), CircleShape)
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = flagEmoji,
                    fontSize = (size.fontSize + 1).sp
                )
            }
        }
    }
}

private fun DrawScope.drawJersey(
    w: Float,
    h: Float,
    kitColor1: Color,
    kitColor2: Color,
    skinColor: Color
) {
    val jerseyPath = Path().apply {
        moveTo(w * 0.12f, h)
        lineTo(w * 0.18f, h * 0.65f)
        lineTo(w * 0.34f, h * 0.58f)
        lineTo(w * 0.50f, h * 0.68f) // V-neck collar center
        lineTo(w * 0.66f, h * 0.58f)
        lineTo(w * 0.82f, h * 0.65f)
        lineTo(w * 0.88f, h)
        close()
    }
    drawPath(
        path = jerseyPath,
        brush = Brush.verticalGradient(
            colors = listOf(kitColor1, kitColor2),
            startY = h * 0.55f,
            endY = h
        )
    )

    // Collar detail
    val collarPath = Path().apply {
        moveTo(w * 0.34f, h * 0.58f)
        lineTo(w * 0.50f, h * 0.68f)
        lineTo(w * 0.66f, h * 0.58f)
    }
    drawPath(
        path = collarPath,
        color = Color.White.copy(alpha = 0.8f)
    )
}

private fun DrawScope.drawHair(
    w: Float,
    h: Float,
    faceLeft: Float,
    faceTop: Float,
    faceW: Float,
    hairColor: Color,
    styleIndex: Int
) {
    when (styleIndex) {
        0 -> {
            // Sleek Modern Undercut / Pompadour
            val hairPath = Path().apply {
                moveTo(faceLeft - w * 0.03f, faceTop + h * 0.18f)
                quadraticBezierTo(faceLeft, faceTop - h * 0.08f, w * 0.50f, faceTop - h * 0.10f)
                quadraticBezierTo(faceLeft + faceW, faceTop - h * 0.08f, faceLeft + faceW + w * 0.03f, faceTop + h * 0.18f)
                lineTo(faceLeft + faceW, faceTop + h * 0.06f)
                quadraticBezierTo(w * 0.50f, faceTop + h * 0.02f, faceLeft, faceTop + h * 0.06f)
                close()
            }
            drawPath(hairPath, hairColor)
        }
        1 -> {
            // Curly / Textured Top
            for (i in 0..5) {
                val cx = faceLeft + faceW * (0.15f + i * 0.14f)
                val cy = faceTop - h * (0.02f + (i % 2) * 0.03f)
                drawCircle(
                    color = hairColor,
                    radius = w * 0.09f,
                    center = Offset(cx, cy)
                )
            }
        }
        2 -> {
            // Short Athletic Buzz / Fade
            drawOval(
                color = hairColor,
                topLeft = Offset(faceLeft - w * 0.01f, faceTop - h * 0.04f),
                size = Size(faceW + w * 0.02f, h * 0.22f)
            )
        }
        3 -> {
            // Side Sweep Fringe
            val hairPath = Path().apply {
                moveTo(faceLeft - w * 0.02f, faceTop + h * 0.15f)
                quadraticBezierTo(w * 0.40f, faceTop - h * 0.12f, faceLeft + faceW + w * 0.04f, faceTop)
                quadraticBezierTo(w * 0.60f, faceTop + h * 0.12f, faceLeft + faceW * 0.2f, faceTop + h * 0.10f)
                close()
            }
            drawPath(hairPath, hairColor)
        }
        4 -> {
            // Afro / Voluminous Curls
            drawCircle(
                color = hairColor,
                radius = w * 0.28f,
                center = Offset(w * 0.50f, faceTop + h * 0.12f)
            )
        }
        else -> {
            // Headband / Modern Footballer Athletic Band
            drawOval(
                color = hairColor,
                topLeft = Offset(faceLeft - w * 0.02f, faceTop - h * 0.06f),
                size = Size(faceW + w * 0.04f, h * 0.26f)
            )
            // White athletic headband
            drawRect(
                color = Color.White,
                topLeft = Offset(faceLeft, faceTop + h * 0.06f),
                size = Size(faceW, h * 0.035f)
            )
        }
    }
}
