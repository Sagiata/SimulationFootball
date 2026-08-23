package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val colors = AppTheme.colors

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        color = StadiumSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Club & Manager Identification
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(colors.primaryAccent)
                        .border(1.dp, colors.secondaryAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚽",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                    )
                }

                Column(modifier = Modifier.widthIn(max = 180.dp)) {
                    Text(
                        text = userProfile.clubName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${userProfile.managerName} • ${userProfile.eloRating} ELO",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 9.sp,
                            color = colors.primaryAccent
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right Status, Theme, Sound & Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Transfer Budget
                BadgePill(
                    label = "BUDGET",
                    value = "$${String.format("%.1f", userProfile.transferBudgetMillions)}M",
                    valueColor = colors.primaryAccent
                )

                // Theme Switcher Toggle (Light / Dark Mode)
                IconButton(
                    onClick = {
                        HapticController.performTactileClick(haptic, context)
                        ThemeManager.toggleLightDark()
                    },
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("topbar_theme_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (colors.isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = "Toggle Light/Dark Theme",
                        tint = if (colors.isDark) colors.gold else colors.primaryAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Sound Toggle Button
                IconButton(
                    onClick = {
                        isMuted = AudioEffectManager.toggleMute()
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Filled.VolumeMute else Icons.Filled.VolumeUp,
                        contentDescription = "Toggle Audio",
                        tint = if (isMuted) TextMuted else colors.primaryAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Settings Modal Button
                IconButton(
                    onClick = {
                        HapticController.performTactileClick(haptic, context)
                        onOpenSettings()
                    },
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("topbar_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary,
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
                            .height(30.dp)
                            .testTag("topbar_quick_match_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primaryAccent),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = if (colors.isDark) Color.Black else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "VS ${nextOpponent.shortName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (colors.isDark) Color.Black else Color.White
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
        modifier = Modifier
            .height(26.dp)
            .clip(RoundedCornerShape(6.dp)),
        color = StadiumSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = valueColor
                )
            )
        }
    }
}
