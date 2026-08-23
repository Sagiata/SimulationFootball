package com.example.model

enum class FormationType(val label: String, val layoutSlots: List<FormationSlot>) {
    F_433(
        label = "4-3-3 Attack",
        layoutSlots = listOf(
            FormationSlot(0, PlayerRole.GK, 0.08f, 0.50f),
            FormationSlot(1, PlayerRole.LB, 0.25f, 0.15f),
            FormationSlot(2, PlayerRole.CB, 0.23f, 0.38f),
            FormationSlot(3, PlayerRole.CB, 0.23f, 0.62f),
            FormationSlot(4, PlayerRole.RB, 0.25f, 0.85f),
            FormationSlot(5, PlayerRole.CDM, 0.44f, 0.50f),
            FormationSlot(6, PlayerRole.CM, 0.58f, 0.30f),
            FormationSlot(7, PlayerRole.CAM, 0.62f, 0.70f),
            FormationSlot(8, PlayerRole.LW, 0.78f, 0.16f),
            FormationSlot(9, PlayerRole.ST, 0.86f, 0.50f),
            FormationSlot(10, PlayerRole.RW, 0.78f, 0.84f)
        )
    ),
    F_4231(
        label = "4-2-3-1 Wide",
        layoutSlots = listOf(
            FormationSlot(0, PlayerRole.GK, 0.08f, 0.50f),
            FormationSlot(1, PlayerRole.LB, 0.25f, 0.15f),
            FormationSlot(2, PlayerRole.CB, 0.22f, 0.38f),
            FormationSlot(3, PlayerRole.CB, 0.22f, 0.62f),
            FormationSlot(4, PlayerRole.RB, 0.25f, 0.85f),
            FormationSlot(5, PlayerRole.CDM, 0.42f, 0.36f),
            FormationSlot(6, PlayerRole.CDM, 0.42f, 0.64f),
            FormationSlot(7, PlayerRole.LM, 0.64f, 0.18f),
            FormationSlot(8, PlayerRole.CAM, 0.65f, 0.50f),
            FormationSlot(9, PlayerRole.RM, 0.64f, 0.82f),
            FormationSlot(10, PlayerRole.ST, 0.86f, 0.50f)
        )
    ),
    F_352(
        label = "3-5-2 Fluid",
        layoutSlots = listOf(
            FormationSlot(0, PlayerRole.GK, 0.08f, 0.50f),
            FormationSlot(1, PlayerRole.CB, 0.22f, 0.26f),
            FormationSlot(2, PlayerRole.CB, 0.20f, 0.50f),
            FormationSlot(3, PlayerRole.CB, 0.22f, 0.74f),
            FormationSlot(4, PlayerRole.LWB, 0.48f, 0.12f),
            FormationSlot(5, PlayerRole.CM, 0.48f, 0.38f),
            FormationSlot(6, PlayerRole.CAM, 0.58f, 0.50f),
            FormationSlot(7, PlayerRole.CM, 0.48f, 0.62f),
            FormationSlot(8, PlayerRole.RWB, 0.48f, 0.88f),
            FormationSlot(9, PlayerRole.ST, 0.84f, 0.36f),
            FormationSlot(10, PlayerRole.ST, 0.84f, 0.64f)
        )
    ),
    F_442(
        label = "4-4-2 Classic",
        layoutSlots = listOf(
            FormationSlot(0, PlayerRole.GK, 0.08f, 0.50f),
            FormationSlot(1, PlayerRole.LB, 0.24f, 0.15f),
            FormationSlot(2, PlayerRole.CB, 0.22f, 0.38f),
            FormationSlot(3, PlayerRole.CB, 0.22f, 0.62f),
            FormationSlot(4, PlayerRole.RB, 0.24f, 0.85f),
            FormationSlot(5, PlayerRole.LM, 0.52f, 0.15f),
            FormationSlot(6, PlayerRole.CM, 0.50f, 0.38f),
            FormationSlot(7, PlayerRole.CM, 0.50f, 0.62f),
            FormationSlot(8, PlayerRole.RM, 0.52f, 0.85f),
            FormationSlot(9, PlayerRole.ST, 0.84f, 0.38f),
            FormationSlot(10, PlayerRole.ST, 0.84f, 0.62f)
        )
    ),
    F_532(
        label = "5-3-2 Counter",
        layoutSlots = listOf(
            FormationSlot(0, PlayerRole.GK, 0.08f, 0.50f),
            FormationSlot(1, PlayerRole.LWB, 0.28f, 0.12f),
            FormationSlot(2, PlayerRole.CB, 0.20f, 0.30f),
            FormationSlot(3, PlayerRole.CB, 0.18f, 0.50f),
            FormationSlot(4, PlayerRole.CB, 0.20f, 0.70f),
            FormationSlot(5, PlayerRole.RWB, 0.28f, 0.88f),
            FormationSlot(6, PlayerRole.CM, 0.50f, 0.30f),
            FormationSlot(7, PlayerRole.CDM, 0.44f, 0.50f),
            FormationSlot(8, PlayerRole.CM, 0.50f, 0.70f),
            FormationSlot(9, PlayerRole.CF, 0.82f, 0.38f),
            FormationSlot(10, PlayerRole.ST, 0.84f, 0.62f)
        )
    )
}

data class FormationSlot(
    val slotIndex: Int, // 0..10
    val targetRole: PlayerRole,
    val relativeX: Float, // 0.0 to 1.0 (from our goal to opponent goal across pitch)
    val relativeY: Float  // 0.0 to 1.0 (from left flank to right flank)
)

enum class TeamMentality(val label: String, val attackBoost: Float, val defBoost: Float) {
    VERY_DEFENSIVE("Very Defensive (Park the Bus)", -0.4f, +0.5f),
    DEFENSIVE("Defensive (Counter Attack)", -0.2f, +0.3f),
    BALANCED("Balanced Control", 0.0f, 0.0f),
    ATTACKING("High Offensive Pressure", +0.3f, -0.1f),
    ALL_OUT_ATTACK("All-Out Blitz (Overload)", +0.6f, -0.4f)
}

enum class PassingStyle(val label: String) {
    TIKI_TAKA("Tiki-Taka (Short & Fast)"),
    DIRECT("Direct (Vertical Transitions)"),
    WING_PLAY("Wing Play (Wide Crosses)"),
    LONG_BALL("Long Ball (Target Man)")
}

enum class PressingIntensity(val label: String) {
    LOW_BLOCK("Low Block (Regroup)"),
    MID_BLOCK("Mid Block (Zone Defense)"),
    GEGENPRESSING("Gegenpressing (High Intensity)")
}

enum class MatchTempo(val label: String) {
    PATIENT("Patient & Calculated"),
    STANDARD("Standard Dynamic"),
    UP_TEMPO("Up-Tempo High Voltage")
}

data class TeamTactics(
    val formation: FormationType = FormationType.F_433,
    val mentality: TeamMentality = TeamMentality.ATTACKING,
    val passingStyle: PassingStyle = PassingStyle.TIKI_TAKA,
    val pressingIntensity: PressingIntensity = PressingIntensity.GEGENPRESSING,
    val matchTempo: MatchTempo = MatchTempo.STANDARD,
    val defensiveLineDepth: Int = 70, // 0 to 100
    val width: Int = 65 // 0 to 100
)
