package com.example.ui.training

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.HapticController
import com.example.ui.theme.*
import com.example.utils.AppLanguage
import com.example.utils.AudioEffectManager
import com.example.utils.LocalizationManager

enum class SquadDrillType(
    val title: String,
    val icon: ImageVector,
    val primaryStatBoost: String,
    val energyCost: Int,
    val description: String
) {
    ATTACKING_FINISHING(
        "Attacking & Finishing",
        Icons.Filled.SportsSoccer,
        "+2 SHOOTING, +1 PACE",
        15,
        "Intensive 1v1 finishing drills, penalty practice, and edge-of-box strike precision."
    ),
    PLAYMAKING_VISION(
        "Tiki-Taka & Vision",
        Icons.Filled.Psychology,
        "+2 PASSING, +2 TACTICAL IQ",
        12,
        "High-tempo rondos, through-ball spatial awareness, and quick triangular transitions."
    ),
    PRESSING_DEFENDING(
        "Gegenpressing & Defense",
        Icons.Filled.Shield,
        "+2 DEFENSE, +1 PHYSICAL",
        18,
        "Zonal marking, counter-press traps, defensive line cohesion, and aerial duel practice."
    ),
    STAMINA_RECOVERY(
        "Fitness & Conditioning",
        Icons.Filled.FitnessCenter,
        "+15 STAMINA, +10 MORALE",
        -25,
        "Cryotherapy recovery, aerobic stamina conditioning, and match sharpness recovery."
    )
}

