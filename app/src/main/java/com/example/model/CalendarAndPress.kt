package com.example.model

enum class CalendarEventType {
    LEAGUE_MATCH,
    CUP_MATCH,
    TRAINING_CAMP,
    TRANSFER_DEADLINE,
    PRESS_CONFERENCE,
    AWARDS_GALA
}

data class CalendarFixture(
    val id: String,
    val dateString: String,
    val homeTeam: String,
    val awayTeam: String,
    val competition: String,
    val eventType: CalendarEventType,
    val isUserMatch: Boolean = false,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val isPlayed: Boolean = false
)

data class PressConferenceQuestion(
    val id: String,
    val reporterName: String,
    val mediaOutlet: String,
    val questionText: String,
    val contextType: String, // "PRE_MATCH", "POST_MATCH_WIN", "POST_MATCH_DEFEAT", "TRANSFER_SPECULATION"
    val options: List<PressResponseOption>
)

data class PressResponseOption(
    val id: String,
    val answerText: String,
    val tone: String, // "Confident", "Passionate", "Pragmatic", "Critical"
    val moraleImpact: Int, // e.g. +5, -3
    val reputationImpact: Int // e.g. +2
)
