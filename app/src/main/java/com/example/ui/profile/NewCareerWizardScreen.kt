package com.example.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.utils.AudioEffectManager

enum class WizardStep(val title: String, val subtitle: String, val stepNum: Int) {
    MANAGER_INFO("Profil Manajer", "Nama, Kebangsaan & Filosofi", 1),
    REGION_SELECT("Pilih Region", "Pusat Sepak Bola Dunia", 2),
    LEAGUE_SELECT("Pilih Kompetisi Liga", "Divisi Domestik Klub", 3),
    CLUB_SELECT("Pilih Klub Sepak Bola", "Klub yang Anda Kelola", 4),
    NATIONAL_TEAM("Tim Nasional & Piala Dunia", "Pimpin Negara Menuju World Cup", 5),
    SUMMARY("Konfirmasi Karir", "Siap Memulai Perjalanan Manajer", 6)
}

data class AvailableClubOption(
    val name: String,
    val league: String,
    val region: String,
    val rating: Int,
    val transferBudgetMillions: Double,
    val wageBudgetThousands: Int,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val stadium: String
)

data class AvailableNationalTeam(
    val name: String,
    val flag: String,
    val rating: Int,
    val confederation: String,
    val target: String
)

@Composable
fun NewCareerWizardScreen(
    onCancel: () -> Unit,
    onFinishWizard: (
        managerName: String,
        managerNationality: String,
        managerFlag: String,
        managerArchetype: String,
        region: String,
        league: String,
        clubName: String,
        clubBadgeColor: Long,
        transferBudget: Double,
        stadium: String,
        nationalTeam: String,
        nationalFlag: String,
        nationalRating: Int
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var currentStep by remember { mutableStateOf(WizardStep.MANAGER_INFO) }

    // Manager state
    var managerName by remember { mutableStateOf("Coach Alex Sterling") }
    var selectedNationality by remember { mutableStateOf("Indonesia") }
    var selectedFlag by remember { mutableStateOf("🇮🇩") }
    var selectedArchetype by remember { mutableStateOf("Tiki-Taka Mastermind") }

    // Region & League
    var selectedRegion by remember { mutableStateOf("Europe Top 5") }
    var selectedLeague by remember { mutableStateOf("Premier League") }

    // Club state
    var selectedClubName by remember { mutableStateOf("Real Madrid") }
    var selectedClubBadgeColor by remember { mutableStateOf(0xFF00E5FF) }
    var selectedTransferBudget by remember { mutableStateOf(140.0) }
    var selectedStadium by remember { mutableStateOf("Santiago Bernabéu") }

    // National Team state
    var selectedNationalTeam by remember { mutableStateOf("Indonesia") }
    var selectedNationalFlag by remember { mutableStateOf("🇮🇩") }
    var selectedNationalRating by remember { mutableStateOf(78) }

    val nationalities = listOf(
        Pair("Indonesia", "🇮🇩"),
        Pair("Inggris", "🏴󠁧󠁢󠁥󠁮󠁧󠁿"),
        Pair("Spanyol", "🇪🇸"),
        Pair("Brasil", "🇧🇷"),
        Pair("Argentina", "🇦🇷"),
        Pair("Prancis", "🇫🇷"),
        Pair("Jerman", "🇩🇪"),
        Pair("Jepang", "🇯🇵"),
        Pair("Italia", "🇮🇹"),
        Pair("Portugal", "🇵🇹"),
        Pair("Belanda", "🇳🇱")
    )

    val archetypes = listOf(
        Pair("Tiki-Taka Mastermind", "Dominasi penguasaan bola, umpan pendek akurat, dan kontrol tempo."),
        Pair("Gegenpress Tactician", "Tekanan tinggi tanpa henti di daerah lawan untuk merebut bola cepat."),
        Pair("Counter-Attack Specialist", "Pertahanan rapat dan transisi kilat mematikan ke lini depan."),
        Pair("Talent Developer & Mentor", "Fokus akademi muda, pengembangan potensi pemain, dan efisiensi finansial.")
    )

    val regions = listOf(
        Pair("Europe Top 5", "🇪🇺 Liga-Liga Elit Eropa"),
        Pair("South America", "🌎 Zona CONMEBOL & Copa"),
        Pair("Asia-Pacific & ASEAN", "🌏 Zona AFC & Asia Tenggara"),
        Pair("North America", "🇺🇸 Major League & CONCACAF"),
        Pair("Global Elite", "🌍 Super League Internasional")
    )

    val leagues = when (selectedRegion) {
        "Europe Top 5" -> listOf("Premier League", "La Liga EA", "Serie A TIM", "Bundesliga", "Ligue 1")
        "South America" -> listOf("Brasileirão Serie A", "Liga Profesional Argentina", "Copa Libertadores")
        "Asia-Pacific & ASEAN" -> listOf("BRI Liga 1 Indonesia", "J1 League Jepang", "Saudi Pro League", "A-League")
        "North America" -> listOf("MLS (Major League Soccer)", "Liga MX")
        else -> listOf("Apex Champions Division", "World Super League")
    }

    val clubs = listOf(
        AvailableClubOption("Real Madrid", "La Liga EA", "Europe Top 5", 89, 150.0, 1800, 0xFF00E5FF, 0xFF14241B, "Santiago Bernabéu"),
        AvailableClubOption("Manchester City", "Premier League", "Europe Top 5", 90, 180.0, 2100, 0xFF6CABDD, 0xFF1C2C5B, "Etihad Stadium"),
        AvailableClubOption("Arsenal FC", "Premier League", "Europe Top 5", 87, 120.0, 1400, 0xFFEF0107, 0xFF063672, "Emirates Stadium"),
        AvailableClubOption("FC Barcelona", "La Liga EA", "Europe Top 5", 87, 85.0, 1350, 0xFF004D98, 0xFFA50044, "Spotify Camp Nou"),
        AvailableClubOption("Bayern Munich", "Bundesliga", "Europe Top 5", 88, 140.0, 1600, 0xFFDC052D, 0xFF0066B2, "Allianz Arena"),
        AvailableClubOption("Paris SG", "Ligue 1", "Europe Top 5", 88, 160.0, 1900, 0xFF004170, 0xFFDA291C, "Parc des Princes"),
        AvailableClubOption("Juventus", "Serie A TIM", "Europe Top 5", 85, 75.0, 1100, 0xFFFFFFFF, 0xFF000000, "Allianz Stadium Turin"),
        AvailableClubOption("Persija Jakarta", "BRI Liga 1 Indonesia", "Asia-Pacific & ASEAN", 76, 25.0, 350, 0xFFFF3B30, 0xFFFFCC00, "Jakarta International Stadium"),
        AvailableClubOption("Persib Bandung", "BRI Liga 1 Indonesia", "Asia-Pacific & ASEAN", 76, 25.0, 350, 0xFF0055A5, 0xFFFFFFFF, "Gelora Bandung Lautan Api"),
        AvailableClubOption("Al-Hilal", "Saudi Pro League", "Asia-Pacific & ASEAN", 83, 130.0, 1500, 0xFF005BAC, 0xFFFFFFFF, "Kingdom Arena"),
        AvailableClubOption("Inter Miami", "MLS (Major League Soccer)", "North America", 82, 70.0, 950, 0xFFF7B5CD, 0xFF231F20, "Chase Stadium")
    ).filter { it.region == selectedRegion || selectedRegion == "Global Elite" }

    val nationalTeams = listOf(
        AvailableNationalTeam("Indonesia", "🇮🇩", 78, "AFC", "Target: Kualifikasi Putaran 4 Piala Dunia & Juara ASEAN"),
        AvailableNationalTeam("Brasil", "🇧🇷", 89, "CONMEBOL", "Target: Juara Dunia FIFA World Cup & Copa America"),
        AvailableNationalTeam("Argentina", "🇦🇷", 90, "CONMEBOL", "Target: Pertahankan Gelar Juara Dunia"),
        AvailableNationalTeam("Inggris", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", 88, "UEFA", "Target: Angkat Trofi Juara Dunia / Euro"),
        AvailableNationalTeam("Prancis", "🇫🇷", 89, "UEFA", "Target: Final Piala Dunia"),
        AvailableNationalTeam("Jepang", "🇯🇵", 84, "AFC", "Target: 8 Besar Piala Dunia & Dominasi Asia"),
        AvailableNationalTeam("Jerman", "🇩🇪", 86, "UEFA", "Target: Semifinal Piala Dunia"),
        AvailableNationalTeam("Spanyol", "🇪🇸", 88, "UEFA", "Target: Juara Dunia")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0C1710), Color(0xFF060B08))
                )
            )
            .padding(16.dp)
            .testTag("new_career_wizard")
    ) {
        // Top Wizard Stepper Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    HapticController.triggerClick(haptic)
                    if (currentStep.stepNum > 1) {
                        currentStep = WizardStep.entries[currentStep.stepNum - 2]
                    } else {
                        onCancel()
                    }
                }
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LANGKAH ${currentStep.stepNum} / 6",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF00FF87),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = currentStep.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Step Indicator Dots
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                WizardStep.entries.forEach { step ->
                    Box(
                        modifier = Modifier
                            .size(if (step == currentStep) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (step.stepNum <= currentStep.stepNum) Color(0xFF00FF87) else Color(0xFF263D2E)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Step Content Body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentStep) {
                WizardStep.MANAGER_INFO -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "NAMA MANAJER",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00FF87))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = managerName,
                                        onValueChange = { managerName = it },
                                        placeholder = { Text("Masukkan Nama Manajer") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF00FF87),
                                            unfocusedBorderColor = Color(0xFF2B4734),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "KEBANGSAAN MANAJER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF88A090),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(nationalities) { (nation, flag) ->
                                    val isSelected = selectedNationality == nation
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) Color(0xFF00FF87) else Color(0xFF14241B)
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .clickable {
                                                HapticController.triggerClick(haptic)
                                                selectedNationality = nation
                                                selectedFlag = flag
                                            }
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Color(0xFF00FF87) else Color(0xFF2B4734),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(text = flag, fontSize = 18.sp)
                                            Text(
                                                text = nation,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    color = if (isSelected) Color(0xFF07140B) else Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "FILOSOFI & GAYA MANAJER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF88A090),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        items(archetypes) { (archetype, desc) ->
                            val isSelected = selectedArchetype == archetype
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF1B3825) else Color(0xFF111E16)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        HapticController.triggerClick(haptic)
                                        selectedArchetype = archetype
                                    }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF00FF87) else Color(0xFF263D2E),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedArchetype = archetype },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFF00FF87),
                                            unselectedColor = Color(0xFF88A090)
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = archetype,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = if (isSelected) Color(0xFF00FF87) else Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFB0C4B8),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                WizardStep.REGION_SELECT -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = "PILIH BENUA / WILAYAH KOMPETISI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF88A090),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        items(regions) { (regionCode, regionTitle) ->
                            val isSelected = selectedRegion == regionCode
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF1B3825) else Color(0xFF111E16)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        HapticController.triggerClick(haptic)
                                        selectedRegion = regionCode
                                    }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF00FF87) else Color(0xFF263D2E),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = regionCode,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = if (isSelected) Color(0xFF00FF87) else Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = regionTitle,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFB0C4B8),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF00FF87)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                WizardStep.LEAGUE_SELECT -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = "LIGA DOMESTIK DI $selectedRegion",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF88A090),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        items(leagues) { league ->
                            val isSelected = selectedLeague == league
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF1B3825) else Color(0xFF111E16)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        HapticController.triggerClick(haptic)
                                        selectedLeague = league
                                    }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF00FF87) else Color(0xFF263D2E),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    LeagueBadge(leagueName = league, size = 38.dp, showTitle = true)
                                    if (isSelected) {
                                        Icon(
                                            Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF00FF87)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                WizardStep.CLUB_SELECT -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = "PILIH KLUB UNTUK DIKELOLA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF88A090),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        items(if (clubs.isNotEmpty()) clubs else listOf(
                            AvailableClubOption("Apex Tacticians FC", selectedLeague, selectedRegion, 84, 85.0, 1200, 0xFF00E5FF, 0xFF14241B, "Apex Park")
                        )) { club ->
                            val isSelected = selectedClubName == club.name
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF1B3825) else Color(0xFF111E16)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        HapticController.triggerClick(haptic)
                                        selectedClubName = club.name
                                        selectedClubBadgeColor = club.primaryColorHex
                                        selectedTransferBudget = club.transferBudgetMillions
                                        selectedStadium = club.stadium
                                    }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF00FF87) else Color(0xFF263D2E),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    ClubCrest(
                                        clubName = club.name,
                                        primaryColor = Color(club.primaryColorHex),
                                        secondaryColor = Color(club.secondaryColorHex),
                                        size = CrestSize.MEDIUM
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = club.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = if (isSelected) Color(0xFF00FF87) else Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "🏟️ ${club.stadium} • ${club.league}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFB0C4B8),
                                                fontSize = 10.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                text = "OVR: ${club.rating}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFFFFD700),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = "Budget: $${club.transferBudgetMillions.toInt()}M",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF00E5FF),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF00FF87)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                WizardStep.NATIONAL_TEAM -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF14291D)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("🏆", fontSize = 28.sp)
                                    Column {
                                        Text(
                                            text = "DUAL ROLE: MANAJER KLUB & TIMNAS",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = Color(0xFFFFD700),
                                                fontWeight = FontWeight.Black
                                            )
                                        )
                                        Text(
                                            text = "Pimpin negara Anda menembus Kualifikasi & Final Piala Dunia!",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFD0DDD4),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        items(nationalTeams) { nat ->
                            val isSelected = selectedNationalTeam == nat.name
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF1B3825) else Color(0xFF111E16)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        HapticController.triggerClick(haptic)
                                        selectedNationalTeam = nat.name
                                        selectedNationalFlag = nat.flag
                                        selectedNationalRating = nat.rating
                                    }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF00FF87) else Color(0xFF263D2E),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    NationalFlagBadge(
                                        countryName = nat.name,
                                        flagEmoji = nat.flag,
                                        rating = nat.rating,
                                        size = 40.dp
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = nat.name,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    color = if (isSelected) Color(0xFF00FF87) else Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = "(${nat.confederation})",
                                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF88A090))
                                            )
                                        }
                                        Text(
                                            text = nat.target,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFB0C4B8),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF00FF87)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                WizardStep.SUMMARY -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "KONTRAK MANAJER RESMI",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF00FF87),
                                            fontWeight = FontWeight.Black
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        PlayerAvatarVisual(
                                            name = managerName,
                                            overallRating = 88,
                                            flagEmoji = selectedFlag,
                                            size = AvatarSize.LARGE
                                        )

                                        Column {
                                            Text(
                                                text = managerName,
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = "$selectedFlag $selectedNationality • $selectedArchetype",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF88A090))
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = Color(0xFF263D2E))
                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Club & National Overview
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("KLUB UTAMA", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF88A090)))
                                            Text(selectedClubName, style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                            Text("🏟️ $selectedStadium", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB0C4B8), fontSize = 10.sp))
                                            Text("Budget: $$selectedTransferBudget M", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00E5FF)))
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("TIM NASIONAL", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF88A090)))
                                            Text("$selectedNationalFlag $selectedNationalTeam", style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                            Text("Rating OVR: $selectedNationalRating", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFFD700), fontSize = 10.sp))
                                            Text("Target: Piala Dunia 2026", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00FF87)))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Navigation Next / Confirm Button
        Button(
            onClick = {
                HapticController.triggerImpact(haptic)
                AudioEffectManager.playConfirm()
                if (currentStep.stepNum < 6) {
                    currentStep = WizardStep.entries[currentStep.stepNum]
                } else {
                    onFinishWizard(
                        managerName,
                        selectedNationality,
                        selectedFlag,
                        selectedArchetype,
                        selectedRegion,
                        selectedLeague,
                        selectedClubName,
                        selectedClubBadgeColor,
                        selectedTransferBudget,
                        selectedStadium,
                        selectedNationalTeam,
                        selectedNationalFlag,
                        selectedNationalRating
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00FF87),
                contentColor = Color(0xFF07140B)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("wizard_next_button")
        ) {
            Text(
                text = if (currentStep == WizardStep.SUMMARY) "MULAI KARIR DI MAIN HUB ⚽" else "LANJUTKAN (${currentStep.stepNum}/6) →",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            )
        }
    }
}
