package com.example.model

enum class MatchEventType {
    KICKOFF,
    GOAL,
    ASSIST,
    SAVED_SHOT,
    MISSED_SHOT,
    WOODWORK,
    YELLOW_CARD,
    RED_CARD,
    SUBSTITUTION,
    TACTICAL_CHANGE,
    HALF_TIME,
    FULL_TIME,
    DANGEROUS_ATTACK,
    INTERCEPTION,
    VAR_DECISION,
    PENALTY_AWARDED,
    TEAM_TALK
}

data class MatchEvent(
    val minute: Int,
    val isHomeTeam: Boolean,
    val eventType: MatchEventType,
    val title: String,
    val description: String,
    val playerName: String? = null,
    val secondaryPlayerName: String? = null
)

data class LivePitchEntity(
    val id: String,
    val name: String,
    val isHome: Boolean,
    val role: PlayerRole,
    val x: Float, // 0.0 to 1.0
    val y: Float, // 0.0 to 1.0
    val hasBall: Boolean = false,
    val matchRating: Float = 7.0f,
    val goalsScored: Int = 0,
    val assistsMade: Int = 0,
    val yellowCard: Boolean = false,
    val redCard: Boolean = false
)

data class MatchStats(
    val possessionHome: Int = 50,
    val possessionAway: Int = 50,
    val shotsHome: Int = 0,
    val shotsAway: Int = 0,
    val shotsOnTargetHome: Int = 0,
    val shotsOnTargetAway: Int = 0,
    val xGHome: Float = 0.0f,
    val xGAway: Float = 0.0f,
    val cornersHome: Int = 0,
    val cornersAway: Int = 0,
    val foulsHome: Int = 0,
    val foulsAway: Int = 0,
    val yellowCardsHome: Int = 0,
    val yellowCardsAway: Int = 0,
    val redCardsHome: Int = 0,
    val redCardsAway: Int = 0,
    val passAccuracyHome: Int = 84,
    val passAccuracyAway: Int = 81
)

enum class MatchStatus {
    PRE_MATCH,
    FIRST_HALF,
    HALF_TIME,
    SECOND_HALF,
    FULL_TIME
}

data class OpponentClub(
    val id: String,
    val name: String,
    val shortName: String,
    val badgeColorHex: Long,
    val secondaryBadgeColorHex: Long,
    val league: String,
    val overallRating: Int,
    val attackRating: Int,
    val midfieldRating: Int,
    val defenseRating: Int,
    val managerName: String,
    val formation: FormationType
)

data class LiveMatchState(
    val matchId: String,
    val homeTeamName: String,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val opponent: OpponentClub,
    val currentMinute: Int = 0,
    val matchStatus: MatchStatus = MatchStatus.PRE_MATCH,
    val stats: MatchStats = MatchStats(),
    val events: List<MatchEvent> = emptyList(),
    val ballPosition: Pair<Float, Float> = Pair(0.5f, 0.5f),
    val entities: List<LivePitchEntity> = emptyList(),
    val simSpeed: Int = 1, // 1x, 2x, 4x
    val isPaused: Boolean = false,
    val substitutionsRemaining: Int = 5,
    val teamTalkGiven: Boolean = false,
    val lastGoalCelebrationText: String? = null
)

data class HistoricalMatchRecord(
    val matchId: String,
    val competition: String,
    val dateString: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val userTeamIsHome: Boolean,
    val matchRating: Float,
    val topPerformer: String,
    val userGoalScorers: List<String>,
    val opponentGoalScorers: List<String>,
    val xGHome: Float,
    val xGAway: Float,
    val possessionUser: Int,
    val totalShotsUser: Int,
    val managerXpGained: Int,
    val resultType: MatchResultType // WIN, DRAW, LOSS
)

enum class MatchResultType {
    WIN, DRAW, LOSS
}
