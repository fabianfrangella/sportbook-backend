package ar.edu.unq.ttip.sportbook.dto.request

data class SetResultRequest(
    val team1Score: Int,
    val team2Score: Int
)

data class FinishEventRequest(
    val winningTeamId: Long?,
    val goals: List<TeamGoalRequest>,
    val missingPlayerIds: List<Long>,
    val mvpId: Long?,
    val sets: List<SetResultRequest>? = null
)

data class TeamGoalRequest(
    val teamId: Long,
    val playerId: Long
)