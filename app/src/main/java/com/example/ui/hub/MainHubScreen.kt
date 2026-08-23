package com.example.ui.hub

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.utils.AudioEffectManager
import com.example.viewmodel.ActiveScreenTab

@Composable
fun MainHubScreen(
    userProfile: UserProfile,
    squad: List<Player>,
    tactics: TeamTactics,
    opponents: List<OpponentClub>,
    matchHistory: List<HistoricalMatchRecord>,
    financialStatement: ClubFinancialStatement,
    onNavigateTab: (ActiveScreenTab) -> Unit,
    onStartMatch: (OpponentClub) -> Unit,
    onInspectPlayer: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val nextOpponent = opponents.firstOrNull() ?: OpponentClub(
        id = "1",
        name = "Rival FC",
        shortName = "RFC",
        badgeColorHex = 0xFFFF3344,
        secondaryBadgeColorHex = 0xFF14241B,
        league = "Super League",
        overallRating = 85,
        attackRating = 84,
        midfieldRating = 83,
        defenseRating = 82,
        managerName = "Opponent Manager",
        formation = FormationType.F_433
    )
    val startingXI = squad.filter { it.isStarter }.take(11)
    val topScorer = squad.maxByOrNull { it.seasonStats.goals } ?: squad.firstOrNull()
    val topAssister = squad.maxByOrNull { it.seasonStats.assists }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0C1610),
                        Color(0xFF060B08),
                        Color(0xFF040705)
                    )
                )
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("main_hub_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. TOP STATUS & CLUB HEADER BAR
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF132219)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF263D2E), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Club + League Crest
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ClubCrest(
                                clubName = userProfile.clubName,
                                primaryColor = Color(userProfile.clubBadgeColor),
                                size = CrestSize.SMALL
                            )
                            Column {
                                Text(
                                    text = userProfile.clubName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Black
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    LeagueBadge(leagueName = userProfile.leagueName, size = 16.dp)
                                    Text(
                                        text = "${userProfile.leagueName} • ${userProfile.currentSeasonYear}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF88A090),
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        // National Team Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1B3825))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(userProfile.nationalTeamFlag, fontSize = 16.sp)
                            Column {
                                Text(
                                    text = userProfile.nationalTeam,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFFFD700),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "World Cup Track",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF88A090),
                                        fontSize = 8.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF263D2E))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Manager Stats Ticker (Budget, Confidence, Morale)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TRANSFER BUDGET",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF88A090), fontSize = 9.sp)
                            )
                            Text(
                                text = "$${String.format("%.1f", userProfile.transferBudgetMillions)}M",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color(0xFF00E5FF),
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "DEWAN DIREKSI",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF88A090), fontSize = 9.sp)
                            )
                            Text(
                                text = "${userProfile.boardConfidencePercent}% (A+)",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color(0xFF00FF87),
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "MORAL SKUAD",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF88A090), fontSize = 9.sp)
                            )
                            Text(
                                text = "🔥 Sangat Tinggi",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. HERO MATCHDAY FIXTURE CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14291D)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF00FF87), Color(0xFF00E5FF))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚽ PERTANDINGAN BERIKUTNYA",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF00FF87),
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "🏆 ${nextOpponent.league}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Matchup Banner: Home Club VS Away Club
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Home
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            ClubCrest(
                                clubName = userProfile.clubName,
                                primaryColor = Color(userProfile.clubBadgeColor),
                                size = CrestSize.MEDIUM
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = userProfile.clubName,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "HOME",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF00FF87),
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }

                        // VS Badge
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFF0B160F))
                                    .border(1.dp, Color(0xFF00FF87), CircleShape)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "VS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFFFFD700),
                                        fontWeight = FontWeight.Black
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "20:00 WIB",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF88A090),
                                    fontSize = 9.sp
                                )
                            )
                        }

                        // Away Opponent
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            ClubCrest(
                                clubName = nextOpponent.name,
                                primaryColor = Color(nextOpponent.badgeColorHex),
                                size = CrestSize.MEDIUM
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = nextOpponent.name,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "AWAY (OVR ${nextOpponent.overallRating})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFFF3366),
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🏟️ ${userProfile.clubStadium} • Cuaca Cerah • 62,500 Penonton",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFB0C4B8),
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Match CTA Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                HapticController.triggerImpact(haptic)
                                AudioEffectManager.playConfirm()
                                onStartMatch(nextOpponent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00FF87),
                                contentColor = Color(0xFF06150C)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("hub_play_match_button")
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MAINKAN PERTANDINGAN",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                HapticController.triggerClick(haptic)
                                onNavigateTab(ActiveScreenTab.TACTICS_SQUAD)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFF00FF87)))
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Icon(Icons.Filled.Settings, contentDescription = null, tint = Color(0xFF00E5FF))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TAKTIK", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 3. STARTING XI PITCH & STAR PLAYERS PREVIEW
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101E15)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF263D2E), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("📋", fontSize = 18.sp)
                            Text(
                                text = "STARTING XI & FORMASI",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Text(
                            text = "${tactics.formation.label} • ${tactics.mentality.label}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF00FF87),
                                fontWeight = FontWeight.Black
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Lineup Showcase with Dynamic Faces
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(startingXI) { player ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF182D20)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .width(88.dp)
                                    .clickable {
                                        HapticController.triggerClick(haptic)
                                        onInspectPlayer(player)
                                    }
                                    .border(1.dp, Color(0xFF2E4E38), RoundedCornerShape(10.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    PlayerAvatar(
                                        player = player,
                                        size = AvatarSize.SMALL
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = player.name.split(" ").lastOrNull() ?: player.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${player.primaryRole.name} • ${player.overallRating}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF00FF87),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                    // Stamina bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color(0xFF0C160F))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(player.stamina / 100f)
                                                .fillMaxHeight()
                                                .background(if (player.stamina > 70) Color(0xFF00FF87) else Color(0xFFFF9800))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. WORLD CUP & NATIONAL TEAM STATUS CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16251C)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(userProfile.nationalTeamFlag, fontSize = 34.sp)

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "ROAD TO WORLD CUP 2026",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black
                                )
                            )
                            Text(
                                text = "• ${userProfile.nationalTeam}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                            )
                        }
                        Text(
                            text = "Posisi: Peringkat 2 (${userProfile.worldCupGroup}) • 6 Poin",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFD0DDD4),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Laga Internasional Berikutnya: Kualifikasi Putaran 4",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF88A090),
                                fontSize = 9.sp
                            )
                        )
                    }

                    Icon(
                        Icons.Filled.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // 5. QUICK GAME HUB ACTION TILES (Like standard EA FC / FM menu tiles)
        item {
            Text(
                text = "PUSAT MANAJEMEN KLUB",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color(0xFF88A090),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HubActionTile(
                        title = "Taktik & Skuad",
                        subtitle = "Lineup, Peran & Set Piece",
                        icon = Icons.Filled.SportsSoccer,
                        tintColor = Color(0xFF00FF87),
                        onClick = { onNavigateTab(ActiveScreenTab.TACTICS_SQUAD) },
                        modifier = Modifier.weight(1f)
                    )

                    HubActionTile(
                        title = "Bursa Transfer",
                        subtitle = "Pemandu & Negosiasi Pemain",
                        icon = Icons.Filled.Search,
                        tintColor = Color(0xFF00E5FF),
                        onClick = { onNavigateTab(ActiveScreenTab.TRANSFERS) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HubActionTile(
                        title = "Pusat Latihan",
                        subtitle = "Akademi & Atribut Pemain",
                        icon = Icons.Filled.FitnessCenter,
                        tintColor = Color(0xFFFFD700),
                        onClick = { onNavigateTab(ActiveScreenTab.TRAINING) },
                        modifier = Modifier.weight(1f)
                    )

                    HubActionTile(
                        title = "Klasemen Liga",
                        subtitle = "Tabel, Statistik & Rekor",
                        icon = Icons.Filled.FormatListNumbered,
                        tintColor = Color(0xFFFF3366),
                        onClick = { onNavigateTab(ActiveScreenTab.LEAGUE_TABLE) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HubActionTile(
                        title = "Keuangan Klub",
                        subtitle = "Gaji, Sponsor & Tiket",
                        icon = Icons.Filled.AccountBalance,
                        tintColor = Color(0xFFB388FF),
                        onClick = { onNavigateTab(ActiveScreenTab.CLUB_MANAGEMENT) },
                        modifier = Modifier.weight(1f)
                    )

                    HubActionTile(
                        title = "Jadwal Kalender",
                        subtitle = "Simulasi Harian & Laga",
                        icon = Icons.Filled.CalendarMonth,
                        tintColor = Color(0xFFFFAB00),
                        onClick = { onNavigateTab(ActiveScreenTab.CALENDAR) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 6. TOP PERFORMER & RECENT FORM SPOTLIGHT
        if (topScorer != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF132219)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF263D2E), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PlayerAvatar(
                            player = topScorer,
                            size = AvatarSize.LARGE
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "BINTANG UTAMA SKUAD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black
                                )
                            )
                            Text(
                                text = topScorer.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${topScorer.seasonStats.goals} Gol • ${topScorer.seasonStats.assists} Assist • Rating: ${topScorer.seasonStats.avgMatchRating}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF00FF87),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Form indicator pills (W W D W W)
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            listOf("W", "W", "D", "W", "W").forEach { result ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (result) {
                                                "W" -> Color(0xFF00FF87)
                                                "D" -> Color(0xFFFFD700)
                                                else -> Color(0xFFFF3366)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = result,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF06140B),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HubActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tintColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .clickable {
                HapticController.triggerClick(haptic)
                AudioEffectManager.playClick()
                onClick()
            }
            .border(1.dp, Color(0xFF263D2E), RoundedCornerShape(12.dp))
            .testTag("hub_tile_${title.replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tintColor.copy(alpha = 0.15f))
                    .border(1.dp, tintColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tintColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF88A090),
                        fontSize = 9.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
