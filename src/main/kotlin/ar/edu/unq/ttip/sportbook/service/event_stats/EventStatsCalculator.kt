package ar.edu.unq.ttip.sportbook.service.event_stats

import ar.edu.unq.ttip.sportbook.controller.response.*
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.team.TeamColor
import ar.edu.unq.ttip.sportbook.persistence.entity.team.TeamGoal
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import org.springframework.stereotype.Service
import kotlin.math.max

data class CalculatedStats(
    val registered: Int,
    val present: Int,
    val absent: Int,
    val attendanceRate: Double,
    val totalGoals: Int,
    val scores: List<TeamScoreDTO>,
    val scorersRanking: List<PlayerGoalsDTO>,
    val winningTeam: TeamSummary?,
    val mvp: PlayerSummary?,
    val missingPlayers: List<PlayerSummary>
)

@Service
class EventStatsCalculator {

    fun compute(event: Event, stats: FinishedEventStats): CalculatedStats {
        // --- asistencia ---
        val registered = event.unnasignedPlayers?.size ?: 0
        val missingPlayers = stats.missingPlayers.toList()
        val absent = missingPlayers.size
        val present = max(registered - absent, 0)
        val attendanceRate = if (registered > 0) present.toDouble() / registered else 0.0

        // --- goles & estructuras auxiliares (una sola pasada) ---
        val goals: List<TeamGoal> = stats.goals
        val totalGoals = goals.size

        // Mapa teamId -> color (preferimos color del team en goles y/o del ganador)
        val teamColorById: MutableMap<Long, TeamColor?> = goals.asSequence()
            .mapNotNull { it.team?.let { t -> t.id to t.color } }
            .toMap(mutableMapOf())

        stats.winningTeam?.let { teamColorById[it.id] = it.color }

        // Conteo por equipo
        val goalsByTeam: Map<Long, Int> = goals.asSequence()
            .mapNotNull { it.team?.id }
            .groupingBy { it }
            .eachCount()

        // Conteo por jugador
        val goalsByPlayer: Map<Long, Int> = goals.asSequence()
            .mapNotNull { it.player?.id }
            .groupingBy { it }
            .eachCount()

        // Jugador -> team (tomamos el primero visto)
        val teamIdByPlayerId: Map<Long, Long?> = goals.asSequence()
            .filter { it.player?.id != null }
            .groupBy { it.player!!.id }
            .mapValues { (_, list) -> list.firstOrNull { it.team?.id != null }?.team?.id }

        val winningId = stats.winningTeam?.id

        // Scores ordenados
        val scores = goalsByTeam.map { (teamId, count) ->
            TeamScoreDTO(
                teamId = teamId,
                color = teamColorById[teamId],
                goals = count,
                isWinner = (teamId == winningId)
            )
        }.sortedByDescending { it.goals }

        // Ranking goleadores ordenado
        val playerNameById: Map<Long, String?> = goals.asSequence()
            .mapNotNull { it.player?.let { p -> p.id to p.name } }
            .toMap()

        val scorersRanking = goalsByPlayer.entries
            .sortedByDescending { it.value }
            .map { (playerId, count) ->
                PlayerGoalsDTO(
                    player = PlayerSummary(id = playerId, name = playerNameById[playerId]),
                    teamId = teamIdByPlayerId[playerId],
                    goals = count
                )
            }

        // Ganador / MVP / Ausentes como summaries
        val winningTeamSummary = stats.winningTeam?.let { TeamSummary(id = it.id, color = it.color) }
        val mvpSummary = stats.mvp?.let { PlayerSummary(id = it.id, name = it.name) }
        val missingSummaries = missingPlayers
            .map { it.toSummary() }
            .sortedBy { it.name ?: "" }

        return CalculatedStats(
            registered = registered,
            present = present,
            absent = absent,
            attendanceRate = attendanceRate,
            totalGoals = totalGoals,
            scores = scores,
            scorersRanking = scorersRanking,
            winningTeam = winningTeamSummary,
            mvp = mvpSummary,
            missingPlayers = missingSummaries
        )
    }

    private fun Player.toSummary() = PlayerSummary(id = this.id, name = this.name)
}
