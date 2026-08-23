package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.utils.AppLanguage
import com.example.utils.LocalizationManager
import com.example.viewmodel.ActiveScreenTab

data class NavItemSpec(
    val tab: ActiveScreenTab,
    val stringKey: String,
    val fallbackTitle: String,
    val icon: ImageVector
)

@Composable
fun LandscapeNavigationRail(
    currentTab: ActiveScreenTab,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onTabSelected: (ActiveScreenTab) -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val navItems = listOf(
        NavItemSpec(ActiveScreenTab.TACTICS_SQUAD, "nav_tactics", "Tactics", Icons.Filled.SportsSoccer),
        NavItemSpec(ActiveScreenTab.LIVE_MATCH, "nav_match", "Match", Icons.Filled.PlayArrow),
        NavItemSpec(ActiveScreenTab.TRAINING, "nav_training", "Training", Icons.Filled.FitnessCenter),
        NavItemSpec(ActiveScreenTab.TRANSFERS, "nav_transfers", "Transfers", Icons.Filled.PersonSearch),
        NavItemSpec(ActiveScreenTab.CLUB_MANAGEMENT, "nav_club", "Club", Icons.Filled.AccountBalance),
        NavItemSpec(ActiveScreenTab.CALENDAR, "nav_calendar", "Calendar", Icons.Filled.CalendarMonth),
        NavItemSpec(ActiveScreenTab.LEAGUE_TABLE, "nav_league", "League", Icons.Filled.EmojiEvents),
        NavItemSpec(ActiveScreenTab.ANALYTICS, "nav_stats", "Stats", Icons.Filled.BarChart),
        NavItemSpec(ActiveScreenTab.MATCH_HISTORY, "nav_history", "History", Icons.Filled.History),
        NavItemSpec(ActiveScreenTab.MANAGER_PROFILE, "nav_profile", "Profile", Icons.Filled.Person),
        NavItemSpec(ActiveScreenTab.MULTIPLAYER_LOBBY, "nav_online", "Online", Icons.Filled.Public)
    )

    Surface(
        modifier = modifier
            .width(76.dp)
            .fillMaxHeight(),
        color = StadiumSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Branding Logo Mark
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NaturalForest)
                    .border(1.dp, NaturalForestLight, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FM",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(Modifier.height(4.dp))

            // Navigation Tab Items List (Scrollable for small landscape screens)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(navItems, key = { it.tab.name }) { item ->
                    val isSelected = currentTab == item.tab
                    val localizedTitle = LocalizationManager.getString(item.stringKey, currentLanguage)
                    NavThumbItem(
                        title = if (localizedTitle.isNotBlank()) localizedTitle else item.fallbackTitle,
                        icon = item.icon,
                        isSelected = isSelected,
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            onTabSelected(item.tab)
                        },
                        testTag = "nav_${item.fallbackTitle.lowercase()}"
                    )
                }

                item {
                    Spacer(Modifier.height(2.dp))
                    NavThumbItem(
                        title = LocalizationManager.getString("settings", currentLanguage),
                        icon = Icons.Filled.Settings,
                        isSelected = false,
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            onOpenSettings()
                        },
                        testTag = "nav_settings"
                    )
                }
            }
        }
    }
}

@Composable
private fun NavThumbItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = NaturalForest),
                onClick = onClick
            )
            .testTag(testTag),
        color = if (isSelected) NaturalForest.copy(alpha = 0.15f) else Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NaturalForest) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) NaturalForest else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (isSelected) NaturalForest else TextSecondary,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
        }
    }
}
