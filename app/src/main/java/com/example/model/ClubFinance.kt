package com.example.model

enum class FacilityTier(val title: String, val level: Int, val upgradeCostMillions: Double, val perkDesc: String) {
    BASIC("Standard Local Facilities", 1, 5.0, "Baseline recovery & development"),
    IMPROVED("Advanced Training Ground", 2, 12.0, "+10% attribute training growth"),
    PROFESSIONAL("Elite High-Performance Center", 3, 25.0, "+20% training speed, -25% injury risk"),
    STATE_OF_THE_ART("World-Class Complex", 4, 45.0, "+35% training boost, generates top tier wonderkids")
}

data class BoardObjective(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // League, Cup, Financial, Youth
    val targetDesc: String,
    val progressPct: Int, // 0..100
    val isCompleted: Boolean = false,
    val priority: String = "Crucial" // Crucial, High, Medium
)

data class ClubFinancialStatement(
    val weeklyTicketRevenue: Double = 1.25, // in Millions
    val weeklySponsorshipIncome: Double = 2.40, // in Millions
    val weeklyMerchandiseRevenue: Double = 0.85, // in Millions
    val weeklyWageExpenditure: Double = 1.10, // in Millions
    val weeklyFacilityMaintenance: Double = 0.30, // in Millions
    val stadiumCapacity: Int = 54000,
    val averageTicketPriceDollars: Int = 45,
    val fanSatisfactionPct: Int = 88,
    val boardConfidencePct: Int = 92,
    val trainingFacilityTier: FacilityTier = FacilityTier.IMPROVED,
    val youthAcademyTier: FacilityTier = FacilityTier.IMPROVED,
    val scoutingFacilityTier: FacilityTier = FacilityTier.IMPROVED
)
