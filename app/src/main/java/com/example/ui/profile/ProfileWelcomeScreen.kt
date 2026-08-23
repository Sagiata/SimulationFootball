package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    allProfiles: List<UserProfile>,
    currentLanguage: AppLanguage,
    onContinueCareer: () -> Unit,
    onNewCareer: (managerName: String, clubName: String, region: String, budgetTier: String) -> Unit,
    onOpenNewCareerWizard: () -> Unit = {},
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var showNewGameDialog by remember { mutableStateOf(false) }
    val colors = AppTheme.colors

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.background, colors.surface, colors.background)
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(12.dp)
    ) {
        val isCompactOrPortrait = maxWidth < 600.dp || maxWidth < maxHeight

        if (isCompactOrPortrait) {
            // Adaptive Vertical Layout for Portrait / Compact Mobile Screen DPIs
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeroBrandCard(
                    currentLanguage = currentLanguage,
                    onOpenSettings = onOpenSettings,
                    modifier = Modifier.fillMaxWidth()
                )

                CareerProfilesCard(
                    currentProfile = currentProfile,
                    currentLanguage = currentLanguage,
                    onContinueCareer = onContinueCareer,
                    onNewCareerClick = onOpenNewCareerWizard,
                    onOpenSettings = onOpenSettings,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // Landscape / Tablet Wide Screen Multi-Column Layout
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeroBrandCard(
                    currentLanguage = currentLanguage,
                    onOpenSettings = onOpenSettings,
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight()
                )

                CareerProfilesCard(
                    currentProfile = currentProfile,
                    currentLanguage = currentLanguage,
                    onContinueCareer = onContinueCareer,
                    onNewCareerClick = onOpenNewCareerWizard,
                    onOpenSettings = onOpenSettings,
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                )
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
private fun HeroBrandCard(
    currentLanguage: AppLanguage,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val colors = AppTheme.colors

    Surface(
        modifier = modifier,
        color = StadiumSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // FM Shield Crest
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(colors.primaryAccent, colors.secondaryAccent)
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FM",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = if (colors.isDark) Color.Black else Color.White
                    )
                )
            }

            Text(
                text = "FOOTBALL MANAGER 2026",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.1.sp
                )
            )

            Surface(
                color = colors.primaryAccent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.primaryAccent)
            ) {
                Text(
                    text = "PRO CAREER SIMULATION EDITION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = colors.primaryAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            // Edition Features Checklist
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                FeatureRow(icon = Icons.Filled.SportsSoccer, text = "Real-time 2D Match Engine with VAR & xG")
                FeatureRow(icon = Icons.Filled.CalendarMonth, text = "FC26 Authentic Calendar & Matchdays")
                FeatureRow(icon = Icons.Filled.EmojiEvents, text = "Dynamic League Relegation & Promotion")
                FeatureRow(icon = Icons.Filled.FitnessCenter, text = "Individual & Squad Training Development")
            }

            // Language & Quick Settings
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
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(currentLanguage.flagEmoji, fontSize = 13.sp)
                    Text(
                        text = currentLanguage.nativeName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(Icons.Filled.Settings, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                }
            }
        }
    }
}

@Composable
private fun CareerProfilesCard(
    currentProfile: UserProfile,
    currentLanguage: AppLanguage,
    onContinueCareer: () -> Unit,
    onNewCareerClick: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val colors = AppTheme.colors

    Surface(
        modifier = modifier,
        color = StadiumSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SELECT CAREER PROFILE",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = colors.primaryAccent,
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
                        .size(32.dp)
                        .background(StadiumSurfaceVariant, CircleShape)
                        .border(1.dp, StadiumBorder, CircleShape)
                        .testTag("btn_welcome_settings")
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = TextPrimary, modifier = Modifier.size(16.dp))
                }
            }

            // Slot 1: Active Saved Profile Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, colors.primaryAccent, RoundedCornerShape(12.dp))
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
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(colors.primaryAccent)
                                .border(1.5.dp, colors.secondaryAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚽", fontSize = 16.sp)
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = currentProfile.clubName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 12.5.sp
                                    )
                                )
                                Surface(
                                    color = colors.primaryAccent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = colors.primaryAccent,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 7.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Manager: ${currentProfile.managerName} • Rating: ${currentProfile.eloRating} ELO",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            )

                            Text(
                                text = "Transfer Budget: $${String.format(java.util.Locale.US, "%.1f", currentProfile.transferBudgetMillions)}M • ${currentProfile.region}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.amber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
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
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primaryAccent),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_continue_career")
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = if (colors.isDark) Color.Black else Color.White, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = LocalizationManager.getString("continue_career", currentLanguage),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (colors.isDark) Color.Black else Color.White
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
                        onNewCareerClick()
                    }
                    .testTag("btn_new_career_slot"),
                color = StadiumSurfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(StadiumSurface)
                                .border(1.dp, StadiumBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = colors.primaryAccent, modifier = Modifier.size(20.dp))
                        }

                        Column {
                            Text(
                                text = LocalizationManager.getString("new_career", currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 12.5.sp
                                )
                            )
                            Text(
                                text = "Create new manager & customize your club from scratch",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            onNewCareerClick()
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.primaryAccent),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_new_career")
                    ) {
                        Text(
                            text = LocalizationManager.getString("new_career", currentLanguage),
                            color = colors.primaryAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primaryAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = if (colors.isDark) Color.Black else Color.White, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = LocalizationManager.getString("continue_career", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        color = if (colors.isDark) Color.Black else Color.White
                    )
                }

                Button(
                    onClick = {
                        HapticController.performTactileClick(haptic, context)
                        onNewCareerClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.amber),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = LocalizationManager.getString("new_career", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = colors.primaryAccent, modifier = Modifier.size(13.dp))
        Text(text, style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.5.sp, color = TextSecondary))
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
    val colors = AppTheme.colors

    val regions = listOf("Europe Premier League", "South America Primera", "Asia Pro League", "Global Super League")
    val budgetTiers = listOf("Underdog ($30M)", "Standard ($75M)", "Super Club ($150M)")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Filled.SportsSoccer, contentDescription = null, tint = colors.primaryAccent)
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
                colors = ButtonDefaults.buttonColors(containerColor = colors.primaryAccent),
                modifier = Modifier.testTag("btn_confirm_new_career")
            ) {
                Text(LocalizationManager.getString("save_and_continue", currentLanguage), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (colors.isDark) Color.Black else Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LocalizationManager.getString("cancel", currentLanguage), color = TextSecondary, fontSize = 11.sp)
            }
        }
    )
}
