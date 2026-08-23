package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.HistoricalMatchRecord
import com.example.model.MatchResultType

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey val matchId: String,
    val competition: String,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis(),
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val userTeamIsHome: Boolean,
    val matchRating: Float,
    val topPerformer: String,
    val userGoalScorers: List<String> = emptyList(),
    val opponentGoalScorers: List<String> = emptyList(),
    val xGHome: Float,
    val xGAway: Float,
    val possessionUser: Int,
    val totalShotsUser: Int,
    val managerXpGained: Int,
    val resultType: MatchResultType
) {
    fun toDomain(): HistoricalMatchRecord = HistoricalMatchRecord(
        matchId = matchId,
        competition = competition,
        dateString = dateString,
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        homeScore = homeScore,
        awayScore = awayScore,
        userTeamIsHome = userTeamIsHome,
        matchRating = matchRating,
        topPerformer = topPerformer,
        userGoalScorers = userGoalScorers,
        opponentGoalScorers = opponentGoalScorers,
        xGHome = xGHome,
        xGAway = xGAway,
        possessionUser = possessionUser,
        totalShotsUser = totalShotsUser,
        managerXpGained = managerXpGained,
        resultType = resultType
    )

    companion object {
        fun fromDomain(record: HistoricalMatchRecord, timestamp: Long = System.currentTimeMillis()): MatchHistoryEntity = MatchHistoryEntity(
            matchId = record.matchId,
            competition = record.competition,
            dateString = record.dateString,
            timestamp = timestamp,
            homeTeam = record.homeTeam,
            awayTeam = record.awayTeam,
            homeScore = record.homeScore,
            awayScore = record.awayScore,
            userTeamIsHome = record.userTeamIsHome,
            matchRating = record.matchRating,
            topPerformer = record.topPerformer,
            userGoalScorers = record.userGoalScorers,
            opponentGoalScorers = record.opponentGoalScorers,
            xGHome = record.xGHome,
            xGAway = record.xGAway,
            possessionUser = record.possessionUser,
            totalShotsUser = record.totalShotsUser,
            managerXpGained = record.managerXpGained,
            resultType = record.resultType
        )
    }
}
