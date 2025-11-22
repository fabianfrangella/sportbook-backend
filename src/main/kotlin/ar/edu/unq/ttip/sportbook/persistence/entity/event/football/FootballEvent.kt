package ar.edu.unq.ttip.sportbook.persistence.entity.event.football

import ar.edu.unq.ttip.sportbook.exception.BadRequestException
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "FOOTBALL_EVENT")
class FootballEvent : Event() {
    init { this.sport = Sport.FOOTBALL }

    var pitchSize: Int = 0

    override fun createLineups(): List<Lineup> {
        return teams.map { team ->
            createAndPopulateFootballLineup(team)
        }
    }

    private fun createAndPopulateFootballLineup(team: Team): Lineup {
        val lineup = FootballLineup()
        lineup.event = this
        lineup.team = team


        val distinctCount = team.players.map { it.id }.toSet().size
        if (distinctCount != team.players.size) {
            throw BadRequestException("Error de integridad: El equipo ${team.name} contiene jugadores duplicados")
        }


        team.players.forEach { player ->
            lineup.addPlayerToBench(player)
        }

        return lineup
    }

    override fun updatePitchSize(size: Int) {
        size.let { pitchSize = it }
    }

}