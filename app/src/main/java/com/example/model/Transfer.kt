package com.example.model

enum class NegotiationStatus {
    IN_PROGRESS,
    ACCEPTED_BY_AGENT,
    REJECTED_BY_AGENT,
    COUNTER_OFFER,
    FINALIZED
}

data class ContractNegotiation(
    val prospectId: String,
    val playerName: String,
    val playerRole: PlayerRole,
    val currentClub: String,
    val marketValueMillions: Double,
    val initialWageDemandThousands: Int,
    val offeredWageThousands: Int,
    val offeredSigningBonusMillions: Double,
    val offeredReleaseClauseMillions: Double,
    val offeredContractYears: Int,
    val offeredSquadRole: String, // "Key Player", "First Team Regular", "Rotation", "Youth Prospect"
    val agentMood: String = "Neutral", // "Eager", "Neutral", "Hesitant", "Frustrated"
    val agentFeedbackMessage: String = "The player is listening to our proposals.",
    val status: NegotiationStatus = NegotiationStatus.IN_PROGRESS
)

data class IncomingTransferBid(
    val id: String,
    val playerId: String,
    val playerName: String,
    val playerRole: PlayerRole,
    val playerRating: Int,
    val offeringClubName: String,
    val offerAmountMillions: Double,
    val playerMarketValueMillions: Double,
    val dateString: String,
    val isPending: Boolean = true
)
