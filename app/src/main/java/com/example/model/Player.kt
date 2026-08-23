package com.example.model

enum class PositionCategory {
    GK, DEF, MID, ATT
}

enum class PlayerRole(val category: PositionCategory, val abbreviation: String, val fullTitle: String) {
    GK(PositionCategory.GK, "GK", "Goalkeeper"),
    CB(PositionCategory.DEF, "CB", "Center Back"),
    LB(PositionCategory.DEF, "LB", "Left Back"),
    RB(PositionCategory.DEF, "RB", "Right Back"),
    LWB(PositionCategory.DEF, "LWB", "Left Wing Back"),
    RWB(PositionCategory.DEF, "RWB", "Right Wing Back"),
    CDM(PositionCategory.MID, "CDM", "Defensive Midfield"),
    CM(PositionCategory.MID, "CM", "Central Midfield"),
    CAM(PositionCategory.MID, "CAM", "Attacking Midfield"),
    LM(PositionCategory.MID, "LM", "Left Midfield"),
    RM(PositionCategory.MID, "RM", "Right Midfield"),
    LW(PositionCategory.ATT, "LW", "Left Winger"),
    RW(PositionCategory.ATT, "RW", "Right Winger"),
    ST(PositionCategory.ATT, "ST", "Striker"),
    CF(PositionCategory.ATT, "CF", "Center Forward")
}

enum class TrainingFocus(val title: String, val boostedAttr: String) {
    BALANCED("Balanced Overall", "All Attributes"),
    FINISHING_ATTACK("Finishing & Composure", "Shooting & Dribbling"),
    PLAYMAKING_VISION("Playmaking & Vision", "Passing & Tactical IQ"),
    DEFENSIVE_SOLIDITY("Tackling & Positioning", "Defending & Physical"),
    PACE_ACCELERATION("Speed & Burst", "Pace & Stamina"),
    GOALKEEPING_REFLEXES("Reflexes & Handling", "Goalkeeping")
}

data class PlayerAttributes(
    val pace: Int,
    val shooting: Int,
    val passing: Int,
    val dribbling: Int,
    val defending: Int,
    val physicality: Int,
    val tacticalIq: Int
)

data class PlayerSeasonStats(
    val appearances: Int = 0,
    val goals: Int = 0,
    val assists: Int = 0,
    val expectedGoals: Float = 0.0f,
    val passCompletionPct: Int = 82,
    val tacklesWon: Int = 0,
    val cleanSheets: Int = 0,
    val avgMatchRating: Float = 7.1f,
    val minutesPlayed: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0
)

data class Player(
    val id: String,
    val name: String,
    val number: Int,
    val primaryRole: PlayerRole,
    val secondaryRoles: List<PlayerRole> = emptyList(),
    val overallRating: Int,
    val potentialRating: Int,
    val age: Int,
    val nationality: String,
    val flagEmoji: String,
    val attributes: PlayerAttributes,
    val stamina: Int = 100, // 0 - 100
    val morale: Int = 95, // 0 - 100
    val condition: String = "Match Fit", // Match Fit, Fatigued, Injured, Suspended
    val seasonStats: PlayerSeasonStats = PlayerSeasonStats(),
    val marketValueMillions: Double,
    val weeklyWageThousands: Int,
    val isStarter: Boolean = false,
    val starterSlotIndex: Int = -1, // 0..10 for starting XI
    val isCaptain: Boolean = false,
    val isPenaltyTaker: Boolean = false,
    val isFreeKickTaker: Boolean = false,
    val isCornerTaker: Boolean = false,
    val contractExpiryYear: Int = 2029,
    val releaseClauseMillions: Double = 0.0,
    val squadStatus: String = "Important First Team",
    val trainingFocus: TrainingFocus = TrainingFocus.BALANCED,
    val trainingProgressPct: Int = 35,
    val isYouthProspect: Boolean = false,
    val formHistory: List<Float> = listOf(7.2f, 6.8f, 7.5f, 8.0f, 7.4f)
)

