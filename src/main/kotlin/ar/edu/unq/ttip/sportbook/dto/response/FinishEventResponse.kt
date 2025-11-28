package ar.edu.unq.ttip.sportbook.dto.response

import ar.edu.unq.ttip.sportbook.persistence.entity.team.TeamColor
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import java.time.LocalDateTime

data class EventStatsResponse(
    val eventId: Long,
    val sport: Sport,
    val dateTime: LocalDateTime,
    val finished: Boolean,

    val totalRegisteredPlayers: Int,
    val presentPlayers: Int,
    val absentPlayers: Int,
    val attendanceRate: Double,

    val totalGoals: Int,
    val scores: List<TeamScoreDTO>,

    val winningTeam: TeamSummary?,
    val mvp: PlayerSummary?,
    val missingPlayers: List<PlayerSummary>,

    val goalsDetail: List<PlayerGoalsDTO>? = null,
    val sets: List<SetResultResponse>? = null,

    val insights: List<PlayerHistoryDTO>? = null,
)

data class TeamScoreDTO(
    val teamId: Long,
    val color: TeamColor,
    val name: String?,
    val goals: Int,
    val isWinner: Boolean
)

data class PlayerGoalsDTO(
    val player: PlayerSummary,
    val teamId: Long?,
    val goals: Int
)

data class PlayerSummary(
    val id: Long?,
    val name: String?,
    val teamId: Long?,
    val teamColor: TeamColor?
)

data class TeamSummary(
    val id: Long,
    val color: TeamColor,
    val name: String?
)

data class SetResultResponse(
    val team1Score: Int,
    val team2Score: Int
)