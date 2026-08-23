package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.PlayerEntity
import com.example.model.TrainingFocus
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object (DAO) for Football Player entities.
 * Supports squad management, tactical queries, match stats, and transfer operations.
 */
@Dao
interface PlayerDao {

    @Query("SELECT * FROM players ORDER BY isStarter DESC, starterSlotIndex ASC, overallRating DESC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE isYouthProspect = 0 ORDER BY isStarter DESC, starterSlotIndex ASC, overallRating DESC")
    fun getFirstTeamSquad(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE isYouthProspect = 1 ORDER BY potentialRating DESC")
    fun getYouthAcademyPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE isStarter = 1 ORDER BY starterSlotIndex ASC")
    fun getStartingXI(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE isYouthProspect = 0 AND isStarter = 0 ORDER BY overallRating DESC")
    fun getBenchPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE name LIKE '%' || :searchQuery || '%' ORDER BY overallRating DESC")
    fun searchPlayersByName(searchQuery: String): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players ORDER BY goals DESC, appearances ASC LIMIT :limit")
    fun getTopScorers(limit: Int = 5): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players ORDER BY assists DESC, appearances ASC LIMIT :limit")
    fun getTopAssisters(limit: Int = 5): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE overallRating >= :minRating ORDER BY overallRating DESC")
    fun getPlayersByMinRating(minRating: Int): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE isCaptain = 1 LIMIT 1")
    fun getTeamCaptain(): Flow<PlayerEntity?>

    @Query("SELECT * FROM players WHERE id = :id")
    fun getPlayerById(id: String): Flow<PlayerEntity?>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerByIdSync(id: String): PlayerEntity?

    @Query("SELECT COUNT(*) FROM players")
    suspend fun getPlayerCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Query("UPDATE players SET trainingFocus = :focus WHERE id = :id")
    suspend fun updateTrainingFocus(id: String, focus: TrainingFocus)

    @Query("UPDATE players SET stamina = :stamina, morale = :morale, condition = :condition WHERE id = :id")
    suspend fun updatePlayerMatchFitness(id: String, stamina: Int, morale: Int, condition: String)

    @Query("UPDATE players SET isStarter = :isStarter, starterSlotIndex = :slotIndex WHERE id = :id")
    suspend fun updateStarterStatus(id: String, isStarter: Boolean, slotIndex: Int)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("DELETE FROM players WHERE id = :id")
    suspend fun deletePlayerById(id: String)

    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()
}
