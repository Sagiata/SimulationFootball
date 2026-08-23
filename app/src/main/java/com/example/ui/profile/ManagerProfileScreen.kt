package com.example.ui.profile

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
import com.example.model.UserProfile
import com.example.ui.components.HapticController
import com.example.ui.theme.*

@Composable
fun ManagerProfileScreen(
    userProfile: UserProfile,
    onOpenSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Left Column: Identity, Encrypted Auth & Security Credentials
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
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NaturalForest)
                                .border(1.5.dp, NaturalForestLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⚽", fontSize = 16.sp)
                        }

                        Column {
                            Text(
                                text = userProfile.managerName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "${userProfile.clubName} • ${userProfile.region}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NaturalForest,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = StadiumBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "SECURE ENCRYPTED AUTHENTICATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NaturalEarthAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

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
                                text = "AUTH STATUS: SECURE & VERIFIED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NaturalForest,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 8.sp
                                )
                            )
                            Text(
                                text = "Token: ${userProfile.encryptedAuthToken.take(24)}...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 8.sp
                                )
                            )
                            Text(
                                text = "Hash: ${userProfile.securityHash.take(24)}...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "TACTICAL PHILOSOPHY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NaturalForest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = userProfile.clubPhilosophy,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontSize = 8.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                HapticController.performTactileClick(haptic, context)
                                onOpenSettings()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp)
                                .testTag("profile_settings_btn"),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NaturalForest),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Filled.Settings, contentDescription = null, tint = NaturalForest, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("SETTINGS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = NaturalForest)
                        }

                        Button(
                            onClick = {
                                HapticController.performTactileClick(haptic, context)
                                onLogout()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp)
                                .testTag("profile_logout_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = NaturalTerracotta),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Filled.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("LOGOUT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Global Server Info & Account Status
                Surface(
                    color = StadiumSurfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Elo Rating: ${userProfile.eloRating}", fontSize = 8.5.sp, color = NaturalEarthAmber, fontWeight = FontWeight.Bold)
                        Text("Reputation: ⭐ ${userProfile.reputationStars}", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = NaturalForest)
                    }
                }
            }
        }

        // Right Column: Career Statistics & Trophy Honours
        Surface(
            modifier = Modifier
                .weight(1.25f)
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
                        text = "MANAGERIAL HONOURS & CAREER RECORD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NaturalForest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StatCard("CAREER WINS", "${userProfile.careerWins}", NaturalForest, Modifier.weight(1f))
                        StatCard("DRAWS", "${userProfile.careerDraws}", NaturalEarthAmber, Modifier.weight(1f))
                        StatCard("LOSSES", "${userProfile.careerLosses}", NaturalTerracotta, Modifier.weight(1f))
                        StatCard("TROPHIES", "🏆 ${userProfile.totalTrophies}", NaturalEarthAmber, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "HONOURS CABINET",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val honoursList = listOf(
                        "Apex Champions Division Winner",
                        "Continental Super Cup Finalist",
                        "Manager of the Season Award",
                        "Top Youth Development System"
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(honoursList) { honour ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = StadiumSurfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(NaturalForest.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.EmojiEvents,
                                            contentDescription = null,
                                            tint = NaturalEarthAmber,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = honour,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.5.sp,
                                                color = TextPrimary
                                            )
                                        )
                                        Text(
                                            text = "Unlocked in Career Mode",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 7.5.sp,
                                                color = TextSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Surface(
                    color = StadiumSurfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Transfer Budget", fontSize = 8.5.sp, color = TextSecondary)
                        Text("$${String.format(java.util.Locale.US, "%.1f", userProfile.transferBudgetMillions)}M", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = NaturalForest)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = StadiumSurfaceVariant,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 7.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}
