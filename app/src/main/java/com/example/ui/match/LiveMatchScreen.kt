package com.example.ui.match

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun LiveMatchScreen(
    liveMatch: LiveMatchState?,
    opponents: List<OpponentClub>,
    selectedOpponent: OpponentClub?,
    tactics: TeamTactics,
    squad: List<Player> = emptyList(),
    onSelectOpponent: (OpponentClub) -> Unit,
    onStartMatch: (OpponentClub) -> Unit,
    onTogglePause: () -> Unit,
    onSetSpeed: (Int) -> Unit,
    onInstantSim: () -> Unit,
    onMentalityChange: (TeamMentality) -> Unit,
    onTeamTalk: (String) -> Unit = {},
    onMakeSubstitution: (String, String) -> Unit = { _, _ -> },
    onReturnToHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    if (liveMatch == null || liveMatch.matchStatus == MatchStatus.PRE_MATCH) {
        // Pre-Match Opponent Selection & Fixture Preview Screen
        PreMatchFixturePreview(
            opponents = opponents,
            selectedOpponent = selectedOpponent ?: opponents.firstOrNull(),
            tactics = tactics,
            onSelectOpponent = onSelectOpponent,
            onStartMatch = onStartMatch,
            modifier = modifier
        )
    } else {
        // Active Live Match Broadcast Simulator
        ActiveMatchBroadcast(
            liveMatch = liveMatch,
            tactics = tactics,
            squad = squad,
            onTogglePause = onTogglePause,
            onSetSpeed = onSetSpeed,
            onInstantSim = onInstantSim,
            onMentalityChange = onMentalityChange,
            onTeamTalk = onTeamTalk,
            onMakeSubstitution = onMakeSubstitution,
            onNewMatch = {
                val nextOpp = opponents.random()
                onSelectOpponent(nextOpp)
                onStartMatch(nextOpp)
            },
            onReturnToHome = onReturnToHome,
            modifier = modifier
        )
    }
}

@Composable
private fun PreMatchFixturePreview(
    opponents: List<OpponentClub>,
    selectedOpponent: OpponentClub?,
    tactics: TeamTactics,
    onSelectOpponent: (OpponentClub) -> Unit,
    onStartMatch: (OpponentClub) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val opp = selectedOpponent ?: opponents.firstOrNull()

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Left Column: Opponent List Selection
        Surface(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight(),
            color = StadiumSurface,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = "UPCOMING FIXTURES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(opponents, key = { it.id }) { opponent ->
                        val isSelected = opponent.id == opp?.id
                        OpponentListItem(
                            opponent = opponent,
                            isSelected = isSelected,
                            onSelect = {
                                HapticController.performTactileClick(haptic, context)
                                onSelectOpponent(opponent)
                            }
                        )
                    }
                }
            }
        }

        // Right Column: Match Preview & Tactical Matchup
        if (opp != null) {
            Surface(
                modifier = Modifier
                    .weight(1.4f)
                    .fillMaxHeight(),
                color = StadiumSurface,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Matchup Header
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = opp.league,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NaturalForest,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "Matchday Clash",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Head to Head Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(StadiumSurfaceVariant)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // User Club
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(NaturalForest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("APX", fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Apex FC", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                                Text("Form: W-W-D", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = NaturalForest))
                            }

                            // VS Badge
                            Surface(
                                color = NaturalForest.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NaturalForest.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "VS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = NaturalForest,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            // Opponent Club
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(opp.badgeColorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(opp.shortName, fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(opp.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary), maxLines = 1)
                                Text("Manager: ${opp.managerName}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = TextSecondary))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Attributes Comparison
                        ComparisonStatBar(label = "Attack Power", userVal = 91, oppVal = opp.attackRating)
                        ComparisonStatBar(label = "Midfield Control", userVal = 89, oppVal = opp.midfieldRating)
                        ComparisonStatBar(label = "Defensive Rigidity", userVal = 88, oppVal = opp.defenseRating)
                    }

                    // Kickoff Button (Thumb Friendly)
                    Button(
                        onClick = {
                            HapticController.performMatchKickoff(haptic, context)
                            onStartMatch(opp)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_kickoff"),
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "START MATCH SIMULATION",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
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
private fun ActiveMatchBroadcast(
    liveMatch: LiveMatchState,
    tactics: TeamTactics,
    squad: List<Player>,
    onTogglePause: () -> Unit,
    onSetSpeed: (Int) -> Unit,
    onInstantSim: () -> Unit,
    onMentalityChange: (TeamMentality) -> Unit,
    onTeamTalk: (String) -> Unit,
    onMakeSubstitution: (String, String) -> Unit,
    onNewMatch: () -> Unit,
    onReturnToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var showSubDialog by remember { mutableStateOf(false) }
    var selectedEventFilter by remember { mutableStateOf("ALL") } // ALL, GOALS, CARDS

    val starters = squad.filter { it.isStarter }
    val bench = squad.filter { !it.isStarter }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Top Scoreboard Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            color = StadiumSurface,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Home Club
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(NaturalForest),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "APX",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 8.sp
                            )
                        )
                    }
                    Text(
                        text = liveMatch.homeTeamName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        ),
                        maxLines = 1
                    )
                }

                // Center Score & Clock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = NaturalForest,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${liveMatch.homeScore}  -  ${liveMatch.awayScore}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                        )
                    }

                    // Match Status & Minute
                    Surface(
                        color = if (liveMatch.matchStatus == MatchStatus.FULL_TIME) NaturalForest.copy(alpha = 0.2f) else NaturalEarthAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (liveMatch.matchStatus == MatchStatus.FULL_TIME) "FT" else "${liveMatch.currentMinute}'",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (liveMatch.matchStatus == MatchStatus.FULL_TIME) NaturalForest else NaturalEarthAmber,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Away Club
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = liveMatch.opponent.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        ),
                        maxLines = 1
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(liveMatch.opponent.badgeColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = liveMatch.opponent.shortName.take(3),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 8.sp
                            )
                        )
                    }
                }
            }
        }

        // Center Split View: 2D Tactical Pitch Canvas on Left + Live Commentary & Controls on Right
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Left: Live 2D Tactical Pitch Animation
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1.15f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, StadiumBorder, RoundedCornerShape(8.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Field Green
                    drawRect(color = PitchGrass, topLeft = Offset.Zero, size = size)

                    val strokeW = 1.5.dp.toPx()
                    val lineCol = PitchGrassLines

                    // Boundaries & Center Circle
                    drawRect(color = lineCol, topLeft = Offset(8f, 8f), size = Size(w - 16f, h - 16f), style = Stroke(strokeW))
                    drawLine(color = lineCol, start = Offset(w / 2f, 8f), end = Offset(w / 2f, h - 8f), strokeWidth = strokeW)
                    drawCircle(color = lineCol, radius = h * 0.2f, center = Offset(w / 2f, h / 2f), style = Stroke(strokeW))

                    // Penalty Boxes
                    drawRect(color = lineCol, topLeft = Offset(8f, h * 0.25f), size = Size(w * 0.15f, h * 0.5f), style = Stroke(strokeW))
                    drawRect(color = lineCol, topLeft = Offset(w - 8f - (w * 0.15f), h * 0.25f), size = Size(w * 0.15f, h * 0.5f), style = Stroke(strokeW))

                    // Draw Live Entities
                    liveMatch.entities.forEach { entity ->
                        val px = entity.x * w
                        val py = entity.y * h
                        val dotColor = if (entity.isHome) NaturalForest else NaturalTerracotta
                        drawCircle(color = dotColor, radius = 5.dp.toPx(), center = Offset(px, py))
                        drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(px, py))
                    }

                    // Draw Live Ball
                    val bx = liveMatch.ballPosition.first * w
                    val by = liveMatch.ballPosition.second * h
                    drawCircle(color = Color(0xFFFFEB3B), radius = 4.5.dp.toPx(), center = Offset(bx, by))
                    drawCircle(color = Color.Black, radius = 4.5.dp.toPx(), center = Offset(bx, by), style = Stroke(1.dp.toPx()))
                }

                // Goal Celebration Floating Banner
                if (liveMatch.lastGoalCelebrationText != null) {
                    Surface(
                        modifier = Modifier.align(Alignment.Center),
                        color = NaturalForest.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NaturalEarthAmber)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⚽ GOAL CELEBRATION! ⚽", fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp)
                            Text(liveMatch.lastGoalCelebrationText ?: "", fontWeight = FontWeight.Bold, color = NaturalEarthAmber, fontSize = 11.sp)
                        }
                    }
                }

                // Halftime Team Talk Prompt
                if (liveMatch.matchStatus == MatchStatus.HALF_TIME && !liveMatch.teamTalkGiven) {
                    Surface(
                        modifier = Modifier.align(Alignment.Center).padding(8.dp),
                        color = StadiumSurface.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NaturalEarthAmber)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Halftime Team Talk", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(onClick = { onTeamTalk("ENCOURAGE") }, colors = ButtonDefaults.buttonColors(containerColor = NaturalForest), shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text("Encourage", fontSize = 10.sp)
                                }
                                Button(onClick = { onTeamTalk("DEMAND_MORE") }, colors = ButtonDefaults.buttonColors(containerColor = NaturalTerracotta), shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text("Demand More", fontSize = 10.sp)
                                }
                                Button(onClick = { onTeamTalk("PRAISE") }, colors = ButtonDefaults.buttonColors(containerColor = NaturalEarthAmber), shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text("Praise", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // Stats Overlay on Bottom-Left of Pitch
                Surface(
                    color = StadiumSurface.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.align(Alignment.BottomStart).padding(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Poss: ${liveMatch.stats.possessionHome}% - ${liveMatch.stats.possessionAway}%", fontSize = 9.5.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("xG: ${String.format("%.2f", liveMatch.stats.xGHome)} - ${String.format("%.2f", liveMatch.stats.xGAway)}", fontSize = 9.5.sp, color = NaturalForest, fontWeight = FontWeight.Bold)
                        Text("Shots: ${liveMatch.stats.shotsHome} - ${liveMatch.stats.shotsAway}", fontSize = 9.5.sp, color = TextSecondary)
                    }
                }
            }

            // Right: Live Commentary Ticker & Quick Controls
            Surface(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxHeight(),
                color = StadiumSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Match Events Feed Header & Filter Chips
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LIVE COMMENTARY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )

                            if (squad.isNotEmpty() && liveMatch.matchStatus != MatchStatus.FULL_TIME) {
                                OutlinedButton(
                                    onClick = { showSubDialog = true },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                                ) {
                                    Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text("Subs (${liveMatch.substitutionsRemaining})", fontSize = 9.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Events List
                        val filteredEvents = when (selectedEventFilter) {
                            "GOALS" -> liveMatch.events.filter { it.eventType == MatchEventType.GOAL }
                            "CARDS" -> liveMatch.events.filter { it.eventType == MatchEventType.YELLOW_CARD || it.eventType == MatchEventType.RED_CARD }
                            else -> liveMatch.events
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(115.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            items(filteredEvents) { event ->
                                MatchEventRow(event = event)
                            }
                        }
                    }

                    // Tactical Mentality Quick Toggles
                    Column {
                        Text(
                            text = "TOUCHLINE INSTRUCTIONS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            TeamMentality.entries.forEach { mentality ->
                                val isSelected = tactics.mentality == mentality
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(24.dp)
                                        .clickable {
                                            HapticController.performTacticChange(haptic, context)
                                            onMentalityChange(mentality)
                                        },
                                    color = if (isSelected) NaturalForest else StadiumSurfaceVariant,
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, if (isSelected) NaturalForest else StadiumBorder)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = mentality.label.take(3).uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.White else TextSecondary,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 8.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Simulation Controls (1x, 2x, 4x, Pause, Instant Sim, New Match)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (liveMatch.matchStatus == MatchStatus.FULL_TIME) {
                            Button(
                                onClick = {
                                    HapticController.performTactileClick(haptic, context)
                                    onReturnToHome()
                                },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(32.dp)
                                    .testTag("btn_ft_return_to_home"),
                                colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("RETURN TO HOME", fontSize = 9.5.sp, fontWeight = FontWeight.Black)
                            }

                            Button(
                                onClick = {
                                    HapticController.performTactileClick(haptic, context)
                                    onNewMatch()
                                },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(32.dp)
                                    .testTag("btn_ft_next_fixture"),
                                colors = ButtonDefaults.buttonColors(containerColor = StadiumSurfaceVariant),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NaturalForest),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("NEXT MATCH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NaturalForest)
                            }
                        } else {
                            // Speed buttons
                            listOf(1, 2, 4).forEach { spd ->
                                val isCur = liveMatch.simSpeed == spd
                                Surface(
                                    modifier = Modifier
                                        .weight(0.7f)
                                        .height(28.dp)
                                        .clickable {
                                            HapticController.performTactileClick(haptic, context)
                                            onSetSpeed(spd)
                                        },
                                    color = if (isCur) NaturalForest else StadiumSurfaceVariant,
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isCur) NaturalForest else StadiumBorder)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("${spd}x", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isCur) Color.White else TextPrimary)
                                    }
                                }
                            }

                            // Pause Button
                            IconButton(
                                onClick = {
                                    HapticController.performTactileClick(haptic, context)
                                    onTogglePause()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (liveMatch.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                    contentDescription = "Pause",
                                    tint = NaturalForest,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Instant Sim
                            Button(
                                onClick = {
                                    HapticController.performTactileClick(haptic, context)
                                    onInstantSim()
                                },
                                modifier = Modifier.weight(1.2f).height(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NaturalEarthAmber),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("SIM TO FT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // In-game Substitution Dialog
    if (showSubDialog) {
        AlertDialog(
            onDismissRequest = { showSubDialog = false },
            title = { Text("Make In-Game Substitution", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                var selectedStarterId by remember { mutableStateOf(starters.firstOrNull()?.id) }
                var selectedBenchId by remember { mutableStateOf(bench.firstOrNull()?.id) }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Starter to Sub Out:", fontSize = 11.sp, color = TextSecondary)
                    starters.forEach { st ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { selectedStarterId = st.id }.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${st.name} (${st.primaryRole.abbreviation} • Stamina: ${st.stamina}%)", fontSize = 11.sp, color = if (selectedStarterId == st.id) NaturalForest else TextPrimary, fontWeight = if (selectedStarterId == st.id) FontWeight.Bold else FontWeight.Normal)
                            if (selectedStarterId == st.id) Text("OUT ⬇", color = NaturalTerracotta, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = StadiumBorder)

                    Text("Select Bench Player to Sub In:", fontSize = 11.sp, color = TextSecondary)
                    bench.forEach { bn ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { selectedBenchId = bn.id }.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${bn.name} (${bn.primaryRole.abbreviation} • Stamina: ${bn.stamina}%)", fontSize = 11.sp, color = if (selectedBenchId == bn.id) NaturalForest else TextPrimary, fontWeight = if (selectedBenchId == bn.id) FontWeight.Bold else FontWeight.Normal)
                            if (selectedBenchId == bn.id) Text("IN ⬆", color = NaturalForest, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val outId = starters.firstOrNull()?.id
                        val inId = bench.firstOrNull()?.id
                        if (outId != null && inId != null) {
                            onMakeSubstitution(outId, inId)
                        }
                        showSubDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NaturalForest)
                ) {
                    Text("Confirm Sub")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MatchEventRow(event: MatchEvent) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = StadiumSurfaceVariant,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                color = when (event.eventType) {
                    MatchEventType.GOAL -> NaturalForest
                    MatchEventType.YELLOW_CARD -> NaturalEarthAmber
                    MatchEventType.RED_CARD -> NaturalTerracotta
                    else -> StadiumBorder
                },
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(
                    text = "${event.minute}'",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    ),
                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                )
            }

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 8.5.sp,
                    color = TextPrimary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun OpponentListItem(
    opponent: OpponentClub,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clickable { onSelect() },
        color = if (isSelected) StadiumSurfaceVariant else StadiumSurface,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NaturalForest else StadiumBorder)
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
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(opponent.badgeColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(opponent.shortName.take(2), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 8.sp)
                }

                Column {
                    Text(
                        text = opponent.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = opponent.league,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 8.sp
                        )
                    )
                }
            }

            Surface(
                color = NaturalForest.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "${opponent.overallRating} OVR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NaturalForest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ComparisonStatBar(label: String, userVal: Int, oppVal: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$userVal", style = MaterialTheme.typography.labelSmall.copy(color = NaturalForest, fontWeight = FontWeight.Bold, fontSize = 9.5.sp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 9.5.sp))
            Text("$oppVal", style = MaterialTheme.typography.labelSmall.copy(color = NaturalTerracotta, fontWeight = FontWeight.Bold, fontSize = 9.5.sp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(modifier = Modifier.fillMaxWidth().height(4.dp)) {
            LinearProgressIndicator(
                progress = { userVal / 100f },
                modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(2.dp)),
                color = NaturalForest,
                trackColor = StadiumBorder
            )
            Spacer(modifier = Modifier.width(4.dp))
            LinearProgressIndicator(
                progress = { oppVal / 100f },
                modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(2.dp)),
                color = NaturalTerracotta,
                trackColor = StadiumBorder
            )
        }
    }
}
