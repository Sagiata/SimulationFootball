package com.example.model

enum class LeagueTier(val title: String, val regionBadge: String, val minElo: Int) {
    GLOBAL_SUPER_LEAGUE("Apex Champions Division", "🌍 Global", 2400),
    EUROPE_PREMIER("Euro Masterclass League", "🇪🇺 Europe", 2100),
    AMERICAS_ELITE("Americas Continental Cup", "🌎 Americas", 1800),
    ASIA_AFRICA_OPEN("Asia-Pacific & Africa Pro", "🌏 Asia-Afro", 1500),
    ACADEMY_TIER("Challenger Development", "🔰 Division 4", 1000)
}

data class LeaderboardEntry(
    val rank: Int,
    val managerId: String,
    val managerName: String,
    val clubName: String,
    val region: String,
    val eloRating: Int,
    val matchesPlayed: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int,
    val winStreak: Int,
    val isCurrentUser: Boolean = false,
    val verifiedBadge: Boolean = true
)

data class UserProfile(
    val managerId: String,
    val encryptedAuthToken: String,
    val securityHash: String,
    val managerName: String,
    val clubName: String,
    val clubBadgeColor: Long,
    val region: String,
    val reputationStars: Float,
    val careerWins: Int,
    val careerDraws: Int,
    val careerLosses: Int,
    val totalTrophies: Int,
    val eloRating: Int,
    val transferBudgetMillions: Double,
    val weeklyWageBudgetThousands: Int,
    val currentWeeklyWageExpenseThousands: Int,
    val clubPhilosophy: String
)
