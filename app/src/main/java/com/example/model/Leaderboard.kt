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
    val managerId: String = "MGR-7749-AUTH",
    val encryptedAuthToken: String = "SHA256:9f8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c",
    val securityHash: String = "AES-GCM:e5b6c7d8a9f0e1d2c3b4a5f6e7d8",
    val managerName: String = "Coach Alex Sterling",
    val clubName: String = "Apex Tacticians FC",
    val clubBadgeColor: Long = 0xFF00E5FF,
    val region: String = "Global Elite",
    val reputationStars: Float = 4.5f,
    val careerWins: Int = 42,
    val careerDraws: Int = 14,
    val careerLosses: Int = 9,
    val totalTrophies: Int = 5,
    val eloRating: Int = 2185,
    val transferBudgetMillions: Double = 85.5,
    val weeklyWageBudgetThousands: Int = 1250,
    val currentWeeklyWageExpenseThousands: Int = 890,
    val clubPhilosophy: String = "High-Intensity Positional Dominance & Fluid Counter-Press",
    
    // Extended League, Club & National Team Career Data
    val leagueName: String = "Premier League",
    val leagueCountry: String = "🏴󠁧󠁢󠁥󠁮󠁧󠁿 England",
    val nationalTeam: String = "Indonesia",
    val nationalTeamFlag: String = "🇮🇩",
    val nationalTeamRating: Int = 78,
    val worldCupGroup: String = "Group C",
    val worldCupPoints: Int = 6,
    val managerNationality: String = "Indonesia",
    val managerFlag: String = "🇮🇩",
    val managerArchetype: String = "Tactical Mastermind",
    val clubStadium: String = "Stadium Garuda Utama",
    val boardConfidencePercent: Int = 94,
    val currentSeasonYear: String = "2025/2026",
    val avatarHairStyle: Int = 1,
    val avatarSkinTone: Int = 1
)
