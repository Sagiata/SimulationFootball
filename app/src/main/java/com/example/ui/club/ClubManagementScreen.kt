package com.example.ui.club

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun ClubManagementScreen(
    userProfile: UserProfile,
    financialStatement: ClubFinancialStatement,
    boardObjectives: List<BoardObjective>,
    pressQuestions: List<PressConferenceQuestion>,
    onUpgradeFacility: (String) -> Boolean,
    onAnswerPressQuestion: (String, PressResponseOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Finances & Facilities, 1 = Board Objectives, 2 = Press Room

    BoxWithConstraints(modifier = modifier.fillMaxSize().padding(8.dp)) {
        val isWide = maxWidth >= 800.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Header with tab toggles
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = NaturalForest, modifier = Modifier.size(20.dp))
                            Text(
                                text = "${userProfile.clubName} • Operations & Boardroom",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                        }
                        Text(
                            text = "Manager: ${userProfile.managerName} • Rating: ${userProfile.eloRating} ELO",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = TextSecondary)
                        )
                    }

                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = NaturalForest,
                                activeContentColor = Color.White,
                                inactiveContainerColor = StadiumSurfaceVariant,
                                inactiveContentColor = TextSecondary
                            )
                        ) {
                            Text("Finances & Infra", fontSize = 11.sp)
                        }
                        SegmentedButton(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = NaturalForest,
                                activeContentColor = Color.White,
                                inactiveContainerColor = StadiumSurfaceVariant,
                                inactiveContentColor = TextSecondary
                            )
                        ) {
                            Text("Board Objectives (${boardObjectives.size})", fontSize = 11.sp)
                        }
                        SegmentedButton(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = NaturalTerracotta,
                                activeContentColor = Color.White,
                                inactiveContainerColor = StadiumSurfaceVariant,
                                inactiveContentColor = TextSecondary
                            )
                        ) {
                            Text("Press Room (${pressQuestions.size})", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> FinancesAndFacilitiesTab(
                    userProfile = userProfile,
                    financialStatement = financialStatement,
                    onUpgradeFacility = onUpgradeFacility,
                    isWide = isWide
                )
                1 -> BoardObjectivesTab(boardObjectives = boardObjectives)
                2 -> PressRoomTab(
                    pressQuestions = pressQuestions,
                    onAnswer = onAnswerPressQuestion
                )
            }
        }
    }
}

@Composable
fun FinancesAndFacilitiesTab(
    userProfile: UserProfile,
    financialStatement: ClubFinancialStatement,
    onUpgradeFacility: (String) -> Boolean,
    isWide: Boolean
) {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Financial Breakdown Card
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = StadiumSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Weekly Financial Statement",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )

                // Revenue breakdown
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StadiumSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Weekly Income Streams", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NaturalForest))
                        FinanceRow("Matchday Ticket Sales (${financialStatement.stadiumCapacity.toInt() / 1000}k cap)", "+$${String.format("%.2f", financialStatement.weeklyTicketRevenue)}M", NaturalForest)
                        FinanceRow("Commercial & Sponsorships", "+$${String.format("%.2f", financialStatement.weeklySponsorshipIncome)}M", NaturalForest)
                        FinanceRow("Merchandising & Global Kits", "+$${String.format("%.2f", financialStatement.weeklyMerchandiseRevenue)}M", NaturalForest)
                    }
                }

                // Expenditure breakdown
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StadiumSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Weekly Operational Costs", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NaturalTerracotta))
                        FinanceRow("Player & Staff Wages", "-$${String.format("%.2f", financialStatement.weeklyWageExpenditure)}M", NaturalTerracotta)
                        FinanceRow("Facility Upkeep & Staff", "-$${String.format("%.2f", financialStatement.weeklyFacilityMaintenance)}M", NaturalTerracotta)
                    }
                }

                // Net summary
                val net = (financialStatement.weeklyTicketRevenue + financialStatement.weeklySponsorshipIncome + financialStatement.weeklyMerchandiseRevenue) - (financialStatement.weeklyWageExpenditure + financialStatement.weeklyFacilityMaintenance)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (net >= 0) NaturalForest.copy(alpha = 0.15f) else NaturalTerracotta.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Net Weekly Cash Flow", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                        Text(
                            text = "${if (net >= 0) "+" else ""}$${String.format("%.2f", net)}M / week",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = if (net >= 0) NaturalForest else NaturalTerracotta
                        )
                    }
                }
            }
        }

        // Facility Upgrades Card
        Card(
            modifier = Modifier.weight(1.2f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = StadiumSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
            shape = RoundedCornerShape(10.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text(
                        text = "Club Infrastructure & Upgrades",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                }

                item {
                    FacilityUpgradeCard(
                        title = "Training Grounds",
                        currentTier = financialStatement.trainingFacilityTier.title,
                        perk = financialStatement.trainingFacilityTier.perkDesc,
                        nextCost = financialStatement.trainingFacilityTier.upgradeCostMillions,
                        onUpgrade = { onUpgradeFacility("training") }
                    )
                }

                item {
                    FacilityUpgradeCard(
                        title = "Youth Academy Center",
                        currentTier = financialStatement.youthAcademyTier.title,
                        perk = financialStatement.youthAcademyTier.perkDesc,
                        nextCost = financialStatement.youthAcademyTier.upgradeCostMillions,
                        onUpgrade = { onUpgradeFacility("youth") }
                    )
                }

                item {
                    FacilityUpgradeCard(
                        title = "Stadium Capacity Expansion",
                        currentTier = "${financialStatement.stadiumCapacity} Seats",
                        perk = "+12,000 Seats, +$0.40M Weekly Ticket Sales",
                        nextCost = 30.0,
                        onUpgrade = { onUpgradeFacility("stadium") }
                    )
                }
            }
        }
    }
}

