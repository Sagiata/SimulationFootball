package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Settings
import com.example.model.OpponentClub
import com.example.model.UserProfile
import com.example.ui.theme.*
import com.example.utils.AudioEffectManager

@Composable
fun GameTopBar(
    userProfile: UserProfile,
    nextOpponent: OpponentClub?,
    onQuickSimMatch: () -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var isMuted by remember { mutableStateOf(AudioEffectManager.isMuted) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        color = StadiumSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Club & Manager Identification
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(NaturalForest)
                        .border(1.dp, NaturalForestLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚽",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                    )
                }

                Column {
                    Text(
                        text = userProfile.clubName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = "${userProfile.managerName} • Rating: ${userProfile.eloRating} ELO",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 9.sp,
                            color = NaturalForest
                        )
                    )
                }
            }

            // Financial & Budget Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Transfer Budget
                BadgePill(
                    label = "BUDGET",
                    value = "$${String.format("%.1f", userProfile.transferBudgetMillions)}M",
                    valueColor = NaturalForest
                )

                // Weekly Wage Cap
                BadgePill(
                    label = "WAGES",
                    value = "$${userProfile.currentWeeklyWageExpenseThousands}k/wk",
                    valueColor = NaturalEarthAmber
                )

                // Settings Modal Button
                IconButton(
                    onClick = {
                        HapticController.performTactileClick(haptic, context)
                        onOpenSettings()
                    },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("topbar_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Sound Toggle Button
                IconButton(
                    onClick = {
                        isMuted = AudioEffectManager.toggleMute()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Filled.VolumeMute else Icons.Filled.VolumeUp,
                        contentDescription = "Toggle Audio",
                        tint = if (isMuted) TextSecondary else NaturalForest,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Quick Play Next Match Button
                if (nextOpponent != null) {
                    Button(
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            onQuickSimMatch()
                        },
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("topbar_quick_match_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "VS ${nextOpponent.shortName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgePill(
    label: String,
    value: String,
    valueColor: Color
) {
    Surface(
        color = StadiumSurfaceVariant,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = valueColor,
                    fontWeight = FontWeight.Black
                )
            )
        }
    }
}
