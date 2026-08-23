package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    @Query("SELECT * FROM teams ORDER BY overallRating DESC")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE isUserTeam = 0 ORDER BY overallRating DESC")
    fun getOpponentTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE isUserTeam = 1 LIMIT 1")
    fun getUserTeam(): Flow<TeamEntity?>

    @Query("SELECT * FROM teams WHERE id = :id")
    fun getTeamById(id: String): Flow<TeamEntity?>

    @Query("SELECT * FROM teams WHERE id = :id")
    suspend fun getTeamByIdSync(id: String): TeamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<TeamEntity>)

    @Update
    suspend fun updateTeam(team: TeamEntity)

    @Delete
    suspend fun deleteTeam(team: TeamEntity)

    @Query("DELETE FROM teams WHERE id = :id")
    suspend fun deleteTeamById(id: String)

    @Query("DELETE FROM teams")
    suspend fun deleteAllTeams()
}
