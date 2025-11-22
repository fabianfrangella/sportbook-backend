package ar.edu.unq.ttip.sportbook.service.event_stats

import ar.edu.unq.ttip.sportbook.dto.response.*
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
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
    val missingPlayers: List<PlayerSummary>,
    val sets: List<SetResultResponse>?
)

@Service
class EventStatsCalculator {

    fun compute(event: Event, stats: FinishedEventStats): CalculatedStats {

        val allPlayers = event.unnasignedPlayers + event.teams.flatMap { it.players }
        val registered = allPlayers.size

        val missingPlayers = stats.missingPlayers.toList()
        val absent = missingPlayers.size
        val present = max(registered - absent, 0)
        val attendanceRate = if (registered > 0) present.toDouble() / registered else 0.0

        val goals = stats.goals

        val teamColorById = event.teams.associate { it.id to it.color }

        val teamIdByPlayerId = event.teams.flatMap { team ->
            team.players.map { it.id to team.id }
        }.toMap()
        val playerNameById = allPlayers.associate { it.id to it.name }

        fun playerSummary(p: Player): PlayerSummary {
            val teamId = teamIdByPlayerId[p.id]
            return PlayerSummary(
                id = p.id,
                name = p.name,
                teamId = teamId,
                teamColor = teamId?.let { teamColorById[it] }
            )
        }

        val scorersRanking = goals.groupBy { it.player?.id }
            .filterKeys { it != null }
            .map { (playerId, list) ->
                val pid = playerId!!
                val teamId = teamIdByPlayerId[pid]
                PlayerGoalsDTO(
                    player = PlayerSummary(pid, playerNameById[pid], teamId, teamId?.let { teamColorById[it] }),
                    teamId = teamId,
                    goals = list.size
                )
            }
            .sortedByDescending { it.goals }

        val scores: List<TeamScoreDTO>
        var totalGoalsOrSets = 0
        var setsResult: List<SetResultResponse>? = null

        if (event.sport == Sport.FOOTBALL) {

            totalGoalsOrSets = goals.size
            val goalsByTeamId = goals.groupBy { it.team?.id }

            scores = event.teams.map { team ->
                val teamGoalsCount = goalsByTeamId[team.id]?.size ?: 0
                TeamScoreDTO(
                    teamId = team.id,
                    color = team.color,
                    name = team.name,
                    goals = teamGoalsCount,
                    isWinner = stats.winningTeam?.id == team.id
                )
            }.sortedByDescending { it.goals }

        } else {

            val dbSets = stats.sets

            setsResult = dbSets.map {
                SetResultResponse(team1Score = it.team1Score, team2Score = it.team2Score)
            }

            totalGoalsOrSets = dbSets.size

            var team1SetsWon = 0
            var team2SetsWon = 0

            dbSets.forEach { set ->
                if (set.team1Score > set.team2Score) team1SetsWon++
                else if (set.team2Score > set.team1Score) team2SetsWon++
            }

            val team1 = event.teams.getOrNull(0)
            val team2 = event.teams.getOrNull(1)
            val scoresList = mutableListOf<TeamScoreDTO>()

            if (team1 != null) {
                scoresList.add(TeamScoreDTO(
                    teamId = team1.id,
                    color = team1.color,
                    name = team1.name,
                    goals = team1SetsWon,
                    isWinner = stats.winningTeam?.id == team1.id
                ))
            }
            if (team2 != null) {
                scoresList.add(TeamScoreDTO(
                    teamId = team2.id,
                    color = team2.color,
                    name = team2.name,
                    goals = team2SetsWon,
                    isWinner = stats.winningTeam?.id == team2.id
                ))
            }
            scores = scoresList.sortedByDescending { it.goals }
        }


        val winningTeamSummary = stats.winningTeam?.let {
            TeamSummary(it.id, it.color, it.name)
        }

        val mvpSummary = stats.mvp?.let { playerSummary(it) }
        val missingSummaries = missingPlayers.map { playerSummary(it) }.sortedBy { it.name ?: "" }

        return CalculatedStats(
            registered = registered,
            present = present,
            absent = absent,
            attendanceRate = attendanceRate,
            totalGoals = totalGoalsOrSets,
            scores = scores,
            scorersRanking = scorersRanking,
            winningTeam = winningTeamSummary,
            mvp = mvpSummary,
            missingPlayers = missingSummaries,
            sets = setsResult
        )
    }
}