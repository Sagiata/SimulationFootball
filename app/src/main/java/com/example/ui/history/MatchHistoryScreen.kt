package com.example.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.example.model.HistoricalMatchRecord
import com.example.model.MatchResultType
import com.example.ui.components.HapticController
import com.example.ui.theme.*

@Composable
fun MatchHistoryScreen(
    matchHistory: List<HistoricalMatchRecord>,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var selectedRecord by remember { mutableStateOf(matchHistory.firstOrNull()) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Left Column: Match Fixtures History Archive List
        Surface(
            modifier = Modifier
                .weight(1f)
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
                    text = "HISTORICAL MATCH LOG (${matchHistory.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NaturalForest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (matchHistory.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No matches played yet.\nPlay a live match to view records here.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(matchHistory, key = { it.matchId }) { record ->
                            val isSelected = selectedRecord?.matchId == record.matchId
                            val resultColor = when (record.resultType) {
                                MatchResultType.WIN -> NaturalForest
                                MatchResultType.DRAW -> NaturalEarthAmber
                                MatchResultType.LOSS -> NaturalTerracotta
                            }
                            val opponentName = if (record.userTeamIsHome) record.awayTeam else record.homeTeam

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clickable {
                                        HapticController.performTactileClick(haptic, context)
                                        selectedRecord = record
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
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            color = resultColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, resultColor)
                                        ) {
                                            Text(
                                                text = record.resultType.name.take(1),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = resultColor,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 10.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = "vs $opponentName",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = TextPrimary
                                                )
                                            )
                                            Text(
                                                text = record.competition,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = 8.5.sp,
                                                    color = TextSecondary
                                                )
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${record.homeScore} - ${record.awayScore}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 12.sp,
                                                color = resultColor
                                            )
                                        )
                                        Text(
                                            text = record.dateString,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 8.sp,
                                                color = TextSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Right Column: Match Details & Analytical Breakdown
        Surface(
            modifier = Modifier
                .weight(1.25f)
                .fillMaxHeight(),
            color = StadiumSurface,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
        ) {
            val record = selectedRecord ?: matchHistory.firstOrNull()

            if (record != null) {
                val oppTeam = if (record.userTeamIsHome) record.awayTeam else record.homeTeam
                val userTeam = if (record.userTeamIsHome) record.homeTeam else record.awayTeam
                val oppPossession = 100 - record.possessionUser

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Header Score Banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = StadiumSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(userTeam, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary))
                                    Text("Possession: ${record.possessionUser}%", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, color = TextSecondary))
                                }

                                Surface(
                                    color = NaturalForest,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${record.homeScore}  -  ${record.awayScore}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(oppTeam, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary))
                                    Text("Possession: $oppPossession%", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, color = TextSecondary))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Match Details & Statistics
                        Text(
                            text = "MATCH STATISTICAL COMPARISON",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NaturalForest,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        HistoryStatRow("Expected Goals (xG)", String.format(Locale.US, "%.2f", record.xGHome), String.format(Locale.US, "%.2f", record.xGAway))
                        HistoryStatRow("User Shots", "${record.totalShotsUser}", "-")
                        HistoryStatRow("Possession", "${record.possessionUser}%", "$oppPossession%")
                        HistoryStatRow("Match Rating", String.format(Locale.US, "%.1f", record.matchRating), "-")

                        if (record.userGoalScorers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚽ Goals: ${record.userGoalScorers.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.sp,
                                    color = NaturalForest,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Bottom: Top Performer & XP Gained
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = StadiumSurfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⭐ Top Performer: ${record.topPerformer}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = NaturalEarthAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "XP: +${record.managerXpGained}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = NaturalForest,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Select a fixture to view match analysis.", color = TextSecondary, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun HistoryStatRow(label: String, homeVal: String, awayVal: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.5.dp),
        color = StadiumSurfaceVariant,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(homeVal, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalForest)
            Text(label, fontSize = 9.sp, color = TextSecondary)
            Text(awayVal, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalTerracotta)
        }
    }
}
