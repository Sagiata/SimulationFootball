package com.example.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalendarFixture
import com.example.ui.components.HapticController
import com.example.ui.theme.*
import com.example.utils.AppLanguage
import com.example.utils.LocalizationManager

data class CalendarDayModel(
    val dayNumber: Int,
    val dateString: String,
    val dayOfWeek: String,
    val fixture: CalendarFixture? = null,
    val isToday: Boolean = false,
    val eventType: CalendarEventType = CalendarEventType.TRAINING,
    val eventDescription: String = "Squad Tactical Training"
)

enum class CalendarEventType(val label: String, val icon: ImageVector) {
    MATCH("Matchday", Icons.Filled.SportsSoccer),
    TRAINING("Training", Icons.Filled.FitnessCenter),
    PRESS("Press Conf", Icons.Filled.Mic),
    REST("Rest Day", Icons.Filled.Hotel),
    DEADLINE("Transfer Deadline", Icons.Filled.Timer);

    val color: Color
        @Composable
        get() = when (this) {
            MATCH -> AppTheme.colors.primaryAccent
            TRAINING -> AppTheme.colors.secondaryAccent
            PRESS -> AppTheme.colors.amber
            REST -> AppTheme.colors.textSecondary
            DEADLINE -> AppTheme.colors.red
        }
}

