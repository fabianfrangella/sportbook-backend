package ar.edu.unq.ttip.sportbook.controller.request

data class TeamGoalRequest(
    val teamId: Long,
    val playerId: Long
)

data class FinishEventRequest(
    val goals: List<TeamGoalRequest>,
    val winningTeamId: Long,
    val mvpId: Long,
    val missingPlayerIds: Set<Long>
)