@Composable
fun TrainingScreen(
    squad: List<Player>,
    youthAcademy: List<Player>,
    financialStatement: ClubFinancialStatement,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onUpdateFocus: (String, TrainingFocus) -> Unit,
    onExecuteTraining: () -> Unit,
    onPromoteYouth: (String) -> Unit,
    onInspectPlayer: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var selectedSection by remember { mutableStateOf(0) } // 0 = Squad & Drills, 1 = Youth Academy
    var selectedDrill by remember { mutableStateOf(SquadDrillType.ATTACKING_FINISHING) }
    var isTrainingRunning by remember { mutableStateOf(false) }
    var lastTrainingResultMsg by remember { mutableStateOf<String?>(null) }

    BoxWithConstraints(modifier = modifier.fillMaxSize().padding(6.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar & Mode Toggle
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = NaturalForest, modifier = Modifier.size(18.dp))
                            Text(
                                text = LocalizationManager.getString("training_center", currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                            )
                        }
                        Text(
                            text = "Facility Tier: ${financialStatement.trainingFacilityTier.title} • Perk: ${financialStatement.trainingFacilityTier.perkDesc}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, color = TextSecondary)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        SingleChoiceSegmentedButtonRow {
                            SegmentedButton(
                                selected = selectedSection == 0,
                                onClick = {
                                    HapticController.performTactileClick(haptic, context)
                                    selectedSection = 0
                                },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = NaturalForest,
                                    activeContentColor = Color.White,
                                    inactiveContainerColor = StadiumSurfaceVariant,
                                    inactiveContentColor = TextSecondary
                                )
                            ) {
                                Text(
                                    "${LocalizationManager.getString("first_team", currentLanguage)} (${squad.size})",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            SegmentedButton(
                                selected = selectedSection == 1,
                                onClick = {
                                    HapticController.performTactileClick(haptic, context)
                                    selectedSection = 1
                                },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = NaturalEarthAmber,
                                    activeContentColor = Color.White,
                                    inactiveContainerColor = StadiumSurfaceVariant,
                                    inactiveContentColor = TextSecondary
                                )
                            ) {
                                Text(
                                    "${LocalizationManager.getString("youth_academy", currentLanguage)} (${youthAcademy.size})",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Training Result Toast Banner
            AnimatedVisibility(visible = lastTrainingResultMsg != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    color = NaturalForest.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NaturalForest)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = NaturalForest, modifier = Modifier.size(14.dp))
                            Text(lastTrainingResultMsg ?: "", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        IconButton(onClick = { lastTrainingResultMsg = null }, modifier = Modifier.size(18.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            // Main Body View
            if (selectedSection == 0) {
                // First Team Squad Training & Drills
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Left Column: Squad Player Individual Training Focus
                    Surface(
                        modifier = Modifier.weight(1.2f).fillMaxHeight(),
                        color = StadiumSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            Text(
                                text = "INDIVIDUAL PLAYER DEVELOPMENT FOCUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalForest,
                                    fontSize = 8.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(squad, key = { it.id }) { player ->
                                    PlayerTrainingRow(
                                        player = player,
                                        onFocusChange = { focus ->
                                            HapticController.performTactileClick(haptic, context)
                                            onUpdateFocus(player.id, focus)
                                        },
                                        onInspect = { onInspectPlayer(player) }
                                    )
                                }
                            }
                        }
                    }

                    // Right Column: Squad Drills & Session Execution
                    Surface(
                        modifier = Modifier.weight(0.9f).fillMaxHeight(),
                        color = StadiumSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "TEAM TRAINING DRILLS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalForest,
                                        fontSize = 8.5.sp
                                    )
                                )

                                SquadDrillType.entries.forEach { drill ->
                                    val isSelected = selectedDrill == drill
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                HapticController.performTactileClick(haptic, context)
                                                selectedDrill = drill
                                            },
                                        color = if (isSelected) NaturalForest.copy(alpha = 0.2f) else StadiumSurfaceVariant,
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) NaturalForest else StadiumBorder
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(drill.icon, contentDescription = null, tint = if (isSelected) NaturalForest else TextSecondary, modifier = Modifier.size(16.dp))
                                                    Text(
                                                        text = drill.title,
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) NaturalForest else TextPrimary,
                                                            fontSize = 9.5.sp
                                                        )
                                                    )
                                                }

                                                Surface(
                                                    color = NaturalForest.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(3.dp)
                                                ) {
                                                    Text(
                                                        text = drill.primaryStatBoost,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = NaturalForest,
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 7.5.sp
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = drill.description,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = TextSecondary,
                                                    fontSize = 7.5.sp
                                                ),
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            }

                            // Run Training Session Button
                            Button(
                                onClick = {
                                    HapticController.performTactileClick(haptic, context)
                                    AudioEffectManager.playConfirm()
                                    onExecuteTraining()
                                    lastTrainingResultMsg = "Training Complete! Squad gained attribute XP from ${selectedDrill.title}."
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("btn_execute_training_drill"),
                                colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = LocalizationManager.getString("run_training_drill", currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // Youth Academy Section
                YouthAcademySection(
                    youthAcademy = youthAcademy,
                    currentLanguage = currentLanguage,
                    onPromote = {
                        HapticController.performTactileClick(haptic, context)
                        AudioEffectManager.playConfirm()
                        onPromoteYouth(it)
                        lastTrainingResultMsg = "Promoted wonderkid to Senior Squad!"
                    },
                    onInspect = onInspectPlayer
                )
            }
        }
    }
}

@Composable
private fun PlayerTrainingRow(
    player: Player,
    onFocusChange: (TrainingFocus) -> Unit,
    onInspect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInspect() },
        color = StadiumSurfaceVariant,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(NaturalForest.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${player.overallRating}",
                        fontWeight = FontWeight.Black,
                        color = NaturalForest,
                        fontSize = 8.5.sp
                    )
                }

                Column {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 9.5.sp
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = "${player.primaryRole.abbreviation} • Age ${player.age} • Stamina: ${player.stamina}%",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 7.5.sp
                        )
                    )
                }
            }

            // Training Focus Selector Quick Chips
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                TrainingFocus.entries.forEach { focus ->
                    val isSelected = player.trainingFocus == focus
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .clickable { onFocusChange(focus) },
                        color = if (isSelected) NaturalForest else StadiumSurface,
                        shape = RoundedCornerShape(3.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp,
                            if (isSelected) NaturalForest else StadiumBorder
                        )
                    ) {
                        Text(
                            text = focus.title.take(3).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                fontSize = 7.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YouthAcademySection(
    youthAcademy: List<Player>,
    currentLanguage: AppLanguage,
    onPromote: (String) -> Unit,
    onInspect: (Player) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = StadiumSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACADEMY WONDERKIDS & SCOUTED TALENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NaturalEarthAmber,
                        fontSize = 9.sp
                    )
                )
                Text(
                    text = "${youthAcademy.size} Prospects Developing",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 8.5.sp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(youthAcademy, key = { it.id }) { wonderkid ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onInspect(wonderkid) },
                        color = StadiumSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NaturalEarthAmber.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(NaturalEarthAmber.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = NaturalEarthAmber, modifier = Modifier.size(16.dp))
                                }

                                Column {
                                    Text(
                                        text = wonderkid.name,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = "${wonderkid.primaryRole.abbreviation} • Age ${wonderkid.age} • Rating: ${wonderkid.overallRating} OVR • Potential: ${wonderkid.potentialRating} POT",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 8.5.sp
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = { onPromote(wonderkid.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = NaturalEarthAmber),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(
                                    text = LocalizationManager.getString("promote_wonderkid", currentLanguage),
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
