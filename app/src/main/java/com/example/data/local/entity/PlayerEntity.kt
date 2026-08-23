package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Player
import com.example.model.PlayerAttributes
import com.example.model.PlayerRole
import com.example.model.PlayerSeasonStats
import com.example.model.TrainingFocus

/**
 * Room Entity representing a football player with simulation statistics, attributes,
 * and contract information.
 *
 * Core simulation attributes:
 * - [name]: Player's display name
 * - [speed]: Sprint speed and pace rating (0-99)
 * - [passing]: Vision, short/long passing accuracy (0-99)
 * - [shooting]: Finishing, shot power, and composure (0-99)
 * - [stamina]: Current physical match energy and stamina level (0-100)
 * - [overallRating]: Composite player ability rating (0-99)
 */
@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey 
    val id: String,
    val name: String,
    val speed: Int,
    val passing: Int,
    val shooting: Int,
    val stamina: Int = 100,
    val overallRating: Int,

    // Additional tactical, profile, and simulation attributes
    val number: Int = 10,
    val primaryRole: PlayerRole = PlayerRole.CM,
    val secondaryRoles: List<PlayerRole> = emptyList(),
    val potentialRating: Int = 82,
    val age: Int = 24,
    val nationality: String = "Global",
    val flagEmoji: String = "⚽",
    val pace: Int = speed,
    val dribbling: Int = 72,
    val defending: Int = 65,
    val physicality: Int = 70,
    val tacticalIq: Int = 72,

    // Dynamic Match State
    val morale: Int = 95,
    val condition: String = "Match Fit",

    // Season Statistics
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
    val redCards: Int = 0,

    // Contract & Financials
    val marketValueMillions: Double = 15.0,
    val weeklyWageThousands: Int = 45,
    val isStarter: Boolean = false,
    val starterSlotIndex: Int = -1,
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
    val imageUrl: String = "",
    val formHistory: List<Float> = listOf(7.2f, 6.8f, 7.5f, 8.0f, 7.4f)
) {
    fun toDomain(): Player = Player(
        id = id,
        name = name,
        number = number,
        primaryRole = primaryRole,
        secondaryRoles = secondaryRoles,
        overallRating = overallRating,
        potentialRating = potentialRating,
        age = age,
        nationality = nationality,
        flagEmoji = flagEmoji,
        attributes = PlayerAttributes(
            pace = speed.takeIf { it > 0 } ?: pace,
            shooting = shooting,
            passing = passing,
            dribbling = dribbling,
            defending = defending,
            physicality = physicality,
            tacticalIq = tacticalIq
        ),
        stamina = stamina,
        morale = morale,
        condition = condition,
        seasonStats = PlayerSeasonStats(
            appearances = appearances,
            goals = goals,
            assists = assists,
            expectedGoals = expectedGoals,
            passCompletionPct = passCompletionPct,
            tacklesWon = tacklesWon,
            cleanSheets = cleanSheets,
            avgMatchRating = avgMatchRating,
            minutesPlayed = minutesPlayed,
            yellowCards = yellowCards,
            redCards = redCards
        ),
        marketValueMillions = marketValueMillions,
        weeklyWageThousands = weeklyWageThousands,
        isStarter = isStarter,
        starterSlotIndex = starterSlotIndex,
        isCaptain = isCaptain,
        isPenaltyTaker = isPenaltyTaker,
        isFreeKickTaker = isFreeKickTaker,
        isCornerTaker = isCornerTaker,
        contractExpiryYear = contractExpiryYear,
        releaseClauseMillions = releaseClauseMillions,
        squadStatus = squadStatus,
        trainingFocus = trainingFocus,
        trainingProgressPct = trainingProgressPct,
        isYouthProspect = isYouthProspect,
        imageUrl = imageUrl,
        formHistory = formHistory
    )

    companion object {
        fun fromDomain(player: Player): PlayerEntity = PlayerEntity(
            id = player.id,
            name = player.name,
            speed = player.attributes.speed,
            passing = player.attributes.passing,
            shooting = player.attributes.shooting,
            stamina = player.stamina,
            overallRating = player.overallRating,
            number = player.number,
            primaryRole = player.primaryRole,
            secondaryRoles = player.secondaryRoles,
            potentialRating = player.potentialRating,
            age = player.age,
            nationality = player.nationality,
            flagEmoji = player.flagEmoji,
            pace = player.attributes.pace,
            dribbling = player.attributes.dribbling,
            defending = player.attributes.defending,
            physicality = player.attributes.physicality,
            tacticalIq = player.attributes.tacticalIq,
            morale = player.morale,
            condition = player.condition,
            appearances = player.seasonStats.appearances,
            goals = player.seasonStats.goals,
            assists = player.seasonStats.assists,
            expectedGoals = player.seasonStats.expectedGoals,
            passCompletionPct = player.seasonStats.passCompletionPct,
            tacklesWon = player.seasonStats.tacklesWon,
            cleanSheets = player.seasonStats.cleanSheets,
            avgMatchRating = player.seasonStats.avgMatchRating,
            minutesPlayed = player.seasonStats.minutesPlayed,
            yellowCards = player.seasonStats.yellowCards,
            redCards = player.seasonStats.redCards,
            marketValueMillions = player.marketValueMillions,
            weeklyWageThousands = player.weeklyWageThousands,
            isStarter = player.isStarter,
            starterSlotIndex = player.starterSlotIndex,
            isCaptain = player.isCaptain,
            isPenaltyTaker = player.isPenaltyTaker,
            isFreeKickTaker = player.isFreeKickTaker,
            isCornerTaker = player.isCornerTaker,
            contractExpiryYear = player.contractExpiryYear,
            releaseClauseMillions = player.releaseClauseMillions,
            squadStatus = player.squadStatus,
            trainingFocus = player.trainingFocus,
            trainingProgressPct = player.trainingProgressPct,
            isYouthProspect = player.isYouthProspect,
            imageUrl = player.imageUrl,
            formHistory = player.formHistory
        )
    }
}
