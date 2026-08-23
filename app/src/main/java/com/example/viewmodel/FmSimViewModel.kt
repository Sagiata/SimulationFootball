package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FmSimRepository
import com.example.model.*
import com.example.utils.AudioEffectManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

enum class AppPhase {
    SPLASH_PROFILE_MENU,
    MAIN_CAREER
}

enum class ActiveScreenTab(val title: String, val iconName: String) {
    TACTICS_SQUAD("Tactics & Squad", "tactics"),
    LIVE_MATCH("Live Match", "match"),
    TRAINING("Training & Academy", "training"),
    TRANSFERS("Transfer Market", "transfers"),
    CLUB_MANAGEMENT("Club & Finances", "club"),
    CALENDAR("Calendar & Schedule", "calendar"),
    LEAGUE_TABLE("League & Cups", "league"),
    ANALYTICS("Player Analytics", "analytics"),
    MATCH_HISTORY("Match Archive", "history"),
    MANAGER_PROFILE("Manager Profile", "profile"),
    MULTIPLAYER_LOBBY("Online Multiplayer", "multiplayer")
}

class FmSimViewModel(
    private val repository: FmSimRepository = FmSimRepository()
) : ViewModel() {

    private val _appPhase = MutableStateFlow(AppPhase.SPLASH_PROFILE_MENU)
    val appPhase: StateFlow<AppPhase> = _appPhase.asStateFlow()

    val squad: StateFlow<List<Player>> = repository.squad
    val tactics: StateFlow<TeamTactics> = repository.tactics
    val scoutingPool: StateFlow<List<ScoutProspect>> = repository.scoutingPool
    val matchHistory: StateFlow<List<HistoricalMatchRecord>> = repository.matchHistory
    val leaderboards: StateFlow<List<LeaderboardEntry>> = repository.leaderboards
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val opponents: StateFlow<List<OpponentClub>> = repository.opponents
    val financialStatement: StateFlow<ClubFinancialStatement> = repository.financialStatement
    val boardObjectives: StateFlow<List<BoardObjective>> = repository.boardObjectives
    val incomingBids: StateFlow<List<IncomingTransferBid>> = repository.incomingBids
    val calendarFixtures: StateFlow<List<CalendarFixture>> = repository.calendarFixtures
    val pressQuestions: StateFlow<List<PressConferenceQuestion>> = repository.pressQuestions
    val youthAcademy: StateFlow<List<Player>> = repository.youthAcademy

    private val _currentTab = MutableStateFlow(ActiveScreenTab.TACTICS_SQUAD)
    val currentTab: StateFlow<ActiveScreenTab> = _currentTab.asStateFlow()

    // Selected player for detail modal / stats inspection
    private val _inspectedPlayer = MutableStateFlow<Player?>(null)
    val inspectedPlayer: StateFlow<Player?> = _inspectedPlayer.asStateFlow()

    // Squad swap selection state (for drag/tap interaction)
    private val _selectedForSwapPlayerId = MutableStateFlow<String?>(null)
    val selectedForSwapPlayerId: StateFlow<String?> = _selectedForSwapPlayerId.asStateFlow()

    // Selected scout region filter
    private val _selectedScoutRegion = MutableStateFlow(ScoutRegion.SOUTH_AMERICA)
    val selectedScoutRegion: StateFlow<ScoutRegion> = _selectedScoutRegion.asStateFlow()

    // Inspected Scout Prospect
    private val _inspectedProspect = MutableStateFlow<ScoutProspect?>(null)
    val inspectedProspect: StateFlow<ScoutProspect?> = _inspectedProspect.asStateFlow()

    // Active contract negotiation
    private val _activeNegotiation = MutableStateFlow<ContractNegotiation?>(null)
    val activeNegotiation: StateFlow<ContractNegotiation?> = _activeNegotiation.asStateFlow()

    // Live Match State
    private val _liveMatch = MutableStateFlow<LiveMatchState?>(null)
    val liveMatch: StateFlow<LiveMatchState?> = _liveMatch.asStateFlow()

    // Active Opponent Selection for next fixture
    private val _selectedOpponent = MutableStateFlow<OpponentClub?>(null)
    val selectedOpponent: StateFlow<OpponentClub?> = _selectedOpponent.asStateFlow()

    private var matchSimulationJob: Job? = null

    init {
        // Default select first opponent
        viewModelScope.launch {
            repository.opponents.collect { list ->
                if (_selectedOpponent.value == null && list.isNotEmpty()) {
                    _selectedOpponent.value = list.first()
                }
            }
        }
    }

    fun selectTab(tab: ActiveScreenTab) {
        AudioEffectManager.playClick()
        _currentTab.value = tab
    }

    fun inspectPlayer(player: Player?) {
        if (player != null) AudioEffectManager.playClick()
        _inspectedPlayer.value = player
    }

    fun selectPlayerForSwap(playerId: String?) {
        val current = _selectedForSwapPlayerId.value
        if (current == null) {
            AudioEffectManager.playClick()
            _selectedForSwapPlayerId.value = playerId
        } else if (current == playerId) {
            // Unselect if tapped same
            _selectedForSwapPlayerId.value = null
        } else {
            // Execute swap between current and tapped player
            if (playerId != null) {
                AudioEffectManager.playConfirm()
                repository.swapPlayerRoles(current, playerId)
            }
            _selectedForSwapPlayerId.value = null
        }
    }

    fun assignPlayerToPitchSlot(playerId: String, slotIndex: Int) {
        AudioEffectManager.playConfirm()
        repository.assignPlayerToSlot(playerId, slotIndex)
        _selectedForSwapPlayerId.value = null
    }

    fun updateFormation(formation: FormationType) {
        AudioEffectManager.playClick()
        val currentTactics = tactics.value
        repository.updateTactics(currentTactics.copy(formation = formation))
    }

    fun updateMentality(mentality: TeamMentality) {
        AudioEffectManager.playClick()
        val currentTactics = tactics.value
        repository.updateTactics(currentTactics.copy(mentality = mentality))
    }

    fun updatePassingStyle(passingStyle: PassingStyle) {
        AudioEffectManager.playClick()
        val currentTactics = tactics.value
        repository.updateTactics(currentTactics.copy(passingStyle = passingStyle))
    }

    fun updatePressing(pressing: PressingIntensity) {
        AudioEffectManager.playClick()
        val currentTactics = tactics.value
        repository.updateTactics(currentTactics.copy(pressingIntensity = pressing))
    }

    fun updateTempo(tempo: MatchTempo) {
        AudioEffectManager.playClick()
        val currentTactics = tactics.value
        repository.updateTactics(currentTactics.copy(matchTempo = tempo))
    }

    fun updateTacticalSliders(defensiveLine: Int, width: Int) {
        val currentTactics = tactics.value
        repository.updateTactics(currentTactics.copy(defensiveLineDepth = defensiveLine, width = width))
    }

    fun setSetPieceTakers(captainId: String?, penaltyId: String?, freeKickId: String?, cornerId: String?) {
        AudioEffectManager.playConfirm()
        repository.setSetPieceTakers(captainId, penaltyId, freeKickId, cornerId)
    }

    // Training & Youth Management
    fun updatePlayerTrainingFocus(playerId: String, focus: TrainingFocus) {
        AudioEffectManager.playClick()
        repository.updateTrainingFocus(playerId, focus)
    }

    fun executeTrainingSession() {
        AudioEffectManager.playWhistle()
        repository.trainSquadDay()
    }

    fun promoteYouthWonderkid(playerId: String) {
        AudioEffectManager.playConfirm()
        repository.promoteYouthPlayer(playerId)
    }

    // Facilities & Finances
    fun upgradeFacility(type: String): Boolean {
        val success = repository.upgradeFacility(type)
        if (success) {
            AudioEffectManager.playGoalCheer()
        }
        return success
    }

    // Transfer Market & Negotiations
    fun selectScoutRegion(region: ScoutRegion) {
        AudioEffectManager.playClick()
        _selectedScoutRegion.value = region
    }

    fun refreshScoutRegion(region: ScoutRegion) {
        AudioEffectManager.playClick()
        repository.scoutRegionRefresh(region)
    }

    fun inspectProspect(prospect: ScoutProspect?) {
        if (prospect != null) AudioEffectManager.playClick()
        _inspectedProspect.value = prospect
    }

    fun advanceScout(prospectId: String) {
        AudioEffectManager.playClick()
        repository.advanceScoutInvestigation(prospectId, 0.5f)
        val updated = scoutingPool.value.find { it.id == prospectId }
        _inspectedProspect.value = updated
    }

    fun startNegotiation(prospect: ScoutProspect) {
        AudioEffectManager.playClick()
        _activeNegotiation.value = ContractNegotiation(
            prospectId = prospect.id,
            playerName = prospect.name,
            playerRole = prospect.primaryRole,
            currentClub = prospect.currentClub,
            marketValueMillions = prospect.marketValueMillions,
            initialWageDemandThousands = prospect.wageWeeklyThousands,
            offeredWageThousands = prospect.wageWeeklyThousands,
            offeredSigningBonusMillions = (prospect.marketValueMillions * 0.1).coerceAtLeast(0.5),
            offeredReleaseClauseMillions = prospect.marketValueMillions * 2.0,
            offeredContractYears = 4,
            offeredSquadRole = if (prospect.currentOvr >= 84) "Key Player" else if (prospect.currentOvr >= 78) "First Team Regular" else "Rotation Player",
            agentMood = "Neutral",
            agentFeedbackMessage = "The agent is listening carefully to your contract proposal.",
            status = NegotiationStatus.IN_PROGRESS
        )
    }

    fun updateNegotiationValues(
        wageThousands: Int,
        signingBonusMillions: Double,
        releaseClauseMillions: Double,
        years: Int,
        squadRole: String
    ) {
        val current = _activeNegotiation.value ?: return
        _activeNegotiation.value = current.copy(
            offeredWageThousands = wageThousands,
            offeredSigningBonusMillions = signingBonusMillions,
            offeredReleaseClauseMillions = releaseClauseMillions,
            offeredContractYears = years,
            offeredSquadRole = squadRole
        )
    }

    fun submitContractProposal() {
        val current = _activeNegotiation.value ?: return
        val wageDiff = current.offeredWageThousands - current.initialWageDemandThousands

        when {
            wageDiff >= 0 -> {
                AudioEffectManager.playConfirm()
                _activeNegotiation.value = current.copy(
                    agentMood = "Eager",
                    agentFeedbackMessage = "Offer Accepted! The player is delighted with the financial terms and role offered.",
                    status = NegotiationStatus.ACCEPTED_BY_AGENT
                )
            }
            wageDiff >= -15 -> {
                AudioEffectManager.playClick()
                _activeNegotiation.value = current.copy(
                    agentMood = "Hesitant",
                    agentFeedbackMessage = "Counter-offer: The player requests at least $${current.initialWageDemandThousands}k/wk and a higher signing bonus.",
                    status = NegotiationStatus.COUNTER_OFFER
                )
            }
            else -> {
                AudioEffectManager.playCardAlert()
                _activeNegotiation.value = current.copy(
                    agentMood = "Frustrated",
                    agentFeedbackMessage = "Offer Rejected! The wage is far below the player's market expectation.",
                    status = NegotiationStatus.REJECTED_BY_AGENT
                )
            }
        }
    }

    fun finalizeActiveNegotiation(): Boolean {
        val current = _activeNegotiation.value ?: return false
        val success = repository.signScoutedPlayer(current.prospectId)
        if (success) {
            AudioEffectManager.playGoalCheer()
            _activeNegotiation.value = null
            _inspectedProspect.value = null
        }
        return success
    }

    fun dismissNegotiation() {
        _activeNegotiation.value = null
    }

    fun respondToIncomingBid(bidId: String, accept: Boolean) {
        if (accept) {
            AudioEffectManager.playGoalCheer()
        } else {
            AudioEffectManager.playClick()
        }
        repository.respondToTransferBid(bidId, accept)
    }

    // Media & Calendar
    fun answerPressQuestion(questionId: String, option: PressResponseOption) {
        AudioEffectManager.playConfirm()
        repository.answerPressQuestion(questionId, option)
    }

    fun advanceCalendarDay() {
        AudioEffectManager.playClick()
        repository.advanceCalendarDay()
    }

    // Match Engine
    fun selectOpponent(opponent: OpponentClub) {
        _selectedOpponent.value = opponent
    }

    fun startLiveMatch(opponent: OpponentClub) {
        _selectedOpponent.value = opponent
        AudioEffectManager.playWhistle()
        val userClub = userProfile.value.clubName
        val initialEvents = listOf(
            MatchEvent(
                minute = 0,
                isHomeTeam = true,
                eventType = MatchEventType.KICKOFF,
                title = "Match Kickoff",
                description = "The referee blows the whistle! $userClub vs ${opponent.name} in the Apex Super League."
            )
        )

        val starters = squad.value.filter { it.isStarter }
        val homeEntities = starters.mapIndexed { idx, player ->
            val slot = tactics.value.formation.layoutSlots.getOrNull(idx) ?: FormationSlot(idx, player.primaryRole, 0.3f, 0.5f)
            LivePitchEntity(
                id = player.id,
                name = player.name,
                isHome = true,
                role = player.primaryRole,
                x = slot.relativeX * 0.45f + 0.05f,
                y = slot.relativeY,
                hasBall = idx == 9 // striker kicks off
            )
        }

        val awayEntities = opponent.formation.layoutSlots.mapIndexed { idx, slot ->
            LivePitchEntity(
                id = "AWAY-$idx",
                name = "Opponent ${slot.targetRole.name}",
                isHome = false,
                role = slot.targetRole,
                x = 1.0f - (slot.relativeX * 0.45f + 0.05f),
                y = 1.0f - slot.relativeY,
                hasBall = false
            )
        }

        _liveMatch.value = LiveMatchState(
            matchId = "MATCH-${System.currentTimeMillis() % 10000}",
            homeTeamName = userClub,
            homeScore = 0,
            awayScore = 0,
            opponent = opponent,
            currentMinute = 0,
            matchStatus = MatchStatus.FIRST_HALF,
            stats = MatchStats(),
            events = initialEvents,
            ballPosition = Pair(0.5f, 0.5f),
            entities = homeEntities + awayEntities,
            simSpeed = 1,
            isPaused = false,
            substitutionsRemaining = 5,
            teamTalkGiven = false
        )

        _currentTab.value = ActiveScreenTab.LIVE_MATCH
        startMatchSimulationLoop()
    }

    fun setSimSpeed(speed: Int) {
        AudioEffectManager.playClick()
        _liveMatch.value = _liveMatch.value?.copy(simSpeed = speed)
    }

    fun togglePauseMatch() {
        val current = _liveMatch.value ?: return
        AudioEffectManager.playClick()
        _liveMatch.value = current.copy(isPaused = !current.isPaused)
    }

    fun performHalftimeTeamTalk(talkType: String) {
        val current = _liveMatch.value ?: return
        AudioEffectManager.playConfirm()

        val talkDesc = when (talkType) {
            "ENCOURAGE" -> "Team Talk: 'Keep believing! We have the quality to break them down!'"
            "DEMAND_MORE" -> "Team Talk: 'I expect much higher intensity in the second half! Demand more from yourselves!'"
            "PRAISE" -> "Team Talk: 'Brilliant discipline so far, keep maintaining this shape!'"
            else -> "Team Talk: 'Tighten up at the back and stay composed!'"
        }

        val updatedEvents = listOf(
            MatchEvent(
                minute = current.currentMinute,
                isHomeTeam = true,
                eventType = MatchEventType.TEAM_TALK,
                title = "Manager Team Talk",
                description = talkDesc
            )
        ) + current.events

        _liveMatch.value = current.copy(
            events = updatedEvents,
            teamTalkGiven = true
        )
    }

    fun makeMatchSubstitution(starterIdToSubOut: String, benchIdToSubIn: String) {
        val current = _liveMatch.value ?: return
        if (current.substitutionsRemaining <= 0) return

        val outPlayer = squad.value.find { it.id == starterIdToSubOut } ?: return
        val inPlayer = squad.value.find { it.id == benchIdToSubIn } ?: return

        AudioEffectManager.playWhistle()
        // Swap roles in squad
        repository.swapPlayerRoles(starterIdToSubOut, benchIdToSubIn)

        val updatedEntities = current.entities.map { entity ->
            if (entity.id == starterIdToSubOut) {
                entity.copy(
                    id = inPlayer.id,
                    name = inPlayer.name,
                    role = inPlayer.primaryRole
                )
            } else entity
        }

        val subEvent = MatchEvent(
            minute = current.currentMinute,
            isHomeTeam = true,
            eventType = MatchEventType.SUBSTITUTION,
            title = "Substitution (${current.homeTeamName})",
            description = "${inPlayer.name} replaces ${outPlayer.name} on the pitch.",
            playerName = inPlayer.name,
            secondaryPlayerName = outPlayer.name
        )

        _liveMatch.value = current.copy(
            entities = updatedEntities,
            events = listOf(subEvent) + current.events,
            substitutionsRemaining = current.substitutionsRemaining - 1
        )
    }

    fun instantSimulateMatch() {
        val current = _liveMatch.value ?: return
        if (current.matchStatus == MatchStatus.FULL_TIME) return

        AudioEffectManager.playWhistle()
        // Fast forward to 90 minutes
        var curState = current
        while (curState.currentMinute < 90) {
            curState = simulateMatchStep(curState)
        }
        _liveMatch.value = curState.copy(matchStatus = MatchStatus.FULL_TIME, isPaused = true)
        finishAndRecordMatch(curState)
    }

    private fun startMatchSimulationLoop() {
        matchSimulationJob?.cancel()
        matchSimulationJob = viewModelScope.launch {
            while (isActive) {
                val match = _liveMatch.value
                if (match == null || match.matchStatus == MatchStatus.FULL_TIME) {
                    break
                }

                if (!match.isPaused) {
                    val nextState = simulateMatchStep(match)
                    _liveMatch.value = nextState

                    if (nextState.matchStatus == MatchStatus.FULL_TIME) {
                        AudioEffectManager.playWhistle()
                        finishAndRecordMatch(nextState)
                        break
                    }
                }

                val speed = _liveMatch.value?.simSpeed ?: 1
                val delayTime = when (speed) {
                    4 -> 250L
                    2 -> 600L
                    else -> 1200L
                }
                delay(delayTime)
            }
        }
    }

    private fun simulateMatchStep(state: LiveMatchState): LiveMatchState {
        val nextMin = state.currentMinute + 1
        val newStatus = when {
            nextMin >= 90 -> MatchStatus.FULL_TIME
            nextMin == 45 -> MatchStatus.HALF_TIME
            nextMin > 45 -> MatchStatus.SECOND_HALF
            else -> MatchStatus.FIRST_HALF
        }

        val starters = squad.value.filter { it.isStarter }
        val currentTactics = tactics.value
        val mentalityBoost = currentTactics.mentality.attackBoost

        val homeOvr = (starters.map { it.overallRating }.average().takeIf { !it.isNaN() } ?: 85.0) + (mentalityBoost * 5)
        val awayOvr = state.opponent.overallRating.toDouble()

        var homeScore = state.homeScore
        var awayScore = state.awayScore
        val newEvents = state.events.toMutableList()
        var newStats = state.stats
        var goalCelebrationText = state.lastGoalCelebrationText

        // Ball movement simulation
        val ballX = (0.2f + (Random.nextFloat() * 0.6f)).coerceIn(0.05f, 0.95f)
        val ballY = (0.15f + (Random.nextFloat() * 0.7f)).coerceIn(0.1f, 0.9f)

        // Event simulation roll
        val eventRoll = Random.nextInt(100)
        val homeChance = (50 + (homeOvr - awayOvr) * 1.5).coerceIn(20.0, 80.0)

        if (eventRoll < 18) {
            // Attack / Shot event
            val isHomeAttack = Random.nextDouble(100.0) < homeChance
            if (isHomeAttack) {
                val attacker = starters.filter { it.primaryRole.category == PositionCategory.ATT || it.primaryRole.category == PositionCategory.MID }.randomOrNull()
                    ?: starters.random()
                val isGoal = Random.nextInt(100) < (attacker.attributes.shooting / 2.8).toInt()
                val xGInc = Random.nextFloat() * 0.25f + (if (isGoal) 0.35f else 0.05f)

                if (isGoal) {
                    homeScore += 1
                    AudioEffectManager.playGoalCheer()
                    val assister = starters.filter { it.id != attacker.id && (it.primaryRole.category == PositionCategory.MID || it.primaryRole.category == PositionCategory.ATT) }.randomOrNull()
                    val desc = if (assister != null) {
                        "GOAL! ${attacker.name} smashes it into the top corner following a pinpoint assist from ${assister.name}!"
                    } else {
                        "GOAL! Sensational solo finish by ${attacker.name}!"
                    }
                    goalCelebrationText = "${attacker.name} (${nextMin}')"
                    newEvents.add(
                        0,
                        MatchEvent(
                            minute = nextMin,
                            isHomeTeam = true,
                            eventType = MatchEventType.GOAL,
                            title = "GOAL! ${state.homeTeamName}",
                            description = desc,
                            playerName = attacker.name,
                            secondaryPlayerName = assister?.name
                        )
                    )
                    newStats = newStats.copy(
                        shotsHome = newStats.shotsHome + 1,
                        shotsOnTargetHome = newStats.shotsOnTargetHome + 1,
                        xGHome = newStats.xGHome + xGInc
                    )
                } else {
                    newEvents.add(
                        0,
                        MatchEvent(
                            minute = nextMin,
                            isHomeTeam = true,
                            eventType = MatchEventType.SAVED_SHOT,
                            title = "Great Save!",
                            description = "${attacker.name}'s stinging strike is deflected wide by the goalkeeper!",
                            playerName = attacker.name
                        )
                    )
                    newStats = newStats.copy(
                        shotsHome = newStats.shotsHome + 1,
                        shotsOnTargetHome = newStats.shotsOnTargetHome + 1,
                        xGHome = newStats.xGHome + xGInc,
                        cornersHome = newStats.cornersHome + 1
                    )
                }
            } else {
                // Away Attack
                val isAwayGoal = Random.nextInt(100) < (state.opponent.attackRating / 3.2).toInt()
                val xGInc = Random.nextFloat() * 0.22f + (if (isAwayGoal) 0.30f else 0.04f)

                if (isAwayGoal) {
                    awayScore += 1
                    AudioEffectManager.playGoalCheer()
                    val oppScorer = "${state.opponent.shortName} Striker"
                    goalCelebrationText = "$oppScorer (${nextMin}')"
                    newEvents.add(
                        0,
                        MatchEvent(
                            minute = nextMin,
                            isHomeTeam = false,
                            eventType = MatchEventType.GOAL,
                            title = "GOAL! ${state.opponent.name}",
                            description = "Dangerous counter-attack converted calmly by $oppScorer.",
                            playerName = oppScorer
                        )
                    )
                    newStats = newStats.copy(
                        shotsAway = newStats.shotsAway + 1,
                        shotsOnTargetAway = newStats.shotsOnTargetAway + 1,
                        xGAway = newStats.xGAway + xGInc
                    )
                } else {
                    newEvents.add(
                        0,
                        MatchEvent(
                            minute = nextMin,
                            isHomeTeam = false,
                            eventType = MatchEventType.MISSED_SHOT,
                            title = "Opponent Chance Missed",
                            description = "${state.opponent.name} fires wide from the edge of the penalty box.",
                            playerName = state.opponent.shortName
                        )
                    )
                    newStats = newStats.copy(
                        shotsAway = newStats.shotsAway + 1,
                        xGAway = newStats.xGAway + xGInc
                    )
                }
            }
        } else if (eventRoll in 19..22) {
            // Yellow Card
            AudioEffectManager.playCardAlert()
            val cardedPlayer = starters.random()
            newEvents.add(
                0,
                MatchEvent(
                    minute = nextMin,
                    isHomeTeam = true,
                    eventType = MatchEventType.YELLOW_CARD,
                    title = "Yellow Card",
                    description = "Tactical foul committed by ${cardedPlayer.name} to stop the break.",
                    playerName = cardedPlayer.name
                )
            )
            newStats = newStats.copy(
                foulsHome = newStats.foulsHome + 1,
                yellowCardsHome = newStats.yellowCardsHome + 1
            )
        } else if (eventRoll == 23) {
            // VAR check event
            newEvents.add(
                0,
                MatchEvent(
                    minute = nextMin,
                    isHomeTeam = true,
                    eventType = MatchEventType.VAR_DECISION,
                    title = "VAR Check Complete",
                    description = "VAR review completed: Penalty decision confirmed by on-field referee."
                )
            )
        }

        // Calculate dynamic possession based on tactical style
        val posOffset = when (currentTactics.passingStyle) {
            PassingStyle.TIKI_TAKA -> +7
            PassingStyle.DIRECT -> -3
            PassingStyle.WING_PLAY -> +2
            PassingStyle.LONG_BALL -> -6
        }
        val possessionHome = ((50 + (homeOvr - awayOvr) + posOffset) / 2 + Random.nextInt(-4, 5)).toInt().coerceIn(35, 75)

        newStats = newStats.copy(
            possessionHome = possessionHome,
            possessionAway = 100 - possessionHome
        )

        // Animate entities with slight tactical jitter
        val updatedEntities = state.entities.map { entity ->
            val jitterX = (Random.nextFloat() - 0.5f) * 0.04f
            val jitterY = (Random.nextFloat() - 0.5f) * 0.04f
            entity.copy(
                x = (entity.x + jitterX).coerceIn(0.04f, 0.96f),
                y = (entity.y + jitterY).coerceIn(0.08f, 0.92f),
                hasBall = (Math.abs(entity.x - ballX) < 0.08f && Math.abs(entity.y - ballY) < 0.08f)
            )
        }

        return state.copy(
            currentMinute = nextMin,
            matchStatus = newStatus,
            homeScore = homeScore,
            awayScore = awayScore,
            stats = newStats,
            events = newEvents.take(35),
            ballPosition = Pair(ballX, ballY),
            entities = updatedEntities,
            lastGoalCelebrationText = goalCelebrationText
        )
    }

    private fun finishAndRecordMatch(finalState: LiveMatchState) {
        val userGoalEvents = finalState.events.filter { it.eventType == MatchEventType.GOAL && it.isHomeTeam }
        val oppGoalEvents = finalState.events.filter { it.eventType == MatchEventType.GOAL && !it.isHomeTeam }

        val userGoals = userGoalEvents.map { "${it.playerName ?: "Attacker"} (${it.minute}')" }
        val oppGoals = oppGoalEvents.map { "${it.playerName ?: "Opponent"} (${it.minute}')" }

        val result = when {
            finalState.homeScore > finalState.awayScore -> MatchResultType.WIN
            finalState.homeScore == finalState.awayScore -> MatchResultType.DRAW
            else -> MatchResultType.LOSS
        }

        val topScorer = userGoalEvents.firstOrNull()?.playerName ?: squad.value.filter { it.isStarter }.random().name
        val rating = when (result) {
            MatchResultType.WIN -> (8.2 + (Random.nextDouble(0.0, 1.2))).toFloat()
            MatchResultType.DRAW -> (7.2 + (Random.nextDouble(0.0, 0.8))).toFloat()
            MatchResultType.LOSS -> (6.1 + (Random.nextDouble(0.0, 0.9))).toFloat()
        }

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val record = HistoricalMatchRecord(
            matchId = finalState.matchId,
            competition = finalState.opponent.league,
            dateString = dateFormat.format(Date()),
            homeTeam = finalState.homeTeamName,
            awayTeam = finalState.opponent.name,
            homeScore = finalState.homeScore,
            awayScore = finalState.awayScore,
            userTeamIsHome = true,
            matchRating = (rating * 10).toInt() / 10f,
            topPerformer = "$topScorer (${String.format(Locale.US, "%.1f", rating)})",
            userGoalScorers = userGoals,
            opponentGoalScorers = oppGoals,
            xGHome = finalState.stats.xGHome,
            xGAway = finalState.stats.xGAway,
            possessionUser = finalState.stats.possessionHome,
            totalShotsUser = finalState.stats.shotsHome,
            managerXpGained = if (result == MatchResultType.WIN) 450 else if (result == MatchResultType.DRAW) 220 else 100,
            resultType = result
        )

        repository.recordMatchCompletion(record)
    }

    fun startOrContinueCareer() {
        _appPhase.value = AppPhase.MAIN_CAREER
        _currentTab.value = ActiveScreenTab.TACTICS_SQUAD
    }

    fun createNewCareer(
        managerName: String,
        clubName: String,
        region: String,
        budgetTier: String
    ) {
        val budgetAmount = when {
            budgetTier.contains("150") -> 150.0
            budgetTier.contains("30") -> 30.0
            else -> 75.0
        }
        val updatedProfile = userProfile.value.copy(
            managerName = managerName,
            clubName = clubName,
            region = region,
            transferBudgetMillions = budgetAmount,
            eloRating = 1350
        )
        repository.updateUserProfile(updatedProfile)
        _appPhase.value = AppPhase.MAIN_CAREER
        _currentTab.value = ActiveScreenTab.TACTICS_SQUAD
    }

    fun logout() {
        matchSimulationJob?.cancel()
        _liveMatch.value = null
        _appPhase.value = AppPhase.SPLASH_PROFILE_MENU
    }

    fun returnToHomeAfterMatch() {
        matchSimulationJob?.cancel()
        _liveMatch.value = null
        _currentTab.value = ActiveScreenTab.TACTICS_SQUAD
    }

    override fun onCleared() {
        super.onCleared()
        matchSimulationJob?.cancel()
    }
}
