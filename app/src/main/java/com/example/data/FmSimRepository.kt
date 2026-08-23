package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class FmSimRepository {

    private val _squad = MutableStateFlow<List<Player>>(generateInitialSquad())
    val squad: StateFlow<List<Player>> = _squad.asStateFlow()

    private val _tactics = MutableStateFlow(TeamTactics())
    val tactics: StateFlow<TeamTactics> = _tactics.asStateFlow()

    private val _scoutingPool = MutableStateFlow<List<ScoutProspect>>(generateInitialScouts())
    val scoutingPool: StateFlow<List<ScoutProspect>> = _scoutingPool.asStateFlow()

    private val _matchHistory = MutableStateFlow<List<HistoricalMatchRecord>>(generateInitialMatchHistory())
    val matchHistory: StateFlow<List<HistoricalMatchRecord>> = _matchHistory.asStateFlow()

    private val _leaderboards = MutableStateFlow<List<LeaderboardEntry>>(generateInitialLeaderboard())
    val leaderboards: StateFlow<List<LeaderboardEntry>> = _leaderboards.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            managerId = "MGR-7749-AUTH",
            encryptedAuthToken = "SHA256:9f8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c",
            securityHash = "AES-GCM:e5b6c7d8a9f0e1d2c3b4a5f6e7d8",
            managerName = "Coach Alex Sterling",
            clubName = "Apex Tacticians FC",
            clubBadgeColor = 0xFF00E5FF,
            region = "Global Elite",
            reputationStars = 4.5f,
            careerWins = 42,
            careerDraws = 14,
            careerLosses = 9,
            totalTrophies = 5,
            eloRating = 2185,
            transferBudgetMillions = 85.5,
            weeklyWageBudgetThousands = 1250,
            currentWeeklyWageExpenseThousands = 890,
            clubPhilosophy = "High-Intensity Positional Dominance & Fluid Counter-Press"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _opponents = MutableStateFlow(generateOpponents())
    val opponents: StateFlow<List<OpponentClub>> = _opponents.asStateFlow()

    private val _financialStatement = MutableStateFlow(ClubFinancialStatement())
    val financialStatement: StateFlow<ClubFinancialStatement> = _financialStatement.asStateFlow()

    private val _boardObjectives = MutableStateFlow(generateInitialBoardObjectives())
    val boardObjectives: StateFlow<List<BoardObjective>> = _boardObjectives.asStateFlow()

    private val _incomingBids = MutableStateFlow(generateInitialIncomingBids())
    val incomingBids: StateFlow<List<IncomingTransferBid>> = _incomingBids.asStateFlow()

    private val _calendarFixtures = MutableStateFlow(generateInitialCalendarFixtures())
    val calendarFixtures: StateFlow<List<CalendarFixture>> = _calendarFixtures.asStateFlow()

    private val _pressQuestions = MutableStateFlow(generateInitialPressQuestions())
    val pressQuestions: StateFlow<List<PressConferenceQuestion>> = _pressQuestions.asStateFlow()

    private val _youthAcademy = MutableStateFlow(generateInitialYouthAcademy())
    val youthAcademy: StateFlow<List<Player>> = _youthAcademy.asStateFlow()

    fun updateTactics(newTactics: TeamTactics) {
        _tactics.value = newTactics
    }

    fun setSetPieceTakers(
        captainId: String?,
        penaltyId: String?,
        freeKickId: String?,
        cornerId: String?
    ) {
        val currentList = _squad.value.map { player ->
            player.copy(
                isCaptain = player.id == captainId,
                isPenaltyTaker = player.id == penaltyId,
                isFreeKickTaker = player.id == freeKickId,
                isCornerTaker = player.id == cornerId
            )
        }
        _squad.value = currentList
    }

    fun updateTrainingFocus(playerId: String, focus: TrainingFocus) {
        val updated = _squad.value.map { player ->
            if (player.id == playerId) player.copy(trainingFocus = focus) else player
        }
        _squad.value = updated
    }

    fun trainSquadDay() {
        val facilityBonus = when (_financialStatement.value.trainingFacilityTier) {
            FacilityTier.STATE_OF_THE_ART -> 1.35f
            FacilityTier.PROFESSIONAL -> 1.20f
            FacilityTier.IMPROVED -> 1.10f
            FacilityTier.BASIC -> 1.0f
        }

        val updated = _squad.value.map { player ->
            val addedProgress = (Random.nextInt(10, 22) * facilityBonus).toInt()
            val newProgress = player.trainingProgressPct + addedProgress
            if (newProgress >= 100 && player.overallRating < player.potentialRating) {
                // Player leveled up an attribute
                val attr = player.attributes
                val upgradedAttr = when (player.trainingFocus) {
                    TrainingFocus.FINISHING_ATTACK -> attr.copy(shooting = (attr.shooting + 1).coerceAtMost(99), dribbling = (attr.dribbling + 1).coerceAtMost(99))
                    TrainingFocus.PLAYMAKING_VISION -> attr.copy(passing = (attr.passing + 1).coerceAtMost(99), tacticalIq = (attr.tacticalIq + 1).coerceAtMost(99))
                    TrainingFocus.DEFENSIVE_SOLIDITY -> attr.copy(defending = (attr.defending + 1).coerceAtMost(99), physicality = (attr.physicality + 1).coerceAtMost(99))
                    TrainingFocus.PACE_ACCELERATION -> attr.copy(pace = (attr.pace + 1).coerceAtMost(99))
                    TrainingFocus.GOALKEEPING_REFLEXES -> attr.copy(defending = (attr.defending + 1).coerceAtMost(99), tacticalIq = (attr.tacticalIq + 1).coerceAtMost(99))
                    TrainingFocus.BALANCED -> attr.copy(tacticalIq = (attr.tacticalIq + 1).coerceAtMost(99), passing = (attr.passing + 1).coerceAtMost(99))
                }
                player.copy(
                    overallRating = (player.overallRating + 1).coerceAtMost(player.potentialRating),
                    attributes = upgradedAttr,
                    trainingProgressPct = newProgress - 100,
                    stamina = (player.stamina - 4).coerceAtLeast(50),
                    marketValueMillions = player.marketValueMillions + (Random.nextDouble(2.0, 5.0))
                )
            } else {
                player.copy(
                    trainingProgressPct = newProgress.coerceAtMost(99),
                    stamina = (player.stamina + 5).coerceAtMost(100)
                )
            }
        }
        _squad.value = updated
    }

    fun promoteYouthPlayer(playerId: String): Boolean {
        val youth = _youthAcademy.value.find { it.id == playerId } ?: return false
        _youthAcademy.value = _youthAcademy.value.filter { it.id != playerId }
        _squad.value = _squad.value + youth.copy(isYouthProspect = false, squadStatus = "Promoted Youth")
        return true
    }

    fun upgradeFacility(facilityType: String): Boolean {
        val currentFin = _financialStatement.value
        val user = _userProfile.value

        when (facilityType.lowercase()) {
            "training" -> {
                val nextTier = when (currentFin.trainingFacilityTier) {
                    FacilityTier.BASIC -> FacilityTier.IMPROVED
                    FacilityTier.IMPROVED -> FacilityTier.PROFESSIONAL
                    FacilityTier.PROFESSIONAL -> FacilityTier.STATE_OF_THE_ART
                    FacilityTier.STATE_OF_THE_ART -> return false
                }
                if (user.transferBudgetMillions < nextTier.upgradeCostMillions) return false
                _userProfile.value = user.copy(transferBudgetMillions = user.transferBudgetMillions - nextTier.upgradeCostMillions)
                _financialStatement.value = currentFin.copy(trainingFacilityTier = nextTier)
                return true
            }
            "youth" -> {
                val nextTier = when (currentFin.youthAcademyTier) {
                    FacilityTier.BASIC -> FacilityTier.IMPROVED
                    FacilityTier.IMPROVED -> FacilityTier.PROFESSIONAL
                    FacilityTier.PROFESSIONAL -> FacilityTier.STATE_OF_THE_ART
                    FacilityTier.STATE_OF_THE_ART -> return false
                }
                if (user.transferBudgetMillions < nextTier.upgradeCostMillions) return false
                _userProfile.value = user.copy(transferBudgetMillions = user.transferBudgetMillions - nextTier.upgradeCostMillions)
                _financialStatement.value = currentFin.copy(youthAcademyTier = nextTier)
                return true
            }
            "stadium" -> {
                val cost = 30.0
                if (user.transferBudgetMillions < cost) return false
                _userProfile.value = user.copy(transferBudgetMillions = user.transferBudgetMillions - cost)
                _financialStatement.value = currentFin.copy(
                    stadiumCapacity = currentFin.stadiumCapacity + 12000,
                    weeklyTicketRevenue = currentFin.weeklyTicketRevenue + 0.40
                )
                return true
            }
            else -> return false
        }
    }

    fun respondToTransferBid(bidId: String, accept: Boolean): Boolean {
        val bid = _incomingBids.value.find { it.id == bidId } ?: return false
        if (accept) {
            // Sell player
            _squad.value = _squad.value.filter { it.id != bid.playerId }
            val user = _userProfile.value
            _userProfile.value = user.copy(
                transferBudgetMillions = user.transferBudgetMillions + bid.offerAmountMillions
            )
            _incomingBids.value = _incomingBids.value.filter { it.id != bidId }
            return true
        } else {
            // Reject bid
            _incomingBids.value = _incomingBids.value.filter { it.id != bidId }
            return false
        }
    }

    fun answerPressQuestion(questionId: String, option: PressResponseOption) {
        val user = _userProfile.value
        _userProfile.value = user.copy(
            eloRating = (user.eloRating + option.reputationImpact * 10).coerceAtLeast(800)
        )
        // Boost/decrease squad morale
        val delta = option.moraleImpact
        _squad.value = _squad.value.map {
            it.copy(morale = (it.morale + delta).coerceIn(40, 100))
        }
        // Remove answered question and add a new dynamic one if empty
        val remaining = _pressQuestions.value.filter { it.id != questionId }
        _pressQuestions.value = if (remaining.isEmpty()) generateInitialPressQuestions() else remaining
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
    }

    fun advanceCalendarDay() {
        trainSquadDay()
        // Collect weekly revenues
        val fin = _financialStatement.value
        val user = _userProfile.value
        val netWeeklyMillions = (fin.weeklyTicketRevenue + fin.weeklySponsorshipIncome + fin.weeklyMerchandiseRevenue) - (fin.weeklyWageExpenditure + fin.weeklyFacilityMaintenance)
        _userProfile.value = user.copy(
            transferBudgetMillions = (user.transferBudgetMillions + (netWeeklyMillions / 7.0)).coerceAtLeast(0.0)
        )
    }


    fun swapPlayerRoles(playerAId: String, playerBId: String) {
        val currentList = _squad.value.toMutableList()
        val indexA = currentList.indexOfFirst { it.id == playerAId }
        val indexB = currentList.indexOfFirst { it.id == playerBId }

        if (indexA != -1 && indexB != -1) {
            val playerA = currentList[indexA]
            val playerB = currentList[indexB]

            // Swap starter status and slot indices
            val updatedA = playerA.copy(
                isStarter = playerB.isStarter,
                starterSlotIndex = playerB.starterSlotIndex
            )
            val updatedB = playerB.copy(
                isStarter = playerA.isStarter,
                starterSlotIndex = playerA.starterSlotIndex
            )

            currentList[indexA] = updatedA
            currentList[indexB] = updatedB
            _squad.value = currentList
        }
    }

    fun assignPlayerToSlot(playerId: String, slotIndex: Int) {
        val currentList = _squad.value.toMutableList()
        val targetPlayerIdx = currentList.indexOfFirst { it.id == playerId }
        if (targetPlayerIdx == -1) return

        // Check if another player currently occupies that slot
        val occupantIdx = currentList.indexOfFirst { it.isStarter && it.starterSlotIndex == slotIndex }
        val targetPlayer = currentList[targetPlayerIdx]

        if (occupantIdx != -1 && occupantIdx != targetPlayerIdx) {
            val occupant = currentList[occupantIdx]
            currentList[occupantIdx] = occupant.copy(
                isStarter = targetPlayer.isStarter,
                starterSlotIndex = targetPlayer.starterSlotIndex
            )
        }

        currentList[targetPlayerIdx] = targetPlayer.copy(
            isStarter = true,
            starterSlotIndex = slotIndex
        )
        _squad.value = currentList
    }

    fun scoutRegionRefresh(region: ScoutRegion) {
        val currentList = _scoutingPool.value.toMutableList()
        // Generate 2 new dynamic prospects for this region
        val newProspects = generateProspectsForRegion(region, 2)
        // Remove older uncompleted ones in that region and prepend new
        val filtered = currentList.filter { it.region != region || it.scoutingProgress >= 1.0f }
        _scoutingPool.value = newProspects + filtered
    }

    fun advanceScoutInvestigation(prospectId: String, progressIncrement: Float) {
        val list = _scoutingPool.value.map { prospect ->
            if (prospect.id == prospectId) {
                val newProgress = (prospect.scoutingProgress + progressIncrement).coerceAtMost(1.0f)
                prospect.copy(
                    scoutingProgress = newProgress,
                    isScouted = newProgress >= 1.0f
                )
            } else prospect
        }
        _scoutingPool.value = list
    }

    fun signScoutedPlayer(prospectId: String): Boolean {
        val prospect = _scoutingPool.value.find { it.id == prospectId } ?: return false
        val user = _userProfile.value

        if (user.transferBudgetMillions < prospect.marketValueMillions) {
            return false // Insufficient funds
        }

        // Add player to squad
        val newPlayer = Player(
            id = "PLY-SIGNED-${System.currentTimeMillis() % 10000}",
            name = prospect.name,
            number = Random.nextInt(12, 99),
            primaryRole = prospect.primaryRole,
            overallRating = prospect.currentOvr,
            potentialRating = prospect.maxPotential,
            age = prospect.age,
            nationality = prospect.nationality,
            flagEmoji = prospect.flag,
            attributes = prospect.attributes,
            marketValueMillions = prospect.marketValueMillions,
            weeklyWageThousands = prospect.wageWeeklyThousands,
            isStarter = false,
            starterSlotIndex = -1
        )

        _squad.value = _squad.value + newPlayer
        // Remove from pool
        _scoutingPool.value = _scoutingPool.value.filter { it.id != prospectId }

        // Deduct transfer budget
        _userProfile.value = user.copy(
            transferBudgetMillions = user.transferBudgetMillions - prospect.marketValueMillions,
            currentWeeklyWageExpenseThousands = user.currentWeeklyWageExpenseThousands + prospect.wageWeeklyThousands
        )
        return true
    }

    fun recordMatchCompletion(record: HistoricalMatchRecord) {
        _matchHistory.value = listOf(record) + _matchHistory.value
        // Update user stats and leaderboards
        val user = _userProfile.value
        val isWin = record.resultType == MatchResultType.WIN
        val isDraw = record.resultType == MatchResultType.DRAW
        val eloDelta = when (record.resultType) {
            MatchResultType.WIN -> +28
            MatchResultType.DRAW -> +5
            MatchResultType.LOSS -> -22
        }

        _userProfile.value = user.copy(
            careerWins = if (isWin) user.careerWins + 1 else user.careerWins,
            careerDraws = if (isDraw) user.careerDraws + 1 else user.careerDraws,
            careerLosses = if (!isWin && !isDraw) user.careerLosses + 1 else user.careerLosses,
            eloRating = (user.eloRating + eloDelta).coerceAtLeast(800)
        )

        // Update player season stats
        val userScorers = record.userGoalScorers
        val updatedSquad = _squad.value.map { player ->
            if (player.isStarter) {
                val goalsCount = userScorers.count { it.contains(player.name.take(6), ignoreCase = true) }
                val stats = player.seasonStats
                val newRating = (Random.nextDouble(6.8, 8.9)).toFloat()
                player.copy(
                    stamina = (player.stamina - Random.nextInt(8, 18)).coerceAtLeast(40),
                    seasonStats = stats.copy(
                        appearances = stats.appearances + 1,
                        goals = stats.goals + goalsCount,
                        assists = stats.assists + if (goalsCount > 0 && Random.nextBoolean()) 1 else 0,
                        expectedGoals = stats.expectedGoals + (Random.nextFloat() * 0.4f),
                        minutesPlayed = stats.minutesPlayed + 90,
                        avgMatchRating = (stats.avgMatchRating * stats.appearances + newRating) / (stats.appearances + 1)
                    ),
                    formHistory = (player.formHistory.takeLast(4) + newRating)
                )
            } else {
                player.copy(
                    stamina = (player.stamina + 15).coerceAtMost(100)
                )
            }
        }
        _squad.value = updatedSquad

        // Update current user entry in leaderboard
        val updatedLeaderboard = _leaderboards.value.map { entry ->
            if (entry.isCurrentUser) {
                val newW = if (isWin) entry.wins + 1 else entry.wins
                val newD = if (isDraw) entry.draws + 1 else entry.draws
                val newL = if (!isWin && !isDraw) entry.losses + 1 else entry.losses
                val newPoints = newW * 3 + newD
                entry.copy(
                    matchesPlayed = entry.matchesPlayed + 1,
                    wins = newW,
                    draws = newD,
                    losses = newL,
                    goalsFor = entry.goalsFor + record.homeScore,
                    goalsAgainst = entry.goalsAgainst + record.awayScore,
                    goalDifference = entry.goalDifference + (record.homeScore - record.awayScore),
                    points = newPoints,
                    eloRating = user.eloRating + eloDelta
                )
            } else {
                entry
            }
        }.sortedByDescending { it.points * 1000 + it.goalDifference }
        .mapIndexed { index, item -> item.copy(rank = index + 1) }

        _leaderboards.value = updatedLeaderboard
    }

    private fun generateInitialSquad(): List<Player> {
        val formationSlots = FormationType.F_433.layoutSlots
        return listOf(
            // Starting 11 (mapped to slots 0..10)
            Player(
                id = "P1", name = "Thibaut Vance", number = 1, primaryRole = PlayerRole.GK,
                overallRating = 88, potentialRating = 91, age = 27, nationality = "Belgium", flagEmoji = "🇧🇪",
                attributes = PlayerAttributes(pace = 65, shooting = 20, passing = 74, dribbling = 55, defending = 89, physicality = 86, tacticalIq = 90),
                marketValueMillions = 52.0, weeklyWageThousands = 140, isStarter = true, starterSlotIndex = 0, isCaptain = false
            ),
            Player(
                id = "P2", name = "Alphonso Drake", number = 3, primaryRole = PlayerRole.LB,
                overallRating = 85, potentialRating = 90, age = 23, nationality = "Canada", flagEmoji = "🇨🇦",
                attributes = PlayerAttributes(pace = 95, shooting = 68, passing = 81, dribbling = 87, defending = 79, physicality = 82, tacticalIq = 84),
                marketValueMillions = 68.0, weeklyWageThousands = 120, isStarter = true, starterSlotIndex = 1
            ),
            Player(
                id = "P3", name = "Ruben Valente", number = 4, primaryRole = PlayerRole.CB,
                overallRating = 89, potentialRating = 92, age = 26, nationality = "Portugal", flagEmoji = "🇵🇹",
                attributes = PlayerAttributes(pace = 78, shooting = 45, passing = 83, dribbling = 72, defending = 91, physicality = 88, tacticalIq = 93),
                marketValueMillions = 82.0, weeklyWageThousands = 180, isStarter = true, starterSlotIndex = 2, isCaptain = true
            ),
            Player(
                id = "P4", name = "Gabriel Silva", number = 5, primaryRole = PlayerRole.CB,
                overallRating = 86, potentialRating = 89, age = 25, nationality = "Brazil", flagEmoji = "🇧🇷",
                attributes = PlayerAttributes(pace = 80, shooting = 40, passing = 77, dribbling = 68, defending = 87, physicality = 89, tacticalIq = 86),
                marketValueMillions = 55.0, weeklyWageThousands = 110, isStarter = true, starterSlotIndex = 3
            ),
            Player(
                id = "P5", name = "Trent Alexander", number = 66, primaryRole = PlayerRole.RB,
                overallRating = 87, potentialRating = 90, age = 25, nationality = "England", flagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
                attributes = PlayerAttributes(pace = 82, shooting = 78, passing = 93, dribbling = 84, defending = 80, physicality = 77, tacticalIq = 91),
                marketValueMillions = 75.0, weeklyWageThousands = 160, isStarter = true, starterSlotIndex = 4
            ),
            Player(
                id = "P6", name = "Rodri Casemiro", number = 16, primaryRole = PlayerRole.CDM,
                overallRating = 90, potentialRating = 91, age = 28, nationality = "Spain", flagEmoji = "🇪🇸",
                attributes = PlayerAttributes(pace = 72, shooting = 79, passing = 91, dribbling = 83, defending = 90, physicality = 88, tacticalIq = 95),
                marketValueMillions = 95.0, weeklyWageThousands = 210, isStarter = true, starterSlotIndex = 5
            ),
            Player(
                id = "P7", name = "Luka Modrician", number = 10, primaryRole = PlayerRole.CM,
                overallRating = 88, potentialRating = 88, age = 29, nationality = "Croatia", flagEmoji = "🇭🇷",
                attributes = PlayerAttributes(pace = 76, shooting = 82, passing = 94, dribbling = 89, defending = 74, physicality = 73, tacticalIq = 96),
                marketValueMillions = 60.0, weeklyWageThousands = 175, isStarter = true, starterSlotIndex = 6
            ),
            Player(
                id = "P8", name = "Jude Bellingham", number = 8, primaryRole = PlayerRole.CAM,
                overallRating = 91, potentialRating = 96, age = 21, nationality = "England", flagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
                attributes = PlayerAttributes(pace = 84, shooting = 88, passing = 89, dribbling = 90, defending = 80, physicality = 87, tacticalIq = 92),
                marketValueMillions = 145.0, weeklyWageThousands = 240, isStarter = true, starterSlotIndex = 7
            ),
            Player(
                id = "P9", name = "Vinicius Junior", number = 7, primaryRole = PlayerRole.LW,
                overallRating = 91, potentialRating = 95, age = 24, nationality = "Brazil", flagEmoji = "🇧🇷",
                attributes = PlayerAttributes(pace = 96, shooting = 87, passing = 84, dribbling = 95, defending = 42, physicality = 76, tacticalIq = 88),
                marketValueMillions = 150.0, weeklyWageThousands = 250, isStarter = true, starterSlotIndex = 8
            ),
            Player(
                id = "P10", name = "Erling Haaland", number = 9, primaryRole = PlayerRole.ST,
                overallRating = 92, potentialRating = 95, age = 24, nationality = "Norway", flagEmoji = "🇳🇴",
                attributes = PlayerAttributes(pace = 91, shooting = 95, passing = 72, dribbling = 82, defending = 45, physicality = 93, tacticalIq = 90),
                marketValueMillions = 165.0, weeklyWageThousands = 280, isStarter = true, starterSlotIndex = 9
            ),
            Player(
                id = "P11", name = "Bukayo Saka", number = 11, primaryRole = PlayerRole.RW,
                overallRating = 89, potentialRating = 93, age = 23, nationality = "England", flagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
                attributes = PlayerAttributes(pace = 89, shooting = 85, passing = 86, dribbling = 91, defending = 62, physicality = 78, tacticalIq = 89),
                marketValueMillions = 110.0, weeklyWageThousands = 190, isStarter = true, starterSlotIndex = 10
            ),

            // Substitutes (Bench)
            Player(
                id = "P12", name = "Diogo Costa", number = 12, primaryRole = PlayerRole.GK,
                overallRating = 82, potentialRating = 88, age = 24, nationality = "Portugal", flagEmoji = "🇵🇹",
                attributes = PlayerAttributes(pace = 60, shooting = 15, passing = 72, dribbling = 50, defending = 83, physicality = 80, tacticalIq = 82),
                marketValueMillions = 32.0, weeklyWageThousands = 65, isStarter = false
            ),
            Player(
                id = "P13", name = "William Saliba", number = 2, primaryRole = PlayerRole.CB,
                overallRating = 85, potentialRating = 91, age = 23, nationality = "France", flagEmoji = "🇫🇷",
                attributes = PlayerAttributes(pace = 83, shooting = 38, passing = 79, dribbling = 74, defending = 87, physicality = 84, tacticalIq = 87),
                marketValueMillions = 62.0, weeklyWageThousands = 100, isStarter = false
            ),
            Player(
                id = "P14", name = "Eduardo Camavinga", number = 14, primaryRole = PlayerRole.CDM,
                overallRating = 84, potentialRating = 92, age = 21, nationality = "France", flagEmoji = "🇫🇷",
                attributes = PlayerAttributes(pace = 81, shooting = 72, passing = 85, dribbling = 86, defending = 81, physicality = 83, tacticalIq = 87),
                marketValueMillions = 70.0, weeklyWageThousands = 115, isStarter = false
            ),
            Player(
                id = "P15", name = "Jamal Musiala", number = 42, primaryRole = PlayerRole.CAM,
                overallRating = 87, potentialRating = 94, age = 21, nationality = "Germany", flagEmoji = "🇩🇪",
                attributes = PlayerAttributes(pace = 87, shooting = 82, passing = 86, dribbling = 94, defending = 54, physicality = 68, tacticalIq = 90),
                marketValueMillions = 98.0, weeklyWageThousands = 150, isStarter = false
            ),
            Player(
                id = "P16", name = "Rafael Leao", number = 17, primaryRole = PlayerRole.LW,
                overallRating = 86, potentialRating = 90, age = 25, nationality = "Portugal", flagEmoji = "🇵🇹",
                attributes = PlayerAttributes(pace = 94, shooting = 83, passing = 78, dribbling = 91, defending = 35, physicality = 80, tacticalIq = 81),
                marketValueMillions = 72.0, weeklyWageThousands = 130, isStarter = false
            ),
            Player(
                id = "P17", name = "Julian Alvarez", number = 19, primaryRole = PlayerRole.ST,
                overallRating = 85, potentialRating = 91, age = 24, nationality = "Argentina", flagEmoji = "🇦🇷",
                attributes = PlayerAttributes(pace = 85, shooting = 87, passing = 80, dribbling = 86, defending = 55, physicality = 77, tacticalIq = 88),
                marketValueMillions = 75.0, weeklyWageThousands = 125, isStarter = false
            ),
            Player(
                id = "P18", name = "Federico Valverde", number = 15, primaryRole = PlayerRole.CM,
                overallRating = 88, potentialRating = 92, age = 26, nationality = "Uruguay", flagEmoji = "🇺🇾",
                attributes = PlayerAttributes(pace = 88, shooting = 84, passing = 87, dribbling = 83, defending = 80, physicality = 86, tacticalIq = 89),
                marketValueMillions = 88.0, weeklyWageThousands = 160, isStarter = false
            )
        )
    }

    private fun generateInitialScouts(): List<ScoutProspect> {
        return generateProspectsForRegion(ScoutRegion.SOUTH_AMERICA, 2) +
                generateProspectsForRegion(ScoutRegion.WESTERN_EUROPE, 2) +
                generateProspectsForRegion(ScoutRegion.AFRICA, 2) +
                generateProspectsForRegion(ScoutRegion.ASIA_PACIFIC, 2)
    }

    fun generateProspectsForRegion(region: ScoutRegion, count: Int): List<ScoutProspect> {
        val prospects = mutableListOf<ScoutProspect>()
        val timeSeed = System.currentTimeMillis() % 100000

        when (region) {
            ScoutRegion.SOUTH_AMERICA -> {
                prospects.add(
                    ScoutProspect(
                        id = "SCOUT-SA-1-$timeSeed",
                        name = "Lucas Endrickson",
                        age = 18,
                        nationality = "Brazil",
                        flag = "🇧🇷",
                        currentClub = "Palmeiras Academy",
                        region = region,
                        primaryRole = PlayerRole.ST,
                        currentOvr = 79,
                        minPotential = 91,
                        maxPotential = 96,
                        attributes = PlayerAttributes(pace = 92, shooting = 82, passing = 74, dribbling = 88, defending = 40, physicality = 81, tacticalIq = 85),
                        marketValueMillions = 28.5,
                        wageWeeklyThousands = 45,
                        report = ScoutReport(
                            scoutGrade = ScoutGrade.GRADE_S,
                            scoutAccuracy = 92,
                            estimatedPotential = "92 - 96 (Generational)",
                            scoutNotes = "Incredible burst speed, lethal left foot, natural finisher inside the box.",
                            recommendedRole = PlayerRole.ST,
                            wageExpectationThousands = 50,
                            transferFeeEstimateMillions = 32.0
                        ),
                        isScouted = true,
                        scoutingProgress = 1.0f
                    )
                )
                prospects.add(
                    ScoutProspect(
                        id = "SCOUT-SA-2-$timeSeed",
                        name = "Matias Almada-Cruz",
                        age = 20,
                        nationality = "Argentina",
                        flag = "🇦🇷",
                        currentClub = "River Plate Youth",
                        region = region,
                        primaryRole = PlayerRole.CAM,
                        currentOvr = 77,
                        minPotential = 87,
                        maxPotential = 92,
                        attributes = PlayerAttributes(pace = 84, shooting = 78, passing = 86, dribbling = 89, defending = 48, physicality = 68, tacticalIq = 88),
                        marketValueMillions = 19.0,
                        wageWeeklyThousands = 30,
                        report = ScoutReport(
                            scoutGrade = ScoutGrade.GRADE_A,
                            scoutAccuracy = 88,
                            estimatedPotential = "87 - 92",
                            scoutNotes = "Sublime close control in tight channels, excellent vision on through passes.",
                            recommendedRole = PlayerRole.CAM,
                            wageExpectationThousands = 35,
                            transferFeeEstimateMillions = 22.0
                        ),
                        isScouted = false,
                        scoutingProgress = 0.5f
                    )
                )
            }
            ScoutRegion.WESTERN_EUROPE -> {
                prospects.add(
                    ScoutProspect(
                        id = "SCOUT-WE-1-$timeSeed",
                        name = "Lamine Yamal-Navarro",
                        age = 17,
                        nationality = "Spain",
                        flag = "🇪🇸",
                        currentClub = "La Masia Res.",
                        region = region,
                        primaryRole = PlayerRole.RW,
                        currentOvr = 81,
                        minPotential = 93,
                        maxPotential = 97,
                        attributes = PlayerAttributes(pace = 90, shooting = 80, passing = 88, dribbling = 93, defending = 45, physicality = 66, tacticalIq = 91),
                        marketValueMillions = 42.0,
                        wageWeeklyThousands = 60,
                        report = ScoutReport(
                            scoutGrade = ScoutGrade.GRADE_S,
                            scoutAccuracy = 95,
                            estimatedPotential = "94 - 97 (World Class)",
                            scoutNotes = "Unplayable 1v1 dribbler with mature decision making beyond his years.",
                            recommendedRole = PlayerRole.RW,
                            wageExpectationThousands = 70,
                            transferFeeEstimateMillions = 50.0
                        ),
                        isScouted = true,
                        scoutingProgress = 1.0f
                    )
                )
                prospects.add(
                    ScoutProspect(
                        id = "SCOUT-WE-2-$timeSeed",
                        name = "Florian Wirtz-Kahn",
                        age = 21,
                        nationality = "Germany",
                        flag = "🇩🇪",
                        currentClub = "Bayer Prodigies",
                        region = region,
                        primaryRole = PlayerRole.CAM,
                        currentOvr = 83,
                        minPotential = 89,
                        maxPotential = 93,
                        attributes = PlayerAttributes(pace = 83, shooting = 81, passing = 89, dribbling = 88, defending = 58, physicality = 72, tacticalIq = 92),
                        marketValueMillions = 55.0,
                        wageWeeklyThousands = 85,
                        report = ScoutReport(
                            scoutGrade = ScoutGrade.GRADE_A,
                            scoutAccuracy = 84,
                            estimatedPotential = "89 - 93",
                            scoutNotes = "Reads the game two steps ahead. Elite between defensive lines.",
                            recommendedRole = PlayerRole.CAM,
                            wageExpectationThousands = 90,
                            transferFeeEstimateMillions = 62.0
                        ),
                        isScouted = false,
                        scoutingProgress = 0.35f
                    )
                )
            }
            ScoutRegion.AFRICA -> {
                prospects.add(
                    ScoutProspect(
                        id = "SCOUT-AF-1-$timeSeed",
                        name = "Victor Osimhen Jr.",
                        age = 19,
                        nationality = "Nigeria",
                        flag = "🇳🇬",
                        currentClub = "Lagos Academy",
                        region = region,
                        primaryRole = PlayerRole.ST,
                        currentOvr = 78,
                        minPotential = 88,
                        maxPotential = 93,
                        attributes = PlayerAttributes(pace = 94, shooting = 83, passing = 68, dribbling = 79, defending = 48, physicality = 89, tacticalIq = 80),
                        marketValueMillions = 21.0,
                        wageWeeklyThousands = 32,
                        report = ScoutReport(
                            scoutGrade = ScoutGrade.GRADE_A,
                            scoutAccuracy = 89,
                            estimatedPotential = "88 - 93",
                            scoutNotes = "Dominant physical monster with explosive acceleration and aerial power.",
                            recommendedRole = PlayerRole.ST,
                            wageExpectationThousands = 38,
                            transferFeeEstimateMillions = 24.0
                        ),
                        isScouted = true,
                        scoutingProgress = 1.0f
                    )
                )
                prospects.add(
                    ScoutProspect(
                        id = "SCOUT-AF-2-$timeSeed",
                        name = "Pape Matar Gueye",
                        age = 20,
                        nationality = "Senegal",
                        flag = "🇸🇳",
                        currentClub = "Génération Foot",
                        region = region,
                        primaryRole = PlayerRole.CDM,
                        currentOvr = 76,
                        minPotential = 86,
                        maxPotential = 90,
                        attributes = PlayerAttributes(pace = 82, shooting = 70, passing = 80, dribbling = 78, defending = 82, physicality = 86, tacticalIq = 83),
                        marketValueMillions = 14.5,
                        wageWeeklyThousands = 25,
                        report = ScoutReport(
                            scoutGrade = ScoutGrade.GRADE_B,
                            scoutAccuracy = 78,
                            estimatedPotential = "86 - 90",
                            scoutNotes = "High engine workhorse midfielder, sweeps loose balls and transitions cleanly.",
                            recommendedRole = PlayerRole.CDM,
                            wageExpectationThousands = 28,
                            transferFeeEstimateMillions = 16.0
                        ),
                        isScouted = false,
                        scoutingProgress = 0.2f
                    )
                )
            }
            ScoutRegion.ASIA_PACIFIC -> {
                prospects.add(
                    ScoutProspect(
                        id = "SCOUT-AP-1-$timeSeed",
                        name = "Kaoru Mitomatsu",
                        age = 21,
                        nationality = "Japan",
                        flag = "🇯🇵",
                        currentClub = "Kawasaki Elite",
                        region = region,
                        primaryRole = PlayerRole.LW,
                        currentOvr = 78,
                        minPotential = 87,
                        maxPotential = 91,
                        attributes = PlayerAttributes(pace = 91, shooting = 77, passing = 82, dribbling = 90, defending = 52, physicality = 71, tacticalIq = 86),
                        marketValueMillions = 18.0,
                        wageWeeklyThousands = 35,
                        report = ScoutReport(
                            scoutGrade = ScoutGrade.GRADE_A,
                            scoutAccuracy = 91,
                            estimatedPotential = "87 - 91",
                            scoutNotes = "Exceptional acceleration, PhD-level dribbling mechanics.",
                            recommendedRole = PlayerRole.LW,
                            wageExpectationThousands = 40,
                            transferFeeEstimateMillions = 21.0
                        ),
                        isScouted = true,
                        scoutingProgress = 1.0f
                    )
                )
                prospects.add(
                    ScoutProspect(
                        id = "SCOUT-AP-2-$timeSeed",
                        name = "Kang-In Parkson",
                        age = 20,
                        nationality = "South Korea",
                        flag = "🇰🇷",
                        currentClub = "Seoul FC",
                        region = region,
                        primaryRole = PlayerRole.CAM,
                        currentOvr = 77,
                        minPotential = 86,
                        maxPotential = 90,
                        attributes = PlayerAttributes(pace = 80, shooting = 78, passing = 87, dribbling = 88, defending = 46, physicality = 70, tacticalIq = 87),
                        marketValueMillions = 16.0,
                        wageWeeklyThousands = 28,
                        report = ScoutReport(
                            scoutGrade = ScoutGrade.GRADE_B,
                            scoutAccuracy = 82,
                            estimatedPotential = "86 - 90",
                            scoutNotes = "Dead ball specialist, pin-point crossing and press resistance.",
                            recommendedRole = PlayerRole.CAM,
                            wageExpectationThousands = 32,
                            transferFeeEstimateMillions = 18.5
                        ),
                        isScouted = false,
                        scoutingProgress = 0.4f
                    )
                )
            }
            ScoutRegion.EASTERN_EUROPE -> {
                prospects.add(
                    ScoutProspect(
                        id = "SCOUT-EE-1-$timeSeed",
                        name = "Radu Dragos",
                        age = 21,
                        nationality = "Romania",
                        flag = "🇷🇴",
                        currentClub = "Steaua Bucharest",
                        region = region,
                        primaryRole = PlayerRole.CB,
                        currentOvr = 76,
                        minPotential = 86,
                        maxPotential = 90,
                        attributes = PlayerAttributes(pace = 78, shooting = 35, passing = 72, dribbling = 65, defending = 83, physicality = 87, tacticalIq = 84),
                        marketValueMillions = 12.0,
                        wageWeeklyThousands = 22,
                        report = ScoutReport(
                            scoutGrade = ScoutGrade.GRADE_B,
                            scoutAccuracy = 85,
                            estimatedPotential = "86 - 90",
                            scoutNotes = "Aggressive tackler, commanding aerial presence in both penalty boxes.",
                            recommendedRole = PlayerRole.CB,
                            wageExpectationThousands = 26,
                            transferFeeEstimateMillions = 14.0
                        ),
                        isScouted = false,
                        scoutingProgress = 0.6f
                    )
                )
            }
            ScoutRegion.NORTH_AMERICA -> {
                prospects.add(
                    ScoutProspect(
                        id = "SCOUT-NA-1-$timeSeed",
                        name = "Christian Reyna-Adams",
                        age = 20,
                        nationality = "USA",
                        flag = "🇺🇸",
                        currentClub = "New York Academy",
                        region = region,
                        primaryRole = PlayerRole.RW,
                        currentOvr = 77,
                        minPotential = 87,
                        maxPotential = 91,
                        attributes = PlayerAttributes(pace = 88, shooting = 78, passing = 83, dribbling = 86, defending = 49, physicality = 74, tacticalIq = 83),
                        marketValueMillions = 15.5,
                        wageWeeklyThousands = 30,
                        report = ScoutReport(
                            scoutGrade = ScoutGrade.GRADE_B,
                            scoutAccuracy = 87,
                            estimatedPotential = "87 - 91",
                            scoutNotes = "High pace on the break, sharp cutting inside onto favored foot.",
                            recommendedRole = PlayerRole.RW,
                            wageExpectationThousands = 34,
                            transferFeeEstimateMillions = 17.5
                        ),
                        isScouted = false,
                        scoutingProgress = 0.5f
                    )
                )
            }
        }
        return prospects.take(count)
    }

    private fun generateInitialMatchHistory(): List<HistoricalMatchRecord> {
        return listOf(
            HistoricalMatchRecord(
                matchId = "MATCH-001",
                competition = "Global Super Cup QF",
                dateString = "Aug 18, 2026",
                homeTeam = "Apex Tacticians FC",
                awayTeam = "Manchester Blue FC",
                homeScore = 3,
                awayScore = 1,
                userTeamIsHome = true,
                matchRating = 8.6f,
                topPerformer = "Erling Haaland (9.2)",
                userGoalScorers = listOf("Erling Haaland (18')", "Jude Bellingham (54')", "Vinicius Junior (81')"),
                opponentGoalScorers = listOf("Kevin De Bruyne (67')"),
                xGHome = 2.84f,
                xGAway = 1.12f,
                possessionUser = 58,
                totalShotsUser = 14,
                managerXpGained = 450,
                resultType = MatchResultType.WIN
            ),
            HistoricalMatchRecord(
                matchId = "MATCH-002",
                competition = "Apex Premier League",
                dateString = "Aug 12, 2026",
                homeTeam = "Bayern Munich Elite",
                awayTeam = "Apex Tacticians FC",
                homeScore = 2,
                awayScore = 2,
                userTeamIsHome = false,
                matchRating = 7.8f,
                topPerformer = "Vinicius Junior (8.4)",
                userGoalScorers = listOf("Vinicius Junior (32')", "Luka Modrician (89')"),
                opponentGoalScorers = listOf("Harry Kane (12', 44')"),
                xGHome = 1.95f,
                xGAway = 2.10f,
                possessionUser = 52,
                totalShotsUser = 11,
                managerXpGained = 280,
                resultType = MatchResultType.DRAW
            ),
            HistoricalMatchRecord(
                matchId = "MATCH-003",
                competition = "Apex Premier League",
                dateString = "Aug 06, 2026",
                homeTeam = "Apex Tacticians FC",
                awayTeam = "Real Madrid CF",
                homeScore = 2,
                awayScore = 0,
                userTeamIsHome = true,
                matchRating = 8.9f,
                topPerformer = "Ruben Valente (9.0)",
                userGoalScorers = listOf("Bukayo Saka (41')", "Erling Haaland (77')"),
                opponentGoalScorers = emptyList(),
                xGHome = 2.45f,
                xGAway = 0.68f,
                possessionUser = 61,
                totalShotsUser = 16,
                managerXpGained = 500,
                resultType = MatchResultType.WIN
            )
        )
    }

    private fun generateInitialLeaderboard(): List<LeaderboardEntry> {
        return listOf(
            LeaderboardEntry(
                rank = 1, managerId = "MGR-001-UK", managerName = "Pep Guard", clubName = "Sky Blue Dominance",
                region = "Europe", eloRating = 2390, matchesPlayed = 38, wins = 29, draws = 6, losses = 3,
                goalsFor = 94, goalsAgainst = 28, goalDifference = 66, points = 93, winStreak = 5
            ),
            LeaderboardEntry(
                rank = 2, managerId = "MGR-002-ES", managerName = "Carlo Mystique", clubName = "Galactico Dynasty",
                region = "Europe", eloRating = 2340, matchesPlayed = 38, wins = 28, draws = 5, losses = 5,
                goalsFor = 88, goalsAgainst = 31, goalDifference = 57, points = 89, winStreak = 3
            ),
            LeaderboardEntry(
                rank = 3, managerId = "MGR-7749-AUTH", managerName = "Coach Alex Sterling", clubName = "Apex Tacticians FC",
                region = "Global Elite", eloRating = 2185, matchesPlayed = 36, wins = 25, draws = 7, losses = 4,
                goalsFor = 82, goalsAgainst = 29, goalDifference = 53, points = 82, winStreak = 2, isCurrentUser = true
            ),
            LeaderboardEntry(
                rank = 4, managerId = "MGR-004-BR", managerName = "Tite Samba", clubName = "Flamengo Warriors",
                region = "Americas", eloRating = 2140, matchesPlayed = 38, wins = 23, draws = 8, losses = 7,
                goalsFor = 74, goalsAgainst = 36, goalDifference = 38, points = 77, winStreak = 1
            ),
            LeaderboardEntry(
                rank = 5, managerId = "MGR-005-DE", managerName = "Jurgen Gegenpress", clubName = "Red Bull Leipzig",
                region = "Europe", eloRating = 2095, matchesPlayed = 38, wins = 22, draws = 7, losses = 9,
                goalsFor = 76, goalsAgainst = 42, goalDifference = 34, points = 73, winStreak = 0
            ),
            LeaderboardEntry(
                rank = 6, managerId = "MGR-006-JP", managerName = "Hajime Tactix", clubName = "Tokyo Samurai FC",
                region = "Asia-Pacific", eloRating = 2040, matchesPlayed = 38, wins = 21, draws = 7, losses = 10,
                goalsFor = 68, goalsAgainst = 40, goalDifference = 28, points = 70, winStreak = 2
            ),
            LeaderboardEntry(
                rank = 7, managerId = "MGR-007-NG", managerName = "Sunday Oliseh", clubName = "Super Eagles Pride",
                region = "Africa", eloRating = 1980, matchesPlayed = 38, wins = 19, draws = 9, losses = 10,
                goalsFor = 62, goalsAgainst = 44, goalDifference = 18, points = 66, winStreak = 1
            ),
            LeaderboardEntry(
                rank = 8, managerId = "MGR-008-US", managerName = "Jesse Marschland", clubName = "Seattle Sound Wave",
                region = "North America", eloRating = 1920, matchesPlayed = 38, wins = 18, draws = 8, losses = 12,
                goalsFor = 59, goalsAgainst = 47, goalDifference = 12, points = 62, winStreak = 0
            )
        )
    }

    private fun generateOpponents(): List<OpponentClub> {
        return listOf(
            OpponentClub(
                id = "OPP-01", name = "Manchester Blue FC", shortName = "MCI",
                badgeColorHex = 0xFF6CABDD, secondaryBadgeColorHex = 0xFFFFFFFF,
                league = "Apex Premier League", overallRating = 89, attackRating = 91, midfieldRating = 90, defenseRating = 87,
                managerName = "Pep Guard", formation = FormationType.F_433
            ),
            OpponentClub(
                id = "OPP-02", name = "Real Madrid CF", shortName = "RMA",
                badgeColorHex = 0xFFFFFFFF, secondaryBadgeColorHex = 0xFFFFD700,
                league = "Apex Premier League", overallRating = 90, attackRating = 92, midfieldRating = 89, defenseRating = 88,
                managerName = "Carlo Mystique", formation = FormationType.F_433
            ),
            OpponentClub(
                id = "OPP-03", name = "Bayern Munich Elite", shortName = "BAY",
                badgeColorHex = 0xFFDC052D, secondaryBadgeColorHex = 0xFF0066B2,
                league = "Apex Premier League", overallRating = 88, attackRating = 90, midfieldRating = 87, defenseRating = 86,
                managerName = "Vincent Komp", formation = FormationType.F_4231
            ),
            OpponentClub(
                id = "OPP-04", name = "Paris Saint-Germain", shortName = "PSG",
                badgeColorHex = 0xFF004170, secondaryBadgeColorHex = 0xFFDA291C,
                league = "Continental Super League", overallRating = 87, attackRating = 89, midfieldRating = 86, defenseRating = 85,
                managerName = "Luis Enrique", formation = FormationType.F_433
            ),
            OpponentClub(
                id = "OPP-05", name = "Inter Milan Stars", shortName = "INT",
                badgeColorHex = 0xFF001EA0, secondaryBadgeColorHex = 0xFF000000,
                league = "Continental Super League", overallRating = 86, attackRating = 86, midfieldRating = 87, defenseRating = 88,
                managerName = "Simone Inzaghi", formation = FormationType.F_352
            ),
            OpponentClub(
                id = "OPP-06", name = "Flamengo Regatas", shortName = "FLA",
                badgeColorHex = 0xFFC22A1E, secondaryBadgeColorHex = 0xFF000000,
                league = "Americas Champions Cup", overallRating = 83, attackRating = 85, midfieldRating = 82, defenseRating = 81,
                managerName = "Tite Silva", formation = FormationType.F_4231
            )
        )
    }

    private fun generateInitialBoardObjectives(): List<BoardObjective> {
        return listOf(
            BoardObjective(
                id = "OBJ-01",
                title = "Apex Super League Title Fight",
                description = "Finish in the Top 2 of the Apex Super League and secure direct Champions Cup qualification.",
                category = "Domestic League",
                targetDesc = "Top 2 Finish (Current: 3rd)",
                progressPct = 82,
                priority = "Crucial"
            ),
            BoardObjective(
                id = "OBJ-02",
                title = "Financial Discipline",
                description = "Maintain weekly wage expenditure below $1.25M/wk while generating positive net transfer profits.",
                category = "Financial",
                targetDesc = "Wage expense < $1.25M (Current: $0.89M)",
                progressPct = 95,
                priority = "High"
            ),
            BoardObjective(
                id = "OBJ-03",
                title = "Youth Development Pipeline",
                description = "Promote and give at least 5 first-team appearances to an academy wonderkid under 21 years old.",
                category = "Youth Academy",
                targetDesc = "1 of 2 Wonderkids Integrated",
                progressPct = 50,
                priority = "Medium"
            ),
            BoardObjective(
                id = "OBJ-04",
                title = "Global Continental Trophy Run",
                description = "Reach at least the Semi-Final stage of the Apex Global Champions League.",
                category = "Continental Cup",
                targetDesc = "Quarter-Finals Reached",
                progressPct = 70,
                priority = "Crucial"
            )
        )
    }

    private fun generateInitialIncomingBids(): List<IncomingTransferBid> {
        return listOf(
            IncomingTransferBid(
                id = "BID-01",
                playerId = "P10",
                playerName = "Erling Haaland",
                playerRole = PlayerRole.ST,
                playerRating = 92,
                offeringClubName = "Real Madrid CF",
                offerAmountMillions = 185.0,
                playerMarketValueMillions = 165.0,
                dateString = "Aug 22, 2026",
                isPending = true
            ),
            IncomingTransferBid(
                id = "BID-02",
                playerId = "P11",
                playerName = "Bukayo Saka",
                playerRole = PlayerRole.RW,
                playerRating = 89,
                offeringClubName = "Bayern Munich Elite",
                offerAmountMillions = 120.0,
                playerMarketValueMillions = 110.0,
                dateString = "Aug 21, 2026",
                isPending = true
            ),
            IncomingTransferBid(
                id = "BID-03",
                playerId = "P13",
                playerName = "William Saliba",
                playerRole = PlayerRole.CB,
                playerRating = 85,
                offeringClubName = "Paris Saint-Germain",
                offerAmountMillions = 72.0,
                playerMarketValueMillions = 62.0,
                dateString = "Aug 20, 2026",
                isPending = true
            )
        )
    }

    private fun generateInitialCalendarFixtures(): List<CalendarFixture> {
        return listOf(
            CalendarFixture(
                id = "FIX-01",
                dateString = "Aug 24, 2026",
                homeTeam = "Apex Tacticians FC",
                awayTeam = "Manchester Blue FC",
                competition = "Apex Premier League - Matchday 37",
                eventType = CalendarEventType.LEAGUE_MATCH,
                isUserMatch = true,
                isPlayed = false
            ),
            CalendarFixture(
                id = "FIX-02",
                dateString = "Aug 27, 2026",
                homeTeam = "Sky Blue Dominance",
                awayTeam = "Galactico Dynasty",
                competition = "Apex Premier League - Matchday 37",
                eventType = CalendarEventType.LEAGUE_MATCH,
                isUserMatch = false,
                homeScore = 2,
                awayScore = 1,
                isPlayed = true
            ),
            CalendarFixture(
                id = "FIX-03",
                dateString = "Aug 30, 2026",
                homeTeam = "Real Madrid CF",
                awayTeam = "Apex Tacticians FC",
                competition = "Apex Premier League - Matchday 38 (Finale)",
                eventType = CalendarEventType.LEAGUE_MATCH,
                isUserMatch = true,
                isPlayed = false
            ),
            CalendarFixture(
                id = "FIX-04",
                dateString = "Sep 05, 2026",
                homeTeam = "Apex Tacticians FC",
                awayTeam = "Flamengo Warriors",
                competition = "Global Club World Championship - Semi Final",
                eventType = CalendarEventType.CUP_MATCH,
                isUserMatch = true,
                isPlayed = false
            ),
            CalendarFixture(
                id = "FIX-05",
                dateString = "Sep 12, 2026",
                homeTeam = "Global All-Stars",
                awayTeam = "Apex Tacticians FC",
                competition = "Ballon d'Or Annual Super Gala",
                eventType = CalendarEventType.AWARDS_GALA,
                isUserMatch = true,
                isPlayed = false
            )
        )
    }

    private fun generateInitialPressQuestions(): List<PressConferenceQuestion> {
        return listOf(
            PressConferenceQuestion(
                id = "PRESS-01",
                reporterName = "Marcus Taylor",
                mediaOutlet = "Sky Sports News",
                questionText = "Coach, ahead of your clash against Manchester Blue, how confident are you in executing your high-pressing tactical philosophy?",
                contextType = "PRE_MATCH",
                options = listOf(
                    PressResponseOption("OPT-1", "We have prepared meticulously. Our players are determined to dominate from the opening whistle.", "Confident", +5, +3),
                    PressResponseOption("OPT-2", "They are a formidable side. We must remain disciplined and capitalize on counter-attacking moments.", "Pragmatic", +2, +1),
                    PressResponseOption("OPT-3", "The pressure is on them, not us. We are playing without fear.", "Passionate", +4, +2)
                )
            ),
            PressConferenceQuestion(
                id = "PRESS-02",
                reporterName = "Elena Rostova",
                mediaOutlet = "The Athletic",
                questionText = "With heavy transfer rumors linking your star forward to European giants, how is the dressing room handling the speculation?",
                contextType = "TRANSFER_SPECULATION",
                options = listOf(
                    PressResponseOption("OPT-4", "Our best players are fully committed to this project and our title ambition.", "Confident", +4, +2),
                    PressResponseOption("OPT-5", "Every player has a market value, but our focus is strictly on the next matchday.", "Pragmatic", +1, +0),
                    PressResponseOption("OPT-6", "The media generates stories, but we control our destiny on the pitch.", "Passionate", +3, +1)
                )
            )
        )
    }

    private fun generateInitialYouthAcademy(): List<Player> {
        return listOf(
            Player(
                id = "YOUTH-01",
                name = "Mateo Fernandez",
                number = 34,
                primaryRole = PlayerRole.CAM,
                overallRating = 74,
                potentialRating = 93,
                age = 17,
                nationality = "Argentina",
                flagEmoji = "🇦🇷",
                attributes = PlayerAttributes(pace = 86, shooting = 75, passing = 82, dribbling = 87, defending = 48, physicality = 68, tacticalIq = 80),
                marketValueMillions = 14.5,
                weeklyWageThousands = 8,
                isStarter = false,
                isYouthProspect = true,
                squadStatus = "Youth Wonderkid",
                trainingFocus = TrainingFocus.PLAYMAKING_VISION
            ),
            Player(
                id = "YOUTH-02",
                name = "Kobe Mainoo-Vane",
                number = 37,
                primaryRole = PlayerRole.CDM,
                overallRating = 75,
                potentialRating = 91,
                age = 18,
                nationality = "England",
                flagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
                attributes = PlayerAttributes(pace = 78, shooting = 68, passing = 80, dribbling = 82, defending = 78, physicality = 77, tacticalIq = 84),
                marketValueMillions = 16.0,
                weeklyWageThousands = 10,
                isStarter = false,
                isYouthProspect = true,
                squadStatus = "Youth Wonderkid",
                trainingFocus = TrainingFocus.DEFENSIVE_SOLIDITY
            ),
            Player(
                id = "YOUTH-03",
                name = "Kenji Takahashi",
                number = 41,
                primaryRole = PlayerRole.RW,
                overallRating = 72,
                potentialRating = 89,
                age = 17,
                nationality = "Japan",
                flagEmoji = "🇯🇵",
                attributes = PlayerAttributes(pace = 89, shooting = 72, passing = 75, dribbling = 84, defending = 42, physicality = 65, tacticalIq = 78),
                marketValueMillions = 9.5,
                weeklyWageThousands = 6,
                isStarter = false,
                isYouthProspect = true,
                squadStatus = "Youth Prospect",
                trainingFocus = TrainingFocus.FINISHING_ATTACK
            )
        )
    }
}

