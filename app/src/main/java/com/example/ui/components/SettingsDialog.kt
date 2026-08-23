package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.model.UserProfile
import com.example.ui.theme.*
import com.example.utils.AppLanguage
import com.example.utils.AudioEffectManager
import com.example.utils.LocalizationManager

@Composable
fun SettingsDialog(
    userProfile: UserProfile,
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onLogout: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var isMuted by remember { mutableStateOf(AudioEffectManager.isMuted) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.widthIn(max = 520.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        tint = NaturalForest,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = LocalizationManager.getString("settings", currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section 1: Active Manager Identity
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = StadiumSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NaturalForest),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⚽", fontSize = 16.sp)
                            }
                            Column {
                                Text(
                                    text = userProfile.managerName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = "${userProfile.clubName} • ${userProfile.region} • ${userProfile.eloRating} ELO",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Section 2: Multi-Language Selector (6 languages requested)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Filled.Translate, contentDescription = null, tint = NaturalForest, modifier = Modifier.size(16.dp))
                            Text(
                                text = LocalizationManager.getString("select_language", currentLanguage),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalForest,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // 2x3 Grid of Languages
                        val languages = AppLanguage.entries
                        languages.chunked(3).forEach { rowLangs ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowLangs.forEach { lang ->
                                    val isSelected = currentLanguage == lang
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                HapticController.performTactileClick(haptic, context)
                                                LocalizationManager.setLanguage(lang)
                                                onLanguageSelected(lang)
                                            }
                                            .testTag("lang_btn_${lang.code}"),
                                        color = if (isSelected) NaturalForest.copy(alpha = 0.2f) else StadiumSurfaceVariant,
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) NaturalForest else StadiumBorder
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(lang.flagEmoji, fontSize = 14.sp)
                                            Column {
                                                Text(
                                                    text = lang.nativeName,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) NaturalForest else TextPrimary,
                                                        fontSize = 9.sp
                                                    ),
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = lang.displayName,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = TextSecondary,
                                                        fontSize = 7.5.sp
                                                    ),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 3: Audio & Haptic Controls
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "AUDIO & FEEDBACK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                        )

                        // Audio SFX Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(StadiumSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    imageVector = if (isMuted) Icons.Filled.VolumeMute else Icons.Filled.VolumeUp,
                                    contentDescription = null,
                                    tint = NaturalForest,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = LocalizationManager.getString("audio_sfx", currentLanguage),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Switch(
                                checked = !isMuted,
                                onCheckedChange = {
                                    isMuted = AudioEffectManager.toggleMute()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NaturalForest
                                )
                            )
                        }

                        // Haptic Vibration Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(StadiumSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.Vibration,
                                    contentDescription = null,
                                    tint = NaturalEarthAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = LocalizationManager.getString("haptic_feedback", currentLanguage),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Switch(
                                checked = true,
                                onCheckedChange = { },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NaturalForest
                                )
                            )
                        }
                    }
                }

                // Section 4: LOGOUT BUTTON (Returns to Profile Menu)
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            showLogoutConfirm = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("btn_logout_settings"),
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalTerracotta),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = LocalizationManager.getString("logout", currentLanguage),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("OK", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    )

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.Logout, contentDescription = null, tint = NaturalTerracotta)
                    Text(
                        text = LocalizationManager.getString("logout", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            },
            text = {
                Text(
                    text = LocalizationManager.getString("confirm_logout", currentLanguage),
                    fontSize = 11.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onDismiss()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NaturalTerracotta),
                    modifier = Modifier.testTag("btn_confirm_logout")
                ) {
                    Text(
                        text = LocalizationManager.getString("logout", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text(LocalizationManager.getString("cancel", currentLanguage), color = TextSecondary, fontSize = 11.sp)
                }
            }
        )
    }
}
