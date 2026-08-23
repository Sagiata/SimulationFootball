package com.example.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HapticController
import com.example.ui.theme.*
import com.example.utils.AudioEffectManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onLoadingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var progress by remember { mutableStateOf(0f) }
    var currentStepText by remember { mutableStateOf("Menginisialisasi Engine Simulasi Sepak Bola...") }
    var isComplete by remember { mutableStateOf(false) }

    // Infinite rotation for ball & stadium glow
    val infiniteTransition = rememberInfiniteTransition(label = "SplashAnim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ball_spin"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    // Animated progress simulation
    LaunchedEffect(Unit) {
        val steps = listOf(
            Pair(0.15f, "Memuat Engine Database Pemain (Room DB)..."),
            Pair(0.35f, "Menghasilkan Wajah Pemain & Jersey Tim..."),
            Pair(0.55f, "Menyinkronkan Jadwal Liga & Kualifikasi World Cup..."),
            Pair(0.75f, "Memuat Data Taktik, Keuangan Klub & Pemandu Bakat..."),
            Pair(0.90f, "Menyiapkan Hub Manajemen & Tim Nasional..."),
            Pair(1.00f, "Database Siap! Sentuh untuk Memulai.")
        )

        for (step in steps) {
            val target = step.first
            currentStepText = step.second
            while (progress < target) {
                progress += 0.02f
                delay(30)
            }
            delay(120)
        }
        isComplete = true
        HapticController.triggerImpact(haptic)
        AudioEffectManager.playConfirm()
        delay(350)
        onLoadingComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF132B1C),
                        Color(0xFF07110B),
                        Color(0xFF030705)
                    ),
                    radius = 1200f
                )
            )
            .testTag("splash_screen_view"),
        contentAlignment = Alignment.Center
    ) {
        // Stadium floodlight beams in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top floodlights
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00FF87).copy(alpha = glowAlpha * 0.35f), Color.Transparent),
                    center = Offset(w * 0.2f, 0f),
                    radius = w * 0.6f
                ),
                radius = w * 0.6f,
                center = Offset(w * 0.2f, 0f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E5FF).copy(alpha = glowAlpha * 0.35f), Color.Transparent),
                    center = Offset(w * 0.8f, 0f),
                    radius = w * 0.6f
                ),
                radius = w * 0.6f,
                center = Offset(w * 0.8f, 0f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // Rotating Football + Glowing Halo
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF00FF87).copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
                    .border(2.dp, Color(0xFF00FF87).copy(alpha = glowAlpha), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚽",
                    fontSize = 54.sp,
                    modifier = Modifier.rotate(rotation)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Game Logo Title
            Text(
                text = "FOOTBALL MANAGER 2026",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.5.sp,
                    fontSize = 24.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "PRO SIMULATION & WORLD CUP ROAD",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFF00FF87),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize = 11.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Loading Progress Bar
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MEMUAT DATABASE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF88A090),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF00FF87),
                            fontWeight = FontWeight.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Custom glowing progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF14241B))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF00E5FF), Color(0xFF00FF87), Color(0xFFFFD700))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Current sub-step text
                Text(
                    text = currentStepText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFD0DDD4),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isComplete) {
                Button(
                    onClick = {
                        HapticController.triggerImpact(haptic)
                        AudioEffectManager.playConfirm()
                        onLoadingComplete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF87),
                        contentColor = Color(0xFF05120A)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(48.dp)
                        .testTag("splash_enter_button")
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MASUK KE GAME",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
