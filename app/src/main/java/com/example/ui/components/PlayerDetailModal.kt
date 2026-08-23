package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Player
import com.example.model.PlayerAttributes
import com.example.ui.theme.*

@Composable
fun PlayerDetailModal(
    player: Player?,
    onDismiss: () -> Unit
) {
    if (player == null) return

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.92f)
                .testTag("player_detail_modal"),
            color = StadiumSurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Header: Player Name, OVR, Flag, and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // OVR Rating Circle
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(NaturalForest.copy(alpha = 0.15f))
                                .border(1.5.dp, NaturalForest, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${player.overallRating}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = NaturalForest,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = "OVR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = player.flagEmoji,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = player.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                )
                            }
                            Text(
                                text = "${player.primaryRole.fullTitle} • Age: ${player.age} • Squad #${player.number}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            onDismiss()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Split Content Area: Left = Key Bio & Value, Right = Attributes Breakdown
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left Column: Contract, Morale, Stamina, Market Value
                    Surface(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        color = StadiumSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "CONTRACT & STATUS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NaturalForest,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )

                                InfoRow(label = "Market Value", value = "$${String.format(java.util.Locale.US, "%.1f", player.marketValueMillions)}M")
                                InfoRow(label = "Weekly Wage", value = "$${player.weeklyWageThousands}k / wk")
                                InfoRow(label = "Potential", value = "${player.potentialRating} Max")
                                InfoRow(label = "Morale", value = "${player.morale}%")
                                InfoRow(label = "Condition", value = player.condition)
                                InfoRow(label = "Stamina", value = "${player.stamina}%")
                            }

                            // Season Summary list
                            Column {
                                Text(
                                    text = "SEASON PERFORMANCE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        color = NaturalForest.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Apps: ${player.seasonStats.appearances} | Goals: ${player.seasonStats.goals}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = NaturalForest,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        color = NaturalEarthAmber.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Rating: ${player.seasonStats.avgMatchRating}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = NaturalEarthAmber,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Right Column: Full Attributes Matrix
                    Surface(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight(),
                        color = StadiumSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            item {
                                Text(
                                    text = "TECHNICAL & PHYSICAL ATTRIBUTES",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NaturalForest,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }

                            val attrs = player.attributes
                            item { AttributeBar(label = "Pace / Speed", value = attrs.pace) }
                            item { AttributeBar(label = "Shooting / Finishing", value = attrs.shooting) }
                            item { AttributeBar(label = "Passing & Vision", value = attrs.passing) }
                            item { AttributeBar(label = "Dribbling / Agility", value = attrs.dribbling) }
                            item { AttributeBar(label = "Defending / Tackling", value = attrs.defending) }
                            item { AttributeBar(label = "Physical / Strength", value = attrs.physicality) }
                            item { AttributeBar(label = "Tactical IQ", value = attrs.tacticalIq) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 9.sp, color = TextSecondary)
        Text(text = value, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun AttributeBar(label: String, value: Int) {
    val barColor = when {
        value >= 85 -> RatingHigh
        value >= 75 -> RatingMed
        else -> RatingLow
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 9.sp, color = TextPrimary)
            Text(text = "$value", fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = barColor)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = barColor,
            trackColor = StadiumBorder
        )
    }
}
