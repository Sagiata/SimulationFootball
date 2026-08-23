package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.MatchHistoryEntity
import com.example.model.MatchResultType
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchHistoryDao {

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC")
    fun getAllMatchHistory(): Flow<List<MatchHistoryEntity>>

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMatches(limit: Int): Flow<List<MatchHistoryEntity>>

    @Query("SELECT * FROM match_history WHERE resultType = :resultType ORDER BY timestamp DESC")
    fun getMatchesByResult(resultType: MatchResultType): Flow<List<MatchHistoryEntity>>

    @Query("SELECT * FROM match_history WHERE matchId = :id")
    fun getMatchById(id: String): Flow<MatchHistoryEntity?>

    @Query("SELECT COUNT(*) FROM match_history WHERE resultType = 'WIN'")
    fun getWinsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM match_history WHERE resultType = 'DRAW'")
    fun getDrawsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM match_history WHERE resultType = 'LOSS'")
    fun getLossesCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchHistoryEntity>)

    @Query("DELETE FROM match_history WHERE matchId = :id")
    suspend fun deleteMatchById(id: String)

    @Query("DELETE FROM match_history")
    suspend fun deleteAllMatchHistory()
}
