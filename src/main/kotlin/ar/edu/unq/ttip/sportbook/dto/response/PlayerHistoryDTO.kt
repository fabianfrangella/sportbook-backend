package ar.edu.unq.ttip.sportbook.dto.response

import java.time.LocalDate

data class PlayerHistoryDTO(
    val playerName: String,
    val totalMatchesPlayed: Int,
    val totalWins: Int,
    val totalGoals: Int,
    val lastMatches: List<MatchSnapshotDTO>
)

data class MatchSnapshotDTO(
    val date: LocalDate,
    val won: Boolean,
    val goalsScored: Int,
    val wasMvp: Boolean
)