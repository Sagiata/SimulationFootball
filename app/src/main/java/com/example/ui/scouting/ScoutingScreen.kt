package com.example.ui.scouting

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.HapticController
import com.example.ui.theme.*

@Composable
fun ScoutingScreen(
    scoutingPool: List<ScoutProspect>,
    incomingBids: List<IncomingTransferBid>,
    selectedRegion: ScoutRegion,
    userProfile: UserProfile,
    activeNegotiation: ContractNegotiation?,
    onSelectRegion: (ScoutRegion) -> Unit,
    onRefreshRegion: (ScoutRegion) -> Unit,
    onAdvanceScout: (String) -> Unit,
    onStartNegotiation: (ScoutProspect) -> Unit,
    onUpdateNegotiation: (Int, Double, Double, Int, String) -> Unit,
    onSubmitNegotiationOffer: () -> Unit,
    onFinalizeSigning: () -> Boolean,
    onDismissNegotiation: () -> Unit,
    onRespondToBid: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var selectedTopTab by remember { mutableStateOf(0) } // 0 = Scout Prospects, 1 = Incoming Bids

    val regionProspects = scoutingPool.filter { it.region == selectedRegion }

    Column(modifier = modifier.fillMaxSize().padding(4.dp)) {
        // Top Switcher
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            colors = CardDefaults.cardColors(containerColor = StadiumSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.PersonSearch, contentDescription = null, tint = NaturalForest, modifier = Modifier.size(20.dp))
                    Text(
                        "Global Transfer & Scouting Network",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                }

                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = selectedTopTab == 0,
                        onClick = { selectedTopTab = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = NaturalForest,
                            activeContentColor = Color.White,
                            inactiveContainerColor = StadiumSurfaceVariant,
                            inactiveContentColor = TextSecondary
                        )
                    ) {
                        Text("Scouting Prospects (${scoutingPool.size})", fontSize = 10.sp)
                    }

                    SegmentedButton(
                        selected = selectedTopTab == 1,
                        onClick = { selectedTopTab = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = NaturalTerracotta,
                            activeContentColor = Color.White,
                            inactiveContainerColor = StadiumSurfaceVariant,
                            inactiveContentColor = TextSecondary
                        )
                    ) {
                        Text("Transfer Bids (${incomingBids.size})", fontSize = 10.sp)
                    }
                }
            }
        }

        if (selectedTopTab == 0) {
            // Scouting Split Screen
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Left Column: Regions
                Surface(
                    modifier = Modifier.weight(0.85f).fillMaxHeight(),
                    color = StadiumSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                        Text("SCOUTING REGIONS", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                        Spacer(modifier = Modifier.height(4.dp))

                        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(ScoutRegion.entries) { region ->
                                val isSelected = region == selectedRegion
                                val count = scoutingPool.count { it.region == region }
                                Surface(
                                    modifier = Modifier.fillMaxWidth().height(38.dp).clickable {
                                        HapticController.performTactileClick(haptic, context)
                                        onSelectRegion(region)
                                    },
                                    color = if (isSelected) StadiumSurfaceVariant else StadiumSurface,
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NaturalForest else StadiumBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(region.regionName, fontSize = 10.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) NaturalForest else TextPrimary)
                                        Text("$count", fontSize = 9.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                HapticController.performTactileClick(haptic, context)
                                onRefreshRegion(selectedRegion)
                            },
                            modifier = Modifier.fillMaxWidth().height(32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Dispatch Scouts", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Right Column: Prospects
                Surface(
                    modifier = Modifier.weight(1.4f).fillMaxHeight(),
                    color = StadiumSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("DISCOVERED TARGETS • ${selectedRegion.regionName}", style = MaterialTheme.typography.labelSmall.copy(color = NaturalForest, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                            Text("Budget: $${String.format(java.util.Locale.US, "%.1f", userProfile.transferBudgetMillions)}M", style = MaterialTheme.typography.labelSmall.copy(color = NaturalEarthAmber, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        if (regionProspects.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No scout reports currently in this region. Tap 'Dispatch Scouts'.", color = TextSecondary, fontSize = 11.sp)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(regionProspects, key = { it.id }) { prospect ->
                                    ProspectCard(
                                        prospect = prospect,
                                        userBudget = userProfile.transferBudgetMillions,
                                        onAdvanceScout = { onAdvanceScout(prospect.id) },
                                        onNegotiate = { onStartNegotiation(prospect) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Incoming Transfer Bids Tab
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = StadiumSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    Text("INCOMING CLUB TRANSFER OFFERS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NaturalTerracotta)
                    Spacer(Modifier.height(8.dp))

                    if (incomingBids.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No pending transfer bids from other clubs.", color = TextSecondary, fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(incomingBids, key = { it.id }) { bid ->
                                IncomingBidCard(
                                    bid = bid,
                                    onAccept = { onRespondToBid(bid.id, true) },
                                    onReject = { onRespondToBid(bid.id, false) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Contract Negotiation Modal Dialog
    if (activeNegotiation != null) {
        ContractNegotiationModal(
            negotiation = activeNegotiation,
            userBudget = userProfile.transferBudgetMillions,
            onUpdateValues = onUpdateNegotiation,
            onSubmitProposal = onSubmitNegotiationOffer,
            onFinalizeSigning = onFinalizeSigning,
            onDismiss = onDismissNegotiation
        )
    }
}

@Composable
fun ProspectCard(
    prospect: ScoutProspect,
    userBudget: Double,
    onAdvanceScout: () -> Unit,
    onNegotiate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(StadiumSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(prospect.flag, fontSize = 16.sp)
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(prospect.name, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = TextPrimary)
                        Surface(
                            color = NaturalForest.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(prospect.primaryRole.abbreviation, color = NaturalForest, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                    Text(
                        "${prospect.currentClub} • Age: ${prospect.age} • OVR: ${if (prospect.isScouted) prospect.currentOvr.toString() else "??-??"} • POT: ${prospect.maxPotential}",
                        fontSize = 9.5.sp,
                        color = TextSecondary
                    )
                    Text(
                        "Val: $${String.format(java.util.Locale.US, "%.1f", prospect.marketValueMillions)}M • Demand: $${prospect.wageWeeklyThousands}k/wk",
                        fontSize = 9.5.sp,
                        color = NaturalForest,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (!prospect.isScouted) {
                    Button(
                        onClick = onAdvanceScout,
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalEarthAmber),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Scout (${(prospect.scoutingProgress * 100).toInt()}%)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onNegotiate,
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Negotiate Contract", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun IncomingBidCard(
    bid: IncomingTransferBid,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(bid.playerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text("(${bid.playerRole.abbreviation} • ${bid.playerRating} OVR)", fontSize = 11.sp, color = TextSecondary)
                }
                Text("Bidding Club: ${bid.offeringClubName}", fontSize = 11.sp, color = NaturalTerracotta, fontWeight = FontWeight.SemiBold)
                Text("Offer Fee: $${String.format(java.util.Locale.US, "%.1f", bid.offerAmountMillions)}M (Market Value: $${String.format(java.util.Locale.US, "%.1f", bid.playerMarketValueMillions)}M)", fontSize = 11.sp, color = NaturalForest, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = NaturalTerracotta),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Reject", fontSize = 11.sp)
                }

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Accept ($${String.format(java.util.Locale.US, "%.1f", bid.offerAmountMillions)}M)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ContractNegotiationModal(
    negotiation: ContractNegotiation,
    userBudget: Double,
    onUpdateValues: (Int, Double, Double, Int, String) -> Unit,
    onSubmitProposal: () -> Unit,
    onFinalizeSigning: () -> Boolean,
    onDismiss: () -> Unit
) {
    var offeredWage by remember(negotiation.offeredWageThousands) { mutableStateOf(negotiation.offeredWageThousands.toFloat()) }
    var offeredBonus by remember(negotiation.offeredSigningBonusMillions) { mutableStateOf(negotiation.offeredSigningBonusMillions.toFloat()) }
    var offeredYears by remember(negotiation.offeredContractYears) { mutableStateOf(negotiation.offeredContractYears) }
    var offeredRole by remember(negotiation.offeredSquadRole) { mutableStateOf(negotiation.offeredSquadRole) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Contract Negotiations: ${negotiation.playerName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Agent Feedback Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when (negotiation.status) {
                            NegotiationStatus.ACCEPTED_BY_AGENT -> NaturalForest.copy(alpha = 0.15f)
                            NegotiationStatus.REJECTED_BY_AGENT -> NaturalTerracotta.copy(alpha = 0.15f)
                            else -> StadiumSurfaceVariant
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Agent Mood: ${negotiation.agentMood}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NaturalEarthAmber)
                        Text(negotiation.agentFeedbackMessage, fontSize = 10.5.sp, color = TextPrimary)
                    }
                }

                // Sliders
                Text("Weekly Wage: $${offeredWage.toInt()}k/wk (Demand: $${negotiation.initialWageDemandThousands}k)", fontSize = 11.sp, color = TextSecondary)
                Slider(
                    value = offeredWage,
                    onValueChange = {
                        offeredWage = it
                        onUpdateValues(it.toInt(), offeredBonus.toDouble(), negotiation.offeredReleaseClauseMillions, offeredYears, offeredRole)
                    },
                    valueRange = 10f..400f,
                    colors = SliderDefaults.colors(thumbColor = NaturalForest, activeTrackColor = NaturalForest)
                )

                Text("Signing Bonus: $${String.format(java.util.Locale.US, "%.1f", offeredBonus)}M", fontSize = 11.sp, color = TextSecondary)
                Slider(
                    value = offeredBonus,
                    onValueChange = {
                        offeredBonus = it
                        onUpdateValues(offeredWage.toInt(), it.toDouble(), negotiation.offeredReleaseClauseMillions, offeredYears, offeredRole)
                    },
                    valueRange = 0.1f..15.0f,
                    colors = SliderDefaults.colors(thumbColor = NaturalEarthAmber, activeTrackColor = NaturalEarthAmber)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Contract Length: $offeredYears Years", fontSize = 11.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (2..5).forEach { yr ->
                            FilledTonalButton(
                                onClick = {
                                    offeredYears = yr
                                    onUpdateValues(offeredWage.toInt(), offeredBonus.toDouble(), negotiation.offeredReleaseClauseMillions, yr, offeredRole)
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("${yr}Y", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (negotiation.status == NegotiationStatus.ACCEPTED_BY_AGENT) {
                Button(
                    onClick = { onFinalizeSigning() },
                    colors = ButtonDefaults.buttonColors(containerColor = NaturalForest)
                ) {
                    Text("Sign Player to Squad")
                }
            } else {
                Button(
                    onClick = onSubmitProposal,
                    colors = ButtonDefaults.buttonColors(containerColor = NaturalEarthAmber)
                ) {
                    Text("Submit Offer to Agent")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
