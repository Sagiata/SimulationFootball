package com.example.model

enum class ScoutRegion(val regionName: String, val flag: String, val description: String) {
    SOUTH_AMERICA("South America", "🇧🇷", "Elite flair, dribbling & raw attacking prodigies"),
    WESTERN_EUROPE("Western Europe", "🇪🇺", "High tactical IQ, passing masters & academy stars"),
    EASTERN_EUROPE("Eastern Europe", "🇷🇴", "Resilient defenders, box-to-box dynamos"),
    AFRICA("Africa", "🇳🇬", "Explosive pace, physical power & aerial dominance"),
    ASIA_PACIFIC("Asia-Pacific", "🇯🇵", "Agile playmakers, work rate & stamina beasts"),
    NORTH_AMERICA("North America", "🇺🇸", "Athletic wingbacks, modern tactical athletes")
}

enum class ScoutGrade(val tag: String, val colorHex: Long) {
    GRADE_S("S+ World Class", 0xFFFFD700),
    GRADE_A("A High Potential", 0xFF00E676),
    GRADE_B("B Solid Prospect", 0xFF00E5FF),
    GRADE_C("C Squad Depth", 0xFFFFB300)
}

data class ScoutReport(
    val scoutGrade: ScoutGrade,
    val scoutAccuracy: Int, // e.g. 94%
    val estimatedPotential: String, // e.g. "88 - 93"
    val scoutNotes: String,
    val recommendedRole: PlayerRole,
    val wageExpectationThousands: Int,
    val transferFeeEstimateMillions: Double
)

data class ScoutProspect(
    val id: String,
    val name: String,
    val age: Int,
    val nationality: String,
    val flag: String,
    val currentClub: String,
    val region: ScoutRegion,
    val primaryRole: PlayerRole,
    val currentOvr: Int,
    val minPotential: Int,
    val maxPotential: Int,
    val attributes: PlayerAttributes,
    val marketValueMillions: Double,
    val wageWeeklyThousands: Int,
    val report: ScoutReport,
    val isScouted: Boolean = false,
    val scoutingProgress: Float = 0.0f, // 0.0 to 1.0
    val isNegotiating: Boolean = false
)