@Composable
fun CalendarScreen(
    calendarFixtures: List<CalendarFixture>,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onAdvanceDay: () -> Unit,
    onStartMatchFromCalendar: (CalendarFixture) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    // Build authentic FC26 31-day month grid mapping fixtures
    val daysInMonth = remember(calendarFixtures) {
        val list = mutableListOf<CalendarDayModel>()
        val dayNames = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
        
        for (day in 1..31) {
            val dow = dayNames[(day - 1) % 7]
            val dateStr = "Aug $day, 2026"
            val matchedFixture = calendarFixtures.find { it.dateString == dateStr || (day in listOf(5, 12, 19, 24, 28) && it.id.endsWith("$day")) }
                ?: calendarFixtures.getOrNull((day / 6) % calendarFixtures.size.coerceAtLeast(1))?.takeIf { day in listOf(5, 12, 19, 24, 29) }

            val eventType = when {
                matchedFixture != null -> CalendarEventType.MATCH
                day in listOf(1, 15, 31) -> CalendarEventType.DEADLINE
                day in listOf(4, 11, 18, 23) -> CalendarEventType.PRESS
                day % 3 == 0 -> CalendarEventType.REST
                else -> CalendarEventType.TRAINING
            }

            val desc = when (eventType) {
                CalendarEventType.MATCH -> "League Match vs ${matchedFixture?.awayTeam ?: "Rivals"}"
                CalendarEventType.DEADLINE -> "Transfer Window Activity"
                CalendarEventType.PRESS -> "Pre-match Press Conference"
                CalendarEventType.REST -> "Player Recovery & Rest"
                CalendarEventType.TRAINING -> "Tactical Drill & High Pressing"
            }

            list.add(
                CalendarDayModel(
                    dayNumber = day,
                    dateString = dateStr,
                    dayOfWeek = dow,
                    fixture = matchedFixture,
                    isToday = (day == 24),
                    eventType = eventType,
                    eventDescription = desc
                )
            )
        }
        list
    }

    var selectedDay by remember { mutableStateOf(daysInMonth.find { it.isToday } ?: daysInMonth.first()) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Left Area: Authentic EA FC26 Monthly Grid View
        Surface(
            modifier = Modifier
                .weight(1.5f)
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
                // FC26 Month Header & Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = NaturalForest,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "AUGUST 2026",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "EA FC 26 CAREER CALENDAR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp
                            )
                        )
                    }

                    // Legend Badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendBadge(icon = Icons.Filled.SportsSoccer, color = NaturalForest, label = "Match")
                        LegendBadge(icon = Icons.Filled.FitnessCenter, color = NaturalForestLight, label = "Training")
                        LegendBadge(icon = Icons.Filled.Mic, color = NaturalEarthAmber, label = "Press")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Day of Week Column Headers (MON - SUN)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").forEach { dow ->
                        Text(
                            text = dow,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (dow == "SAT" || dow == "SUN") NaturalForest else TextSecondary,
                                fontWeight = FontWeight.Black,
                                fontSize = 8.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // FC26 7-column Calendar Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(daysInMonth, key = { it.dayNumber }) { dayModel ->
                        val isSelected = selectedDay.dayNumber == dayModel.dayNumber
                        val isToday = dayModel.isToday

                        CalendarGridCell(
                            day = dayModel,
                            isSelected = isSelected,
                            isToday = isToday,
                            onClick = {
                                HapticController.performTactileClick(haptic, context)
                                selectedDay = dayModel
                            }
                        )
                    }
                }
            }
        }

        // Right Area: Selected Day Agenda, Match Details & Simulation Controls
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Day Header Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = StadiumSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedDay.isToday) NaturalForest else StadiumBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = selectedDay.dateString,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = "${selectedDay.dayOfWeek} • ${selectedDay.eventType.label}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = selectedDay.eventType.color,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }

                            if (selectedDay.isToday) {
                                Surface(
                                    color = NaturalForest,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "TODAY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 7.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Scheduled Event Details
                    if (selectedDay.fixture != null) {
                        val fixture = selectedDay.fixture!!
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = StadiumSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NaturalForest.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = fixture.competition,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NaturalEarthAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp
                                        )
                                    )
                                    Text(
                                        text = if (fixture.isUserMatch) "HOME" else "AWAY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NaturalForest,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp
                                        )
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = fixture.homeTeam,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 10.sp
                                        ),
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = if (fixture.isPlayed) "${fixture.homeScore} - ${fixture.awayScore}" else "VS",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = NaturalForest,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                    Text(
                                        text = fixture.awayTeam,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 10.sp
                                        ),
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                }

                                if (!fixture.isPlayed) {
                                    Button(
                                        onClick = {
                                            HapticController.performTactileClick(haptic, context)
                                            onStartMatchFromCalendar(fixture)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("PLAY THIS FIXTURE", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        // Regular Day Activity (Training / Rest / Press)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = StadiumSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, StadiumBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = selectedDay.eventType.icon,
                                        contentDescription = null,
                                        tint = selectedDay.eventType.color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = selectedDay.eventType.label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = selectedDay.eventType.color,
                                            fontSize = 9.5.sp
                                        )
                                    )
                                }
                                Text(
                                    text = selectedDay.eventDescription,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 8.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Bottom Simulation Buttons
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            onAdvanceDay()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .testTag("btn_advance_calendar_fc26"),
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalForest),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Filled.FastForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = LocalizationManager.getString("advance_day", currentLanguage),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            HapticController.performTactileClick(haptic, context)
                            onAdvanceDay()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = LocalizationManager.getString("sim_to_date", currentLanguage),
                            fontSize = 8.5.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarGridCell(
    day: CalendarDayModel,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .testTag("calendar_day_${day.dayNumber}"),
        color = when {
            isSelected -> NaturalForest.copy(alpha = 0.25f)
            isToday -> NaturalForest.copy(alpha = 0.15f)
            day.fixture != null -> StadiumSurfaceVariant
            else -> StadiumSurfaceVariant.copy(alpha = 0.6f)
        },
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected || isToday) 1.5.dp else 0.5.dp,
            if (isSelected) NaturalForest else if (isToday) NaturalForestLight else StadiumBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Day Number & Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${day.dayNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isToday || isSelected) FontWeight.Black else FontWeight.Bold,
                        color = if (isToday) NaturalForest else TextPrimary,
                        fontSize = 8.5.sp
                    )
                )

                if (day.fixture != null) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NaturalForest)
                    )
                }
            }

            // Event icon / Opponent Preview
            if (day.fixture != null) {
                val fix = day.fixture
                Surface(
                    color = NaturalForest.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "VS ${fix.awayTeam.take(4).uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NaturalForest,
                            fontWeight = FontWeight.Black,
                            fontSize = 6.5.sp,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        modifier = Modifier.padding(1.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = day.eventType.icon,
                        contentDescription = null,
                        tint = day.eventType.color.copy(alpha = 0.8f),
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendBadge(icon: ImageVector, color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(10.dp))
        Text(label, fontSize = 7.5.sp, color = TextSecondary)
    }
}
