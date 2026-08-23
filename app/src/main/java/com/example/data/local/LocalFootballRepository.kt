package com.example.data.local

import com.example.data.local.dao.MatchHistoryDao
import com.example.data.local.dao.PlayerDao
import com.example.data.local.dao.TeamDao
import com.example.data.local.entity.MatchHistoryEntity
import com.example.data.local.entity.PlayerEntity
import com.example.data.local.entity.TeamEntity
import com.example.model.HistoricalMatchRecord
import com.example.model.MatchResultType
import com.example.model.OpponentClub
import com.example.model.Player
import com.example.model.TrainingFocus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalFootballRepository(
    private val playerDao: PlayerDao,
    private val teamDao: TeamDao,
    private val matchHistoryDao: MatchHistoryDao
) {

    // Player Streams & Operations
    val allPlayers: Flow<List<Player>> = playerDao.getAllPlayers().map { list ->
        list.map { it.toDomain() }
    }

    val firstTeamSquad: Flow<List<Player>> = playerDao.getFirstTeamSquad().map { list ->
        list.map { it.toDomain() }
    }

    val youthAcademy: Flow<List<Player>> = playerDao.getYouthAcademyPlayers().map { list ->
        list.map { it.toDomain() }
    }

    val startingXI: Flow<List<Player>> = playerDao.getStartingXI().map { list ->
        list.map { it.toDomain() }
    }

    fun getPlayerById(id: String): Flow<Player?> = playerDao.getPlayerById(id).map { it?.toDomain() }

    suspend fun savePlayer(player: Player) {
        playerDao.insertPlayer(PlayerEntity.fromDomain(player))
    }

    suspend fun savePlayers(players: List<Player>) {
        playerDao.insertPlayers(players.map { PlayerEntity.fromDomain(it) })
    }

    suspend fun updateTrainingFocus(playerId: String, focus: TrainingFocus) {
        playerDao.updateTrainingFocus(playerId, focus)
    }

    suspend fun updatePlayerMatchFitness(playerId: String, stamina: Int, morale: Int, condition: String) {
        playerDao.updatePlayerMatchFitness(playerId, stamina, morale, condition)
    }

    suspend fun updateStarterStatus(playerId: String, isStarter: Boolean, slotIndex: Int) {
        playerDao.updateStarterStatus(playerId, isStarter, slotIndex)
    }

    suspend fun deletePlayer(playerId: String) {
        playerDao.deletePlayerById(playerId)
    }

    suspend fun clearPlayers() {
        playerDao.deleteAllPlayers()
    }

    // Team Streams & Operations
    val opponentTeams: Flow<List<OpponentClub>> = teamDao.getOpponentTeams().map { list ->
        list.map { it.toOpponentClub() }
    }

    fun getTeamById(id: String): Flow<OpponentClub?> = teamDao.getTeamById(id).map { it?.toOpponentClub() }

    suspend fun saveTeam(opponent: OpponentClub, isUserTeam: Boolean = false) {
        teamDao.insertTeam(TeamEntity.fromOpponentClub(opponent, isUserTeam))
    }

    suspend fun saveTeams(opponents: List<OpponentClub>) {
        teamDao.insertTeams(opponents.map { TeamEntity.fromOpponentClub(it) })
    }

    suspend fun deleteTeam(id: String) {
        teamDao.deleteTeamById(id)
    }

    // Match History Streams & Operations
    val matchHistory: Flow<List<HistoricalMatchRecord>> = matchHistoryDao.getAllMatchHistory().map { list ->
        list.map { it.toDomain() }
    }

    fun getRecentMatches(limit: Int = 10): Flow<List<HistoricalMatchRecord>> =
        matchHistoryDao.getRecentMatches(limit).map { list -> list.map { it.toDomain() } }

    fun getMatchesByResult(result: MatchResultType): Flow<List<HistoricalMatchRecord>> =
        matchHistoryDao.getMatchesByResult(result).map { list -> list.map { it.toDomain() } }

    val winsCount: Flow<Int> = matchHistoryDao.getWinsCount()
    val drawsCount: Flow<Int> = matchHistoryDao.getDrawsCount()
    val lossesCount: Flow<Int> = matchHistoryDao.getLossesCount()

    suspend fun recordMatch(record: HistoricalMatchRecord) {
        matchHistoryDao.insertMatch(MatchHistoryEntity.fromDomain(record))
    }

    suspend fun recordMatches(records: List<HistoricalMatchRecord>) {
        matchHistoryDao.insertMatches(records.map { MatchHistoryEntity.fromDomain(it) })
    }

    suspend fun deleteMatch(matchId: String) {
        matchHistoryDao.deleteMatchById(matchId)
    }

    suspend fun clearMatchHistory() {
        matchHistoryDao.deleteAllMatchHistory()
    }
}
