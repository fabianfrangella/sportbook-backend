package ar.edu.unq.ttip.sportbook.persistence.entity.event.football

import ar.edu.unq.ttip.sportbook.exception.BadRequestException
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "FOOTBALL_EVENT")
class FootballEvent : Event() {
    init { this.sport = Sport.FOOTBALL }
    @ManyToOne(cascade = [CascadeType.ALL])
    var firstTeam: Team? = null
    @ManyToOne(cascade = [CascadeType.ALL])
    var secondTeam: Team? = null
    var pitchSize: Int = 0

    override fun removePlayerFromTeams(player: Player) {
        firstTeam?.players?.remove(player)
        secondTeam?.players?.remove(player)
    }

    override fun createLineups(): List<Lineup> {
        return listOf(firstTeam, secondTeam).map {
            createAndPopulateFootballLineup(it!!)
        }
    }

    private fun createAndPopulateFootballLineup(team: Team) : Lineup {
        val lineup = FootballLineup()
        lineup.event = this
        lineup.team = team
        val players: List<Player> = team.players
        val distinctCount = players.map { it.id }.toSet().size
        if (distinctCount != players.size) {
            throw BadRequestException("El equipo ${team.color} contiene jugadores duplicados")
        }

        players.forEach { player ->
            lineup.addPlayerToBench(player)
        }
        return lineup
    }

    override fun updatePitchSize(size: Int?) {
        size?.let { pitchSize = it }
    }

    override fun getTeam(teamId: Long): Team {
        if (firstTeam?.id == teamId) return firstTeam!!
        if (secondTeam?.id == teamId) return secondTeam!!
        throw BadRequestException("El equipo con id $teamId no pertenece a este evento")
    }

    override fun getFairnessScore(): Double {
        if (firstTeam == null || secondTeam == null || firstTeam?.players == null || secondTeam?.players == null) {
            return 0.0
        }

        val firstTeamScore = firstTeam!!.players.map { it.user.calculatePlayerScore(sport) }.average()
        val secondTeamScore = secondTeam!!.players.map { it.user.calculatePlayerScore(sport) }.average()

        // Calculamos qué tan parejos están los equipos (diferencia máxima de 10 puntos)
        val scoreDifference = kotlin.math.abs(firstTeamScore - secondTeamScore)
        return kotlin.math.max(10.0 - scoreDifference, 0.0)
    }

}