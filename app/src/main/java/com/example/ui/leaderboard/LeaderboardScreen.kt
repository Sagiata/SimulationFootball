package com.example.ui.leaderboard

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LeaderboardEntry
import com.example.model.LeagueTier
import com.example.ui.components.HapticController
import com.example.ui.theme.*
import com.example.utils.AppLanguage
import com.example.utils.LocalizationManager

@Composable
fun LeaderboardScreen(
    leaderboards: List<LeaderboardEntry>,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var selectedTier by remember { mutableStateOf(LeagueTier.GLOBAL_SUPER_LEAGUE) }

    // Ensure full 20 teams or full table with relegation positions
    val fullStandings = remember(leaderboards) {
        if (leaderboards.size >= 10) {
            leaderboards
        } else {
            // Expand with realistic rival clubs for full relegation depth
            val existing = leaderboards.toMutableList()
            val extraTeams = listOf(
                "Southampton Saints", "Ipswich Town FC", "Sheffield Blades", "Luton Town FC",
                "Burnley Clarets", "Leicester Foxes", "Norwich Canaries", "Watford Hornets"
            )
            var currentRank = existing.size + 1
            for (team in extraTeams) {
                if (existing.size < 20) {
                    val w = (20 - currentRank).coerceAtLeast(3)
                    val d = (currentRank % 4) + 2
                    val l = (28 - (w + d)).coerceAtLeast(0)
                    val gf = 30 + w * 2
                    val ga = 20 + l * 2
                    existing.add(
                        LeaderboardEntry(
                            rank = currentRank,
                            managerId = "mgr_extra_$currentRank",
                            clubName = team,
                            managerName = "Coach ${team.take(4)}",
                            region = "England",
                            matchesPlayed = 28,
                            wins = w,
                            draws = d,
                            losses = l,
                            goalsFor = gf,
                            goalsAgainst = ga,
                            goalDifference = (gf - ga),
                            points = (w * 3 + d),
                            winStreak = if (w > 10) 2 else 0,
                            eloRating = 1200 + (20 - currentRank) * 25,
                            isCurrentUser = false
                        )
                    )
                    currentRank++
                }
            }
            existing
        }
    }

    // Identify Safety threshold (17th position points or (totalTeams - 3)th position)
    val relegationCutoffIndex = (fullStandings.size - 3).coerceAtLeast(fullStandings.size - 2)
    val safeTeamPoints = fullStandings.getOrNull(relegationCutoffIndex - 1)?.points ?: 35

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Left Column: League Divisions & Promotion/Relegation Rules
        Surface(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight(),
            color = StadiumSurface,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "LEAGUE DIVISIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NaturalForest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(LeagueTier.entries) { tier ->
                            val isSelected = tier == selectedTier
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .clickable {
                                        HapticController.performTactileClick(haptic, context)
                                        selectedTier = tier
                                    },
                                color = if (isSelected) StadiumSurfaceVariant else StadiumSurface,
                                shape = RoundedCornerShape(6.dp),
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
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = tier.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.5.sp,
                                                color = if (isSelected) NaturalForest else TextPrimary
                                            ),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${tier.regionBadge} • Min: ${tier.minElo}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 8.sp,
                                                color = TextSecondary
                                            )
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = NaturalForest,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Promotion & Relegation Legend Rules
                Surface(
                    color = StadiumSurfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "QUALIFICATION ZONES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 7.5.sp
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NaturalForest))
                            Text("Pos 1-4: Champions League", fontSize = 7.5.sp, color = TextPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NaturalEarthAmber))
                            Text("Pos 5-7: Europa League", fontSize = 7.5.sp, color = TextPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NaturalTerracotta))
                            Text("Pos 18-20: Relegation Zone", fontSize = 7.5.sp, color = NaturalTerracotta, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Right Column: Division Standings & Relegation Indicators
        Surface(
            modifier = Modifier
                .weight(1.5f)
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
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            LocalizationManager.getString("position", currentLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            LocalizationManager.getString("club", currentLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(LocalizationManager.getString("played", currentLanguage), style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.sp))
                        Text(LocalizationManager.getString("won", currentLanguage), style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.sp))
                        Text(LocalizationManager.getString("drawn", currentLanguage), style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.sp))
                        Text(LocalizationManager.getString("lost", currentLanguage), style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.sp))
                        Text(LocalizationManager.getString("goal_diff", currentLanguage), style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.sp))
                        Text(LocalizationManager.getString("points", currentLanguage), style = MaterialTheme.typography.labelSmall.copy(color = NaturalForest, fontSize = 8.5.sp, fontWeight = FontWeight.Bold))
                        Text(LocalizationManager.getString("elo", currentLanguage), style = MaterialTheme.typography.labelSmall.copy(color = NaturalEarthAmber, fontSize = 8.5.sp, fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Table List with Clear Relegation Boundary
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(fullStandings, key = { it.rank }) { entry ->
                        val isUser = entry.isCurrentUser
                        val isUclZone = entry.rank in 1..4
                        val isUelZone = entry.rank in 5..7
                        val isRelegationZone = entry.rank >= (fullStandings.size - 2)

                        // If exactly at the relegation cutoff line, render the Relegation Divider
                        if (entry.rank == (fullStandings.size - 2)) {
                            RelegationDivider(
                                label = LocalizationManager.getString("relegation_zone", currentLanguage)
                            )
                        }

                        val rowBgColor = when {
                            isUser && isRelegationZone -> NaturalTerracotta.copy(alpha = 0.2f)
                            isUser -> NaturalForest.copy(alpha = 0.15f)
                            isRelegationZone -> NaturalTerracotta.copy(alpha = 0.08f)
                            isUclZone -> NaturalForest.copy(alpha = 0.05f)
                            else -> StadiumSurfaceVariant
                        }

                        val rowBorderColor = when {
                            isUser -> NaturalForest
                            isRelegationZone -> NaturalTerracotta.copy(alpha = 0.4f)
                            isUclZone -> NaturalForest.copy(alpha = 0.3f)
                            else -> StadiumBorder
                        }

                        val rankBadgeColor = when {
                            entry.rank == 1 -> NaturalEarthAmber
                            isUclZone -> NaturalForest
                            isUelZone -> Color(0xFF29B6F6)
                            isRelegationZone -> NaturalTerracotta
                            else -> TextSecondary
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp),
                            color = rowBgColor,
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(if (isUser) 1.dp else 0.5.dp, rowBorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Rank Number with Zone Dot
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.width(26.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(rankBadgeColor)
                                        )
                                        Text(
                                            text = "${entry.rank}",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = rankBadgeColor,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 9.5.sp
                                            )
                                        )
                                    }

                                    // Club Crest
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isUser) NaturalForest
                                                else if (isRelegationZone) NaturalTerracotta
                                                else NaturalForestLight
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(entry.clubName.take(1), fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    // Club Name & Relegation / Zone Tag
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = entry.clubName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isUser) FontWeight.Black else FontWeight.Bold,
                                                fontSize = 9.5.sp,
                                                color = if (isUser) NaturalForest else if (isRelegationZone) NaturalTerracotta else TextPrimary
                                            ),
                                            maxLines = 1
                                        )

                                        if (isRelegationZone) {
                                            val ptsBehind = (safeTeamPoints - entry.points).coerceAtLeast(1)
                                            Surface(
                                                color = NaturalTerracotta.copy(alpha = 0.25f),
                                                shape = RoundedCornerShape(3.dp)
                                            ) {
                                                Text(
                                                    text = "-${ptsBehind} pts",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = NaturalTerracotta,
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 6.5.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Match Stats Numbers
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${entry.matchesPlayed}", fontSize = 8.5.sp, color = TextSecondary)
                                    Text("${entry.wins}", fontSize = 8.5.sp, color = TextSecondary)
                                    Text("${entry.draws}", fontSize = 8.5.sp, color = TextSecondary)
                                    Text("${entry.losses}", fontSize = 8.5.sp, color = TextSecondary)
                                    Text(
                                        text = "${if (entry.goalDifference >= 0) "+${entry.goalDifference}" else "${entry.goalDifference}"}",
                                        fontSize = 8.5.sp,
                                        color = if (entry.goalDifference < 0) NaturalTerracotta else TextSecondary
                                    )
                                    Text(
                                        text = "${entry.points}",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isRelegationZone) NaturalTerracotta else NaturalForest
                                    )
                                    Text("${entry.eloRating}", fontSize = 9.sp, fontWeight = FontWeight.Black, color = NaturalEarthAmber)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RelegationDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = NaturalTerracotta.copy(alpha = 0.6f),
            thickness = 1.dp
        )
        Surface(
            color = NaturalTerracotta.copy(alpha = 0.2f),
            shape = RoundedCornerShape(4.dp),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, NaturalTerracotta)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = NaturalTerracotta, modifier = Modifier.size(10.dp))
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NaturalTerracotta,
                        fontWeight = FontWeight.Black,
                        fontSize = 7.sp
                    )
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = NaturalTerracotta.copy(alpha = 0.6f),
            thickness = 1.dp
        )
    }
}
