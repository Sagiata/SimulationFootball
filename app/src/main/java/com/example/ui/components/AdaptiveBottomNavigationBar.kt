package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

@Composable
fun AdaptiveBottomNavigationBar(
    currentTab: ActiveScreenTab,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onTabSelected: (ActiveScreenTab) -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val navItems = listOf(
        NavItemSpec(ActiveScreenTab.MAIN_HUB, "nav_hub", "Hub", Icons.Filled.Home),
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
            .fillMaxWidth()
            .height(56.dp),
        color = StadiumSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            navItems.forEach { item ->
                val isSelected = currentTab == item.tab
                val localizedTitle = LocalizationManager.getString(item.stringKey, currentLanguage)
                val displayTitle = if (localizedTitle.isNotBlank()) localizedTitle else item.fallbackTitle

                AdaptiveBottomNavItem(
                    title = displayTitle,
                    icon = item.icon,
                    isSelected = isSelected,
                    onClick = {
                        HapticController.performTactileClick(haptic, context)
                        onTabSelected(item.tab)
                    },
                    testTag = "bottom_nav_${item.fallbackTitle.lowercase()}"
                )
            }

            // Quick Settings Item
            AdaptiveBottomNavItem(
                title = LocalizationManager.getString("settings", currentLanguage),
                icon = Icons.Filled.Settings,
                isSelected = false,
                onClick = {
                    HapticController.performTactileClick(haptic, context)
                    onOpenSettings()
                },
                testTag = "bottom_nav_settings"
            )
        }
    }
}

@Composable
private fun AdaptiveBottomNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val accentColor = AppTheme.colors.primaryAccent

    Surface(
        modifier = Modifier
            .widthIn(min = 60.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = accentColor),
                onClick = onClick
            )
            .testTag(testTag),
        color = if (isSelected) accentColor.copy(alpha = 0.16f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, accentColor) else null
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) accentColor else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) accentColor else TextSecondary,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
        }
    }
}
