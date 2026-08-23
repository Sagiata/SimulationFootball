package com.example.ui.tactics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.HapticController
import com.example.ui.theme.*

@Composable
fun TacticsSquadScreen(
    squad: List<Player>,
    tactics: TeamTactics,
    selectedPlayerIdForSwap: String?,
    onSelectForSwap: (String) -> Unit,
    onAssignToPitchSlot: (String, Int) -> Unit = { _, _ -> },
    onInspectPlayer: (Player) -> Unit,
    onFormationChange: (FormationType) -> Unit,
    onMentalityChange: (TeamMentality) -> Unit,
    onPassingStyleChange: (PassingStyle) -> Unit,
    onPressingChange: (PressingIntensity) -> Unit,
    onTempoChange: (MatchTempo) -> Unit,
    onTacticalSlidersChange: (Int, Int) -> Unit = { _, _ -> },
    onSetPieceChange: (String?, String?, String?, String?) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var rightPanelTab by remember { mutableStateOf(0) } // 0 = Bench / Squad List, 1 = Tactical Instructions, 2 = Set Pieces & Roles

    val starters = squad.filter { it.isStarter }
    val bench = squad.filter { !it.isStarter }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Left Column: Interactive 2D Tactical Pitch Canvas
        BoxWithConstraints(
            modifier = Modifier
                .weight(1.25f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, StadiumBorder, RoundedCornerShape(10.dp))
        ) {
            val pitchWidth = maxWidth
            val pitchHeight = maxHeight

            // Pitch Turf Background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Grass Stripes
                val stripeCount = 10
                val stripeWidth = w / stripeCount
                for (i in 0 until stripeCount) {
                    val color = if (i % 2 == 0) PitchGrass else PitchGrassLight
                    drawRect(
                        color = color,
                        topLeft = Offset(i * stripeWidth, 0f),
                        size = Size(stripeWidth, h)
                    )
                }

                val strokeWidth = 2.dp.toPx()
                val lineColor = PitchGrassLines

                // Pitch Outer Boundary
                drawRect(
                    color = lineColor,
                    topLeft = Offset(12f, 12f),
                    size = Size(w - 24f, h - 24f),
                    style = Stroke(width = strokeWidth)
                )

                // Halfway Line
                drawLine(
                    color = lineColor,
                    start = Offset(w / 2f, 12f),
                    end = Offset(w / 2f, h - 12f),
                    strokeWidth = strokeWidth
                )

                // Center Circle & Center Spot
                drawCircle(
                    color = lineColor,
                    radius = h * 0.18f,
                    center = Offset(w / 2f, h / 2f),
                    style = Stroke(width = strokeWidth)
                )
                drawCircle(
                    color = lineColor,
                    radius = 3.dp.toPx(),
                    center = Offset(w / 2f, h / 2f)
                )

                // Left Penalty Area (Home Goalkeeper area)
                drawRect(
                    color = lineColor,
                    topLeft = Offset(12f, h * 0.22f),
                    size = Size(w * 0.18f, h * 0.56f),
                    style = Stroke(width = strokeWidth)
                )
                // Left 6-Yard Box
                drawRect(
                    color = lineColor,
                    topLeft = Offset(12f, h * 0.35f),
                    size = Size(w * 0.07f, h * 0.30f),
                    style = Stroke(width = strokeWidth)
                )

                // Right Penalty Area
                drawRect(
                    color = lineColor,
                    topLeft = Offset(w - 12f - (w * 0.18f), h * 0.22f),
                    size = Size(w * 0.18f, h * 0.56f),
                    style = Stroke(width = strokeWidth)
                )
                // Right 6-Yard Box
                drawRect(
                    color = lineColor,
                    topLeft = Offset(w - 12f - (w * 0.07f), h * 0.35f),
                    size = Size(w * 0.07f, h * 0.30f),
                    style = Stroke(width = strokeWidth)
                )
            }

            // Top Status Overlay (Active Formation, Instructions & Swap Alert)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = StadiumSurface.copy(alpha = 0.90f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = tactics.formation.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = NaturalForest,
                                fontWeight = FontWeight.Black
                            )
                        )
                        Text("•", color = TextSecondary, fontSize = 10.sp)
                        Text(
                            text = tactics.mentality.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NaturalEarthAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                if (selectedPlayerIdForSwap != null) {
                    Surface(
                        color = NaturalEarthAmber.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Tap any position/player to swap",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Formation Player Nodes rendered at relative slots
            val layoutSlots = tactics.formation.layoutSlots
            layoutSlots.forEachIndexed { index, slot ->
                val player = starters.find { it.starterSlotIndex == index }
                    ?: starters.getOrNull(index)

                val xPos = (pitchWidth.value * slot.relativeX).dp
                val yPos = (pitchHeight.value * slot.relativeY).dp

                Box(
                    modifier = Modifier
                        .offset(x = xPos - 32.dp, y = yPos - 22.dp)
                        .testTag("pitch_slot_$index")
                        .clickable {
                            if (selectedPlayerIdForSwap != null) {
                                if (player != null) {
                                    HapticController.performTactileClick(haptic, context)
                                    onSelectForSwap(player.id)
                                } else {
                                    HapticController.performTactileClick(haptic, context)
                                    onAssignToPitchSlot(selectedPlayerIdForSwap, index)
                                }
                            }
                        }
                ) {
                    if (player != null) {
                        val isSelected = selectedPlayerIdForSwap == player.id
                        PitchPlayerNode(
                            player = player,
                            targetRole = slot.targetRole,
                            isSelected = isSelected,
                            onTap = {
                                HapticController.performTactileClick(haptic, context)
                                onSelectForSwap(player.id)
                            },
                            onDoubleTap = {
                                HapticController.performTactileClick(haptic, context)
                                onInspectPlayer(player)
                            }
                        )
                    } else {
                        // Empty slot indicator
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(StadiumSurface.copy(alpha = 0.6f))
                                .border(1.dp, StadiumBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = slot.targetRole.abbreviation,
                                fontSize = 9.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Right Column: Controls, Bench Squad, Tactical Directives, Set Pieces
        Surface(
            modifier = Modifier
                .weight(1.05f)
                .fillMaxHeight(),
            color = StadiumSurface,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                // Header Segmented Tab Switcher (Bench vs Instructions vs Set Pieces)
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = rightPanelTab == 0,
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            rightPanelTab = 0
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = NaturalForest,
                            activeContentColor = Color.White,
                            inactiveContainerColor = StadiumSurfaceVariant,
                            inactiveContentColor = TextSecondary
                        )
                    ) {
                        Text("Bench (${bench.size})", fontSize = 10.sp, maxLines = 1)
                    }

                    SegmentedButton(
                        selected = rightPanelTab == 1,
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            rightPanelTab = 1
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = NaturalForest,
                            activeContentColor = Color.White,
                            inactiveContainerColor = StadiumSurfaceVariant,
                            inactiveContentColor = TextSecondary
                        )
                    ) {
                        Text("Tactics", fontSize = 10.sp, maxLines = 1)
                    }

                    SegmentedButton(
                        selected = rightPanelTab == 2,
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            rightPanelTab = 2
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = NaturalForest,
                            activeContentColor = Color.White,
                            inactiveContainerColor = StadiumSurfaceVariant,
                            inactiveContentColor = TextSecondary
                        )
                    ) {
                        Text("Roles & Set Pieces", fontSize = 10.sp, maxLines = 1)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                when (rightPanelTab) {
                    0 -> {
                        // Bench & Substitutes List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(bench, key = { it.id }) { subPlayer ->
                                val isSelected = selectedPlayerIdForSwap == subPlayer.id
                                BenchPlayerRow(
                                    player = subPlayer,
                                    isSelected = isSelected,
                                    onSelectForSwap = {
                                        HapticController.performTactileClick(haptic, context)
                                        onSelectForSwap(subPlayer.id)
                                    },
                                    onInspect = {
                                        HapticController.performTactileClick(haptic, context)
                                        onInspectPlayer(subPlayer)
                                    }
                                )
                            }
                        }
                    }
                    1 -> {
                        // Tactical Systems & Formations Config
                        TacticalControlsPanel(
                            tactics = tactics,
                            onFormationChange = {
                                HapticController.performTacticChange(haptic, context)
                                onFormationChange(it)
                            },
                            onMentalityChange = {
                                HapticController.performTacticChange(haptic, context)
                                onMentalityChange(it)
                            },
                            onPassingStyleChange = {
                                HapticController.performTacticChange(haptic, context)
                                onPassingStyleChange(it)
                            },
                            onPressingChange = {
                                HapticController.performTacticChange(haptic, context)
                                onPressingChange(it)
                            },
                            onTempoChange = {
                                HapticController.performTacticChange(haptic, context)
                                onTempoChange(it)
                            },
                            onSlidersChange = onTacticalSlidersChange
                        )
                    }
                    2 -> {
                        // Set Pieces & Captain Roles
                        SetPieceRolesPanel(
                            starters = starters,
                            onSetRoles = onSetPieceChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PitchPlayerNode(
    player: Player,
    targetRole: PlayerRole,
    isSelected: Boolean,
    onTap: () -> Unit,
    onDoubleTap: () -> Unit
) {
    val ratingColor = when {
        player.overallRating >= 88 -> RatingHigh
        player.overallRating >= 80 -> RatingMed
        else -> RatingLow
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .pointerInput(player.id) {
                detectTapGestures(
                    onTap = { onTap() },
                    onDoubleTap = { onDoubleTap() }
                )
            }
    ) {
        // Player Token Badge
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            if (isSelected) NaturalForestLight else NaturalForest,
                            if (isSelected) NaturalForest else Color(0xFF1B382B)
                        )
                    )
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) NaturalEarthAmber else ratingColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${player.overallRating}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = targetRole.abbreviation,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFD4E8DC),
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Player Name Label Box
        Surface(
            color = StadiumSurface.copy(alpha = 0.95f),
            shape = RoundedCornerShape(4.dp),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (player.isCaptain) {
                    Text("©", fontSize = 8.sp, color = NaturalEarthAmber, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = player.name.split(" ").lastOrNull() ?: player.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        color = if (isSelected) NaturalForestLight else TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BenchPlayerRow(
    player: Player,
    isSelected: Boolean,
    onSelectForSwap: () -> Unit,
    onInspect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("bench_player_${player.id}")
            .clickable { onSelectForSwap() },
        color = if (isSelected) StadiumSurfaceVariant else StadiumSurface,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NaturalEarthAmber else StadiumBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Role Badge
                Surface(
                    color = StadiumSurfaceVariant,
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                ) {
                    Text(
                        text = player.primaryRole.abbreviation,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NaturalForest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                // Name & Nationality
                Column {
                    Text(
                        text = "${player.flagEmoji} ${player.name}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Stamina: ${player.stamina}% • Age: ${player.age}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 8.5.sp,
                            color = TextSecondary
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // OVR Chip
                Surface(
                    color = NaturalForest.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NaturalForest.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${player.overallRating}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NaturalForest,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                // Info / Inspect Icon Button
                IconButton(
                    onClick = onInspect,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Inspect Player",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TacticalControlsPanel(
    tactics: TeamTactics,
    onFormationChange: (FormationType) -> Unit,
    onMentalityChange: (TeamMentality) -> Unit,
    onPassingStyleChange: (PassingStyle) -> Unit,
    onPressingChange: (PressingIntensity) -> Unit,
    onTempoChange: (MatchTempo) -> Unit,
    onSlidersChange: (Int, Int) -> Unit
) {
    var defLine by remember(tactics.defensiveLineDepth) { mutableStateOf(tactics.defensiveLineDepth.toFloat()) }
    var widthVal by remember(tactics.width) { mutableStateOf(tactics.width.toFloat()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "FORMATION SETUP",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FormationType.entries.forEach { f ->
                    val isSelected = tactics.formation == f
                    TacticalChip(
                        title = f.label,
                        isSelected = isSelected,
                        onClick = { onFormationChange(f) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Text(
                text = "TEAM MENTALITY",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TeamMentality.entries.forEach { m ->
                    val isSelected = tactics.mentality == m
                    TacticalChip(
                        title = m.label,
                        isSelected = isSelected,
                        onClick = { onMentalityChange(m) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Text(
                text = "PASSING PHILOSOPHY",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PassingStyle.entries.forEach { p ->
                    val isSelected = tactics.passingStyle == p
                    TacticalChip(
                        title = p.label,
                        isSelected = isSelected,
                        onClick = { onPassingStyleChange(p) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Text(
                text = "DEFENSIVE LINE DEPTH: ${defLine.toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            )
            Slider(
                value = defLine,
                onValueChange = {
                    defLine = it
                    onSlidersChange(it.toInt(), widthVal.toInt())
                },
                valueRange = 10f..90f,
                colors = SliderDefaults.colors(
                    thumbColor = NaturalForest,
                    activeTrackColor = NaturalForest,
                    inactiveTrackColor = StadiumBorder
                )
            )
        }

        item {
            Text(
                text = "ATTACKING WIDTH: ${widthVal.toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            )
            Slider(
                value = widthVal,
                onValueChange = {
                    widthVal = it
                    onSlidersChange(defLine.toInt(), it.toInt())
                },
                valueRange = 10f..90f,
                colors = SliderDefaults.colors(
                    thumbColor = NaturalForest,
                    activeTrackColor = NaturalForest,
                    inactiveTrackColor = StadiumBorder
                )
            )
        }
    }
}

@Composable
private fun SetPieceRolesPanel(
    starters: List<Player>,
    onSetRoles: (captainId: String?, penaltyId: String?, freeKickId: String?, cornerId: String?) -> Unit
) {
    var captainId by remember { mutableStateOf(starters.find { it.isCaptain }?.id ?: starters.firstOrNull()?.id) }
    var penaltyId by remember { mutableStateOf(starters.find { it.isPenaltyTaker }?.id ?: starters.firstOrNull()?.id) }
    var freeKickId by remember { mutableStateOf(starters.find { it.isFreeKickTaker }?.id ?: starters.firstOrNull()?.id) }
    var cornerId by remember { mutableStateOf(starters.find { it.isCornerTaker }?.id ?: starters.firstOrNull()?.id) }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text(
                text = "LEADERSHIP & SET PIECE SPECIALISTS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            )
        }

        item {
            RoleSelectorRow(
                roleTitle = "Team Captain ©",
                currentId = captainId,
                starters = starters,
                onSelect = {
                    captainId = it
                    onSetRoles(captainId, penaltyId, freeKickId, cornerId)
                }
            )
        }

        item {
            RoleSelectorRow(
                roleTitle = "Penalty Specialist 🎯",
                currentId = penaltyId,
                starters = starters,
                onSelect = {
                    penaltyId = it
                    onSetRoles(captainId, penaltyId, freeKickId, cornerId)
                }
            )
        }

        item {
            RoleSelectorRow(
                roleTitle = "Direct Free-Kicks ⚡",
                currentId = freeKickId,
                starters = starters,
                onSelect = {
                    freeKickId = it
                    onSetRoles(captainId, penaltyId, freeKickId, cornerId)
                }
            )
        }

        item {
            RoleSelectorRow(
                roleTitle = "Corner Kick Taker 🚩",
                currentId = cornerId,
                starters = starters,
                onSelect = {
                    cornerId = it
                    onSetRoles(captainId, penaltyId, freeKickId, cornerId)
                }
            )
        }
    }
}

@Composable
private fun RoleSelectorRow(
    roleTitle: String,
    currentId: String?,
    starters: List<Player>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPlayer = starters.find { it.id == currentId } ?: starters.firstOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceVariant),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(roleTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                ) {
                    Text(
                        selectedPlayer?.name ?: "Select",
                        fontSize = 10.sp,
                        color = NaturalForest,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(StadiumSurface)
                ) {
                    starters.forEach { player ->
                        DropdownMenuItem(
                            text = {
                                Text("${player.flagEmoji} ${player.name} (${player.primaryRole.abbreviation} • OVR: ${player.overallRating})", fontSize = 11.sp)
                            },
                            onClick = {
                                onSelect(player.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TacticalChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(30.dp)
            .clickable { onClick() },
        color = if (isSelected) NaturalForest else StadiumSurfaceVariant,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NaturalForest else StadiumBorder
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) Color.White else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
