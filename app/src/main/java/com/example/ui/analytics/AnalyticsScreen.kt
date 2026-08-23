package com.example.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
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
import java.util.Locale
import com.example.model.Player
import com.example.ui.components.HapticController
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    squad: List<Player>,
    onInspectPlayer: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(0) } // 0 = Goals & xG, 1 = Passing & Creation, 2 = Defensive & Stamina

    val sortedPlayers = when (selectedCategory) {
        0 -> squad.sortedByDescending { it.seasonStats.goals * 10 + it.seasonStats.expectedGoals }
        1 -> squad.sortedByDescending { it.seasonStats.assists * 10 + it.seasonStats.passCompletionPct }
        else -> squad.sortedByDescending { it.seasonStats.tacklesWon * 5 + it.attributes.defending }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Left Column: Squad Key Metrics Summary & Category Selector
        Surface(
            modifier = Modifier
                .weight(0.85f)
                .fillMaxHeight(),
            color = StadiumSurface,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "SQUAD METRICS & KPI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NaturalForest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val totalGoals = squad.sumOf { it.seasonStats.goals }
                    val totalAssists = squad.sumOf { it.seasonStats.assists }
                    val avgPass = squad.map { it.seasonStats.passCompletionPct }.average().toInt()
                    val totalxG = squad.map { it.seasonStats.expectedGoals }.sum()

                    AnalyticsKpiCard("TOTAL SQUAD GOALS", "$totalGoals", NaturalForest)
                    Spacer(modifier = Modifier.height(4.dp))
                    AnalyticsKpiCard("EXPECTED GOALS (xG)", String.format(Locale.US, "%.2f", totalxG), NaturalEarthAmber)
                    Spacer(modifier = Modifier.height(4.dp))
                    AnalyticsKpiCard("TOTAL ASSISTS", "$totalAssists", NaturalForestLight)
                    Spacer(modifier = Modifier.height(4.dp))
                    AnalyticsKpiCard("PASS COMPLETION", "$avgPass%", TextPrimary)
                }

                // Category Switcher
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "ANALYTICAL CATEGORY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    CategoryFilterButton("Goals & Expected xG", selectedCategory == 0) {
                        HapticController.performTactileClick(haptic, context)
                        selectedCategory = 0
                    }
                    CategoryFilterButton("Passing & Key Assists", selectedCategory == 1) {
                        HapticController.performTactileClick(haptic, context)
                        selectedCategory = 1
                    }
                    CategoryFilterButton("Defending & Tackles", selectedCategory == 2) {
                        HapticController.performTactileClick(haptic, context)
                        selectedCategory = 2
                    }
                }
            }
        }

        // Right Column: Detailed Player Performance Table
        Surface(
            modifier = Modifier
                .weight(1.35f)
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
                    Text(
                        text = "PLAYER NAME",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (selectedCategory) {
                            0 -> {
                                Text("APPS", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.5.sp))
                                Text("GOALS", style = MaterialTheme.typography.labelSmall.copy(color = NaturalForest, fontSize = 8.5.sp, fontWeight = FontWeight.Bold))
                                Text("xG", style = MaterialTheme.typography.labelSmall.copy(color = NaturalEarthAmber, fontSize = 8.5.sp, fontWeight = FontWeight.Bold))
                                Text("RATING", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.5.sp))
                            }
                            1 -> {
                                Text("APPS", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.5.sp))
                                Text("AST", style = MaterialTheme.typography.labelSmall.copy(color = NaturalForest, fontSize = 8.5.sp, fontWeight = FontWeight.Bold))
                                Text("PASS %", style = MaterialTheme.typography.labelSmall.copy(color = NaturalForestLight, fontSize = 8.5.sp, fontWeight = FontWeight.Bold))
                                Text("RATING", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.5.sp))
                            }
                            else -> {
                                Text("APPS", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.5.sp))
                                Text("TCK", style = MaterialTheme.typography.labelSmall.copy(color = NaturalForest, fontSize = 8.5.sp, fontWeight = FontWeight.Bold))
                                Text("CS", style = MaterialTheme.typography.labelSmall.copy(color = NaturalEarthAmber, fontSize = 8.5.sp, fontWeight = FontWeight.Bold))
                                Text("RATING", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.5.sp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(sortedPlayers, key = { it.id }) { player ->
                        PlayerAnalyticsRow(
                            player = player,
                            category = selectedCategory,
                            onInspect = {
                                HapticController.performTactileClick(haptic, context)
                                onInspectPlayer(player)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsKpiCard(label: String, value: String, valueColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = StadiumSurfaceVariant,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 12.sp,
                    color = valueColor,
                    fontWeight = FontWeight.Black
                )
            )
        }
    }
}

@Composable
private fun CategoryFilterButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .clickable { onClick() },
        color = if (isSelected) NaturalForest else StadiumSurfaceVariant,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, if (isSelected) NaturalForest else StadiumBorder)
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
                    fontSize = 8.5.sp
                )
            )
        }
    }
}

@Composable
private fun PlayerAnalyticsRow(
    player: Player,
    category: Int,
    onInspect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clickable { onInspect() },
        color = StadiumSurfaceVariant,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
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
                    color = StadiumSurface,
                    shape = RoundedCornerShape(3.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
                ) {
                    Text(
                        text = player.primaryRole.abbreviation,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NaturalForest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        ),
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }

                Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val stats = player.seasonStats
                when (category) {
                    0 -> {
                        Text("${stats.appearances}", fontSize = 10.sp, color = TextSecondary)
                        Text("${stats.goals}", fontSize = 10.sp, color = NaturalForest, fontWeight = FontWeight.Bold)
                        Text(String.format(Locale.US, "%.1f", stats.expectedGoals), fontSize = 10.sp, color = NaturalEarthAmber, fontWeight = FontWeight.Bold)
                        Text(String.format(Locale.US, "%.1f", stats.avgMatchRating), fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    1 -> {
                        Text("${stats.appearances}", fontSize = 10.sp, color = TextSecondary)
                        Text("${stats.assists}", fontSize = 10.sp, color = NaturalForest, fontWeight = FontWeight.Bold)
                        Text("${stats.passCompletionPct}%", fontSize = 10.sp, color = NaturalForestLight, fontWeight = FontWeight.Bold)
                        Text(String.format(Locale.US, "%.1f", stats.avgMatchRating), fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    else -> {
                        Text("${stats.appearances}", fontSize = 10.sp, color = TextSecondary)
                        Text("${stats.tacklesWon}", fontSize = 10.sp, color = NaturalForest, fontWeight = FontWeight.Bold)
                        Text("${stats.cleanSheets}", fontSize = 10.sp, color = NaturalEarthAmber, fontWeight = FontWeight.Bold)
                        Text(String.format(Locale.US, "%.1f", stats.avgMatchRating), fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
