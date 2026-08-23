package com.example.ui.profile

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.components.HapticController
import com.example.ui.theme.*
import com.example.utils.AppLanguage
import com.example.utils.AudioEffectManager
import com.example.utils.LocalizationManager

@Composable
fun ProfileWelcomeScreen(
    currentProfile: UserProfile,
    currentLanguage: AppLanguage,
    onContinueCareer: () -> Unit,
    onNewCareer: (managerName: String, clubName: String, region: String, budgetTier: String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var showNewGameDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(StadiumDark, StadiumSurface, StadiumDark)
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Hero Banner: Football Manager 2026 Brand & Artwork
            Surface(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                color = StadiumSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // FM Shield Crest
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(NaturalForest, NaturalForestLight)
                                    )
                                )
                                .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "FM",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }

                        Text(
                            text = "FOOTBALL MANAGER 2026",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 1.2.sp
                            )
                        )

                        Surface(
                            color = NaturalForest.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NaturalForest)
                        ) {
                            Text(
                                text = "PRO CAREER SIMULATION EDITION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NaturalForest,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Edition Features Checklist
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FeatureRow(icon = Icons.Filled.SportsSoccer, text = "Real-time 2D Match Engine with VAR & xG")
                        FeatureRow(icon = Icons.Filled.CalendarMonth, text = "FC26 Authentic Calendar & Matchdays")
                        FeatureRow(icon = Icons.Filled.EmojiEvents, text = "Dynamic League Relegation & Promotion")
                        FeatureRow(icon = Icons.Filled.FitnessCenter, text = "Individual & Squad Training Development")
                    }

                    // Language Quick Switcher Tag
                    Surface(
                        modifier = Modifier
                            .clickable {
                                HapticController.performTactileClick(haptic, context)
                                onOpenSettings()
                            },
                        color = StadiumSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(currentLanguage.flagEmoji, fontSize = 14.sp)
                            Text(
                                text = currentLanguage.nativeName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Icon(Icons.Filled.Settings, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Right Column: Career Mode Profile Options (CONTINUE / NEW CAREER)
            Surface(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                color = StadiumSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SELECT CAREER PROFILE",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = NaturalForest,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "Resume active career or start fresh journey",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            IconButton(
                                onClick = onOpenSettings,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(StadiumSurfaceVariant, CircleShape)
                                    .border(1.dp, StadiumBorder, CircleShape)
                                    .testTag("btn_welcome_settings")
                            ) {
                                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = TextPrimary, modifier = Modifier.size(18.dp))
                            }
                        }

                        // Slot 1: Active Saved Profile Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, NaturalForest, RoundedCornerShape(12.dp))
                                .clickable {
                                    HapticController.performTactileClick(haptic, context)
                                    AudioEffectManager.playConfirm()
                                    onContinueCareer()
                                }
                                .testTag("btn_continue_career_slot"),
                            color = StadiumSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(NaturalForest)
                                            .border(1.5.dp, NaturalForestLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("⚽", fontSize = 18.sp)
                                    }

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = currentProfile.clubName,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary,
                                                    fontSize = 13.sp
                                                )
                                            )
                                            Surface(
                                                color = NaturalForest.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "ACTIVE SAVE",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = NaturalForest,
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 7.5.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Manager: ${currentProfile.managerName} • Rating: ${currentProfile.eloRating} ELO",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 9.5.sp
                                            )
                                        )

                                        Text(
                                            text = "Transfer Budget: $${String.format(java.util.Locale.US, "%.1f", currentProfile.transferBudgetMillions)}M • ${currentProfile.region}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = NaturalEarthAmber,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        HapticController.performTactileClick(haptic, context)
                                        AudioEffectManager.playConfirm()
                                        onContinueCareer()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_continue_career")
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = LocalizationManager.getString("continue_career", currentLanguage),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Slot 2: New Career Button Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, StadiumBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    HapticController.performTactileClick(haptic, context)
                                    showNewGameDialog = true
                                }
                                .testTag("btn_new_career_slot"),
                            color = StadiumSurfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(StadiumSurface)
                                            .border(1.dp, StadiumBorder, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = null, tint = NaturalForest, modifier = Modifier.size(22.dp))
                                    }

                                    Column {
                                        Text(
                                            text = LocalizationManager.getString("new_career", currentLanguage),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "Create new manager & customize your club from scratch",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 9.5.sp
                                            )
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        HapticController.performTactileClick(haptic, context)
                                        showNewGameDialog = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NaturalForest),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_new_career")
                                ) {
                                    Text(
                                        text = LocalizationManager.getString("new_career", currentLanguage),
                                        color = NaturalForest,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                HapticController.performTactileClick(haptic, context)
                                AudioEffectManager.playConfirm()
                                onContinueCareer()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = LocalizationManager.getString("continue_career", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = {
                                HapticController.performTactileClick(haptic, context)
                                showNewGameDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NaturalEarthAmber),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = LocalizationManager.getString("new_career", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // New Career Modal Creation Dialog
    if (showNewGameDialog) {
        NewCareerDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showNewGameDialog = false },
            onConfirm = { mName, cName, reg, bTier ->
                showNewGameDialog = false
                onNewCareer(mName, cName, reg, bTier)
            }
        )
    }
}

@Composable
private fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = NaturalForest, modifier = Modifier.size(14.dp))
        Text(text, style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, color = TextSecondary))
    }
}

@Composable
fun NewCareerDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (managerName: String, clubName: String, region: String, budgetTier: String) -> Unit
) {
    var managerNameInput by remember { mutableStateOf("Alex Ferguson") }
    var clubNameInput by remember { mutableStateOf("Apex London FC") }
    var selectedRegion by remember { mutableStateOf("Europe Premier League") }
    var selectedBudgetTier by remember { mutableStateOf("Standard ($75M)") }

    val regions = listOf("Europe Premier League", "South America Primera", "Asia Pro League", "Global Super League")
    val budgetTiers = listOf("Underdog ($30M)", "Standard ($75M)", "Super Club ($150M)")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Filled.SportsSoccer, contentDescription = null, tint = NaturalForest)
                Text(
                    text = LocalizationManager.getString("new_career", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = managerNameInput,
                        onValueChange = { managerNameInput = it },
                        label = { Text(LocalizationManager.getString("manager_name", currentLanguage), fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("input_manager_name"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = clubNameInput,
                        onValueChange = { clubNameInput = it },
                        label = { Text(LocalizationManager.getString("club_name", currentLanguage), fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("input_club_name"),
                        singleLine = true
                    )
                }

                item {
                    Text("Select Division / Region:", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        regions.take(2).forEach { reg ->
                            FilterChip(
                                selected = selectedRegion == reg,
                                onClick = { selectedRegion = reg },
                                label = { Text(reg.take(15), fontSize = 8.5.sp) }
                            )
                        }
                    }
                }

                item {
                    Text("Starting Budget Preset:", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        budgetTiers.forEach { tier ->
                            FilterChip(
                                selected = selectedBudgetTier == tier,
                                onClick = { selectedBudgetTier = tier },
                                label = { Text(tier, fontSize = 8.5.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (managerNameInput.isNotBlank() && clubNameInput.isNotBlank()) {
                        onConfirm(managerNameInput.trim(), clubNameInput.trim(), selectedRegion, selectedBudgetTier)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                modifier = Modifier.testTag("btn_confirm_new_career")
            ) {
                Text(LocalizationManager.getString("save_and_continue", currentLanguage), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LocalizationManager.getString("cancel", currentLanguage), color = TextSecondary, fontSize = 11.sp)
            }
        }
    )
}
