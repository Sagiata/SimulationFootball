package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.FormationType
import com.example.model.OpponentClub

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: String,
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
    val formation: FormationType,
    val isUserTeam: Boolean = false,
    val eloRating: Int = 1500,
    val transferBudgetMillions: Double = 50.0,
    val weeklyWageBudgetThousands: Int = 800,
    val stadiumName: String = "Grand Stadium",
    val stadiumCapacity: Int = 50000
) {
    fun toOpponentClub(): OpponentClub = OpponentClub(
        id = id,
        name = name,
        shortName = shortName,
        badgeColorHex = badgeColorHex,
        secondaryBadgeColorHex = secondaryBadgeColorHex,
        league = league,
        overallRating = overallRating,
        attackRating = attackRating,
        midfieldRating = midfieldRating,
        defenseRating = defenseRating,
        managerName = managerName,
        formation = formation
    )

    companion object {
        fun fromOpponentClub(opp: OpponentClub, isUserTeam: Boolean = false): TeamEntity = TeamEntity(
            id = opp.id,
            name = opp.name,
            shortName = opp.shortName,
            badgeColorHex = opp.badgeColorHex,
            secondaryBadgeColorHex = opp.secondaryBadgeColorHex,
            league = opp.league,
            overallRating = opp.overallRating,
            attackRating = opp.attackRating,
            midfieldRating = opp.midfieldRating,
            defenseRating = opp.defenseRating,
            managerName = opp.managerName,
            formation = opp.formation,
            isUserTeam = isUserTeam
        )
    }
}