@Composable
fun FinanceRow(label: String, amount: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Text(amount, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun FacilityUpgradeCard(
    title: String,
    currentTier: String,
    perk: String,
    nextCost: Double,
    onUpgrade: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                Text("Tier: $currentTier", fontSize = 11.sp, color = NaturalForest, fontWeight = FontWeight.SemiBold)
                Text(perk, fontSize = 10.sp, color = TextSecondary)
            }

            if (nextCost > 0) {
                Button(
                    onClick = onUpgrade,
                    colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Upgrade ($${String.format("%.0f", nextCost)}M)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Text("MAX TIER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalForest)
            }
        }
    }
}

@Composable
fun BoardObjectivesTab(boardObjectives: List<BoardObjective>) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(boardObjectives, key = { it.id }) { obj ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(obj.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(
                                if (obj.priority == "Crucial") NaturalTerracotta.copy(alpha = 0.2f) else NaturalEarthAmber.copy(alpha = 0.2f)
                            ).padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                obj.priority.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (obj.priority == "Crucial") NaturalTerracotta else NaturalEarthAmber
                            )
                        }
                    }

                    Text(obj.description, fontSize = 11.sp, color = TextSecondary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Target: ${obj.targetDesc}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = NaturalForest)
                        Text("${obj.progressPct}% Completed", fontSize = 10.sp, color = TextSecondary)
                    }

                    LinearProgressIndicator(
                        progress = { obj.progressPct / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = if (obj.progressPct >= 80) NaturalForest else NaturalEarthAmber,
                        trackColor = StadiumBorder
                    )
                }
            }
        }
    }
}

@Composable
fun PressRoomTab(
    pressQuestions: List<PressConferenceQuestion>,
    onAnswer: (String, PressResponseOption) -> Unit
) {
    if (pressQuestions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active press questions at this moment.", color = TextSecondary)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(pressQuestions, key = { it.id }) { q ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Mic, contentDescription = null, tint = NaturalTerracotta, modifier = Modifier.size(18.dp))
                        Text(
                            text = "${q.reporterName} (${q.mediaOutlet})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                    }

                    Text(
                        text = "\"${q.questionText}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    )

                    Text("Choose Manager Response:", fontSize = 10.sp, color = TextSecondary)

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        q.options.forEach { opt ->
                            OutlinedButton(
                                onClick = { onAnswer(q.id, opt) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = StadiumSurfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(opt.answerText, fontSize = 11.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                    Text(
                                        "[${opt.tone} • Morale +${opt.moraleImpact}]",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalForest
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
