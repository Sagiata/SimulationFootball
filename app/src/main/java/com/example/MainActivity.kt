package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.*
import com.example.ui.analytics.AnalyticsScreen
import com.example.ui.calendar.CalendarScreen
import com.example.ui.club.ClubManagementScreen
import com.example.ui.components.AdaptiveBottomNavigationBar
import com.example.ui.components.GameTopBar
import com.example.ui.components.LandscapeNavigationRail
import com.example.ui.components.PlayerDetailModal
import com.example.ui.components.SettingsDialog
import com.example.ui.history.MatchHistoryScreen
import com.example.ui.hub.MainHubScreen
import com.example.ui.leaderboard.LeaderboardScreen
import com.example.ui.match.LiveMatchScreen
import com.example.ui.multiplayer.MultiplayerScreen
import com.example.ui.profile.ManagerProfileScreen
import com.example.ui.profile.NewCareerWizardScreen
import com.example.ui.profile.ProfileWelcomeScreen
import com.example.ui.scouting.ScoutingScreen
import com.example.ui.splash.SplashScreen
import com.example.ui.tactics.TacticsSquadScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StadiumDark
import com.example.ui.training.TrainingScreen
import com.example.utils.AppLanguage
import com.example.utils.LocalizationManager
import com.example.viewmodel.ActiveScreenTab
import com.example.viewmodel.AppPhase
import com.example.viewmodel.FmSimViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FmSimViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()
        setContent {
            MyApplicationTheme {
                FmSimApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}

@Composable
fun FmSimApp(
    viewModel: FmSimViewModel,
    modifier: Modifier = Modifier
) {
    val appPhase by viewModel.appPhase.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val squad by viewModel.squad.collectAsStateWithLifecycle()
    val tactics by viewModel.tactics.collectAsStateWithLifecycle()
    val scoutingPool by viewModel.scoutingPool.collectAsStateWithLifecycle()
    val selectedScoutRegion by viewModel.selectedScoutRegion.collectAsStateWithLifecycle()
    val matchHistory by viewModel.matchHistory.collectAsStateWithLifecycle()
    val leaderboards by viewModel.leaderboards.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val opponents by viewModel.opponents.collectAsStateWithLifecycle()
    val selectedOpponent by viewModel.selectedOpponent.collectAsStateWithLifecycle()
    val liveMatch by viewModel.liveMatch.collectAsStateWithLifecycle()
    val inspectedPlayer by viewModel.inspectedPlayer.collectAsStateWithLifecycle()
    val selectedForSwapPlayerId by viewModel.selectedForSwapPlayerId.collectAsStateWithLifecycle()
    val financialStatement by viewModel.financialStatement.collectAsStateWithLifecycle()
    val boardObjectives by viewModel.boardObjectives.collectAsStateWithLifecycle()
    val incomingBids by viewModel.incomingBids.collectAsStateWithLifecycle()
    val calendarFixtures by viewModel.calendarFixtures.collectAsStateWithLifecycle()
    val pressQuestions by viewModel.pressQuestions.collectAsStateWithLifecycle()
    val youthAcademy by viewModel.youthAcademy.collectAsStateWithLifecycle()
    val activeNegotiation by viewModel.activeNegotiation.collectAsStateWithLifecycle()
    val currentLanguage by LocalizationManager.currentLanguage.collectAsStateWithLifecycle()

    var showSettingsModal by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(StadiumDark),
        color = StadiumDark
    ) {
        when (appPhase) {
            AppPhase.SPLASH_LOADING -> {
                SplashScreen(
                    onLoadingComplete = {
                        viewModel.completeSplashLoading()
                    }
                )
            }

            AppPhase.PROFILE_MENU -> {
                ProfileWelcomeScreen(
                    currentProfile = userProfile,
                    allProfiles = listOf(userProfile),
                    currentLanguage = currentLanguage,
                    onContinueCareer = { viewModel.startOrContinueCareer() },
                    onNewCareer = { mName, cName, reg, bTier ->
                        viewModel.createNewCareer(mName, cName, reg, bTier)
                    },
                    onOpenNewCareerWizard = {
                        viewModel.openNewCareerWizard()
                    },
                    onOpenSettings = { showSettingsModal = true }
                )
            }

            AppPhase.NEW_CAREER_WIZARD -> {
                NewCareerWizardScreen(
                    onFinishWizard = { mName, mNat, mFlag, mArch, reg, leg, cName, cColor, bMils, stad, natTeam, natFlag, natOvr ->
                        viewModel.createNewCareerDetailed(
                            managerName = mName,
                            managerNationality = mNat,
                            managerFlag = mFlag,
                            managerArchetype = mArch,
                            region = reg,
                            league = leg,
                            clubName = cName,
                            clubBadgeColor = cColor,
                            transferBudget = bMils,
                            stadium = stad,
                            nationalTeam = natTeam,
                            nationalFlag = natFlag,
                            nationalRating = natOvr
                        )
                    },
                    onCancel = {
                        viewModel.cancelNewCareerWizard()
                    }
                )
            }

            AppPhase.MAIN_CAREER -> {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                ) {
                    val isCompactOrPortrait = maxWidth < 600.dp || maxWidth < maxHeight

                    if (isCompactOrPortrait) {
                        // Mobile Portrait / Compact Screen Layout: TopBar + Screen Body + Adaptive Bottom Nav
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Top Game Status Bar
                            GameTopBar(
                                userProfile = userProfile,
                                nextOpponent = selectedOpponent,
                                onQuickSimMatch = {
                                    val opp = selectedOpponent ?: opponents.firstOrNull()
                                    if (opp != null) {
                                        viewModel.selectTab(ActiveScreenTab.LIVE_MATCH)
                                        viewModel.startLiveMatch(opp)
                                    }
                                },
                                onOpenSettings = { showSettingsModal = true }
                            )

                            // Tab Screen Content
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                TabContentHost(
                                    currentTab = currentTab,
                                    viewModel = viewModel,
                                    squad = squad,
                                    tactics = tactics,
                                    selectedForSwapPlayerId = selectedForSwapPlayerId,
                                    liveMatch = liveMatch,
                                    opponents = opponents,
                                    selectedOpponent = selectedOpponent,
                                    youthAcademy = youthAcademy,
                                    financialStatement = financialStatement,
                                    currentLanguage = currentLanguage,
                                    scoutingPool = scoutingPool,
                                    incomingBids = incomingBids,
                                    selectedScoutRegion = selectedScoutRegion,
                                    userProfile = userProfile,
                                    activeNegotiation = activeNegotiation,
                                    boardObjectives = boardObjectives,
                                    pressQuestions = pressQuestions,
                                    calendarFixtures = calendarFixtures,
                                    leaderboards = leaderboards,
                                    matchHistory = matchHistory,
                                    onOpenSettings = { showSettingsModal = true }
                                )
                            }

                            // Bottom Navigation Bar for Single-Handed Ergonomic Navigation
                            AdaptiveBottomNavigationBar(
                                currentTab = currentTab,
                                currentLanguage = currentLanguage,
                                onTabSelected = { viewModel.selectTab(it) },
                                onOpenSettings = { showSettingsModal = true }
                            )
                        }
                    } else {
                        // Landscape / Tablet Wide Screen Layout: Left Nav Rail + Content Area
                        Row(modifier = Modifier.fillMaxSize()) {
                            // Left Thumb-Friendly Landscape Navigation Rail
                            LandscapeNavigationRail(
                                currentTab = currentTab,
                                currentLanguage = currentLanguage,
                                onTabSelected = { viewModel.selectTab(it) },
                                onOpenSettings = { showSettingsModal = true }
                            )

                            // Main Game View Area
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                // Top Game Status Bar
                                GameTopBar(
                                    userProfile = userProfile,
                                    nextOpponent = selectedOpponent,
                                    onQuickSimMatch = {
                                        val opp = selectedOpponent ?: opponents.firstOrNull()
                                        if (opp != null) {
                                            viewModel.selectTab(ActiveScreenTab.LIVE_MATCH)
                                            viewModel.startLiveMatch(opp)
                                        }
                                    },
                                    onOpenSettings = { showSettingsModal = true }
                                )

                                // Tab Screen Body
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    TabContentHost(
                                        currentTab = currentTab,
                                        viewModel = viewModel,
                                        squad = squad,
                                        tactics = tactics,
                                        selectedForSwapPlayerId = selectedForSwapPlayerId,
                                        liveMatch = liveMatch,
                                        opponents = opponents,
                                        selectedOpponent = selectedOpponent,
                                        youthAcademy = youthAcademy,
                                        financialStatement = financialStatement,
                                        currentLanguage = currentLanguage,
                                        scoutingPool = scoutingPool,
                                        incomingBids = incomingBids,
                                        selectedScoutRegion = selectedScoutRegion,
                                        userProfile = userProfile,
                                        activeNegotiation = activeNegotiation,
                                        boardObjectives = boardObjectives,
                                        pressQuestions = pressQuestions,
                                        calendarFixtures = calendarFixtures,
                                        leaderboards = leaderboards,
                                        matchHistory = matchHistory,
                                        onOpenSettings = { showSettingsModal = true }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Global Modals (Settings & Player Card)
        if (showSettingsModal) {
            SettingsDialog(
                userProfile = userProfile,
                currentLanguage = currentLanguage,
                onLanguageSelected = { LocalizationManager.setLanguage(it) },
                onLogout = {
                    showSettingsModal = false
                    viewModel.logout()
                },
                onDismiss = { showSettingsModal = false }
            )
        }

        inspectedPlayer?.let { player ->
            PlayerDetailModal(
                player = player,
                onDismiss = { viewModel.inspectPlayer(null) }
            )
        }
    }
}

@Composable
fun TabContentHost(
    currentTab: ActiveScreenTab,
    viewModel: FmSimViewModel,
    squad: List<Player>,
    tactics: TeamTactics,
    selectedForSwapPlayerId: String?,
    liveMatch: LiveMatchState?,
    opponents: List<OpponentClub>,
    selectedOpponent: OpponentClub?,
    youthAcademy: List<Player>,
    financialStatement: ClubFinancialStatement,
    currentLanguage: AppLanguage,
    scoutingPool: List<ScoutProspect>,
    incomingBids: List<IncomingTransferBid>,
    selectedScoutRegion: ScoutRegion,
    userProfile: UserProfile,
    activeNegotiation: ContractNegotiation?,
    boardObjectives: List<BoardObjective>,
    pressQuestions: List<PressConferenceQuestion>,
    calendarFixtures: List<CalendarFixture>,
    leaderboards: List<LeaderboardEntry>,
    matchHistory: List<HistoricalMatchRecord>,
    onOpenSettings: () -> Unit
) {
    when (currentTab) {
        ActiveScreenTab.MAIN_HUB -> {
            MainHubScreen(
                userProfile = userProfile,
                squad = squad,
                tactics = tactics,
                opponents = opponents,
                matchHistory = matchHistory,
                financialStatement = financialStatement,
                onNavigateTab = { viewModel.selectTab(it) },
                onStartMatch = { opp ->
                    viewModel.selectOpponent(opp)
                    viewModel.selectTab(ActiveScreenTab.LIVE_MATCH)
                    viewModel.startLiveMatch(opp)
                },
                onInspectPlayer = { viewModel.inspectPlayer(it) }
            )
        }
        ActiveScreenTab.TACTICS_SQUAD -> {
            TacticsSquadScreen(
                squad = squad,
                tactics = tactics,
                selectedPlayerIdForSwap = selectedForSwapPlayerId,
                onSelectForSwap = { viewModel.selectPlayerForSwap(it) },
                onAssignToPitchSlot = { pId, slotIdx -> viewModel.assignPlayerToPitchSlot(pId, slotIdx) },
                onInspectPlayer = { viewModel.inspectPlayer(it) },
                onFormationChange = { viewModel.updateFormation(it) },
                onMentalityChange = { viewModel.updateMentality(it) },
                onPassingStyleChange = { viewModel.updatePassingStyle(it) },
                onPressingChange = { viewModel.updatePressing(it) },
                onTempoChange = { viewModel.updateTempo(it) },
                onTacticalSlidersChange = { defLine, width -> viewModel.updateTacticalSliders(defLine, width) },
                onSetPieceChange = { cap, pen, fk, cor -> viewModel.setSetPieceTakers(cap, pen, fk, cor) }
            )
        }
        ActiveScreenTab.LIVE_MATCH -> {
            LiveMatchScreen(
                liveMatch = liveMatch,
                opponents = opponents,
                selectedOpponent = selectedOpponent,
                tactics = tactics,
                squad = squad,
                onSelectOpponent = { viewModel.selectOpponent(it) },
                onStartMatch = { viewModel.startLiveMatch(it) },
                onTogglePause = { viewModel.togglePauseMatch() },
                onSetSpeed = { viewModel.setSimSpeed(it) },
                onInstantSim = { viewModel.instantSimulateMatch() },
                onMentalityChange = { viewModel.updateMentality(it) },
                onTeamTalk = { viewModel.performHalftimeTeamTalk(it) },
                onMakeSubstitution = { outId, inId -> viewModel.makeMatchSubstitution(outId, inId) },
                onReturnToHome = { viewModel.returnToHomeAfterMatch() }
            )
        }
        ActiveScreenTab.TRAINING -> {
            TrainingScreen(
                squad = squad,
                youthAcademy = youthAcademy,
                financialStatement = financialStatement,
                currentLanguage = currentLanguage,
                onUpdateFocus = { pId, focus -> viewModel.updatePlayerTrainingFocus(pId, focus) },
                onExecuteTraining = { viewModel.executeTrainingSession() },
                onPromoteYouth = { viewModel.promoteYouthWonderkid(it) },
                onInspectPlayer = { viewModel.inspectPlayer(it) }
            )
        }
        ActiveScreenTab.TRANSFERS -> {
            ScoutingScreen(
                scoutingPool = scoutingPool,
                incomingBids = incomingBids,
                selectedRegion = selectedScoutRegion,
                userProfile = userProfile,
                activeNegotiation = activeNegotiation,
                onSelectRegion = { viewModel.selectScoutRegion(it) },
                onRefreshRegion = { viewModel.refreshScoutRegion(it) },
                onAdvanceScout = { viewModel.advanceScout(it) },
                onStartNegotiation = { viewModel.startNegotiation(it) },
                onUpdateNegotiation = { wage, bonus, rc, years, role ->
                    viewModel.updateNegotiationValues(wage, bonus, rc, years, role)
                },
                onSubmitNegotiationOffer = { viewModel.submitContractProposal() },
                onFinalizeSigning = { viewModel.finalizeActiveNegotiation() },
                onDismissNegotiation = { viewModel.dismissNegotiation() },
                onRespondToBid = { bidId, accepted -> viewModel.respondToIncomingBid(bidId, accepted) }
            )
        }
        ActiveScreenTab.CLUB_MANAGEMENT -> {
            ClubManagementScreen(
                userProfile = userProfile,
                financialStatement = financialStatement,
                boardObjectives = boardObjectives,
                pressQuestions = pressQuestions,
                onUpgradeFacility = { viewModel.upgradeFacility(it) },
                onAnswerPressQuestion = { qId, opt -> viewModel.answerPressQuestion(qId, opt) }
            )
        }
        ActiveScreenTab.CALENDAR -> {
            CalendarScreen(
                calendarFixtures = calendarFixtures,
                currentLanguage = currentLanguage,
                onAdvanceDay = { viewModel.advanceCalendarDay() },
                onStartMatchFromCalendar = { fixture ->
                    val opp = opponents.find { it.name == fixture.awayTeam || it.name == fixture.homeTeam }
                        ?: opponents.firstOrNull()
                    if (opp != null) {
                        viewModel.selectTab(ActiveScreenTab.LIVE_MATCH)
                        viewModel.startLiveMatch(opp)
                    }
                }
            )
        }
        ActiveScreenTab.LEAGUE_TABLE -> {
            LeaderboardScreen(
                leaderboards = leaderboards,
                currentLanguage = currentLanguage
            )
        }
        ActiveScreenTab.ANALYTICS -> {
            AnalyticsScreen(
                squad = squad,
                onInspectPlayer = { viewModel.inspectPlayer(it) }
            )
        }
        ActiveScreenTab.MATCH_HISTORY -> {
            MatchHistoryScreen(
                matchHistory = matchHistory
            )
        }
        ActiveScreenTab.MANAGER_PROFILE -> {
            ManagerProfileScreen(
                userProfile = userProfile,
                onOpenSettings = onOpenSettings,
                onLogout = { viewModel.logout() }
            )
        }
        ActiveScreenTab.MULTIPLAYER_LOBBY -> {
            MultiplayerScreen(
                userProfile = userProfile,
                onChallengeManager = { room ->
                    val opp = OpponentClub(
                        id = room.id,
                        name = room.roomName,
                        shortName = room.hostManager.take(3).uppercase(),
                        badgeColorHex = 0xFF1976D2,
                        secondaryBadgeColorHex = 0xFF0D47A1,
                        league = room.region,
                        overallRating = (room.eloRating / 22).coerceIn(75, 92),
                        attackRating = 85,
                        midfieldRating = 84,
                        defenseRating = 83,
                        managerName = room.hostManager,
                        formation = FormationType.F_433
                    )
                    viewModel.selectTab(ActiveScreenTab.LIVE_MATCH)
                    viewModel.startLiveMatch(opp)
                }
            )
        }
    }
}
