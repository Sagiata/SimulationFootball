package com.example.ui.multiplayer

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
import com.example.model.OpponentClub
import com.example.model.UserProfile
import com.example.ui.components.HapticController
import com.example.ui.theme.*

data class OnlineLobbyRoom(
    val id: String,
    val roomName: String,
    val hostManager: String,
    val region: String,
    val eloRating: Int,
    val pingMs: Int,
    val status: String
)

@Composable
fun MultiplayerScreen(
    userProfile: UserProfile,
    onChallengeManager: (OnlineLobbyRoom) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var isSearching by remember { mutableStateOf(false) }

    val lobbyRooms = remember {
        listOf(
            OnlineLobbyRoom("room_1", "Tokyo Apex League L1", "Kenji Sato", "Asia-East (Tokyo)", 1820, 24, "Ready for Match"),
            OnlineLobbyRoom("room_2", "London Derby Open", "Arthur Pendelton", "EU-West (London)", 1750, 42, "In Lobby"),
            OnlineLobbyRoom("room_3", "São Paulo Champions", "Mateo Silva", "SA-East (São Paulo)", 1890, 68, "Ready for Match"),
            OnlineLobbyRoom("room_4", "New York MLS Hub", "David Vance", "US-East (Virginia)", 1680, 31, "Waiting for Host"),
            OnlineLobbyRoom("room_5", "Berlin Tactical Cup", "Klaus Becker", "EU-Central (Frankfurt)", 1795, 38, "Ready for Match")
        )
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Left Column: Matchmaking & Auth Status
        Surface(
            modifier = Modifier
                .weight(0.9f)
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Public, contentDescription = null, tint = NaturalForest, modifier = Modifier.size(20.dp))
                        Text(
                            text = "GLOBAL LOBBIES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NaturalForest,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = StadiumSurfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Manager: ${userProfile.managerName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Club: ${userProfile.clubName}", fontSize = 10.sp, color = TextSecondary)
                            Text("ELO: ${userProfile.eloRating} Rating", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalEarthAmber)
                            Text("Encrypted Token: Active ✅", fontSize = 9.sp, color = NaturalForest)
                        }
                    }
                }

                Button(
                    onClick = {
                        HapticController.performTactileClick(haptic, context)
                        isSearching = !isSearching
                    },
                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_quick_matchmaking"),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSearching) NaturalTerracotta else NaturalForest),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(if (isSearching) Icons.Filled.Close else Icons.Filled.WifiTethering, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (isSearching) "Cancel Search" else "Quick Match", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Right Column: Available Online Lobbies
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
                Text(
                    text = "ACTIVE MULTIPLAYER ROOMS (${lobbyRooms.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NaturalForest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(lobbyRooms, key = { it.id }) { room ->
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
                                Column {
                                    Text(room.roomName, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = TextPrimary)
                                    Text("Host: ${room.hostManager} • ${room.region}", fontSize = 9.5.sp, color = TextSecondary)
                                    Text("Rating: ${room.eloRating} Elo • Ping: ${room.pingMs}ms", fontSize = 9.5.sp, color = NaturalForest, fontWeight = FontWeight.SemiBold)
                                }

                                Button(
                                    onClick = {
                                        HapticController.performTactileClick(haptic, context)
                                        onChallengeManager(room)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Challenge", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
