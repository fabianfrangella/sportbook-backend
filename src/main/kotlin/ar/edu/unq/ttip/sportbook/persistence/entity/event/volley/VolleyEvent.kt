package ar.edu.unq.ttip.sportbook.persistence.entity.event.volley

import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "VOLLEY_EVENT")
class VolleyEvent : Event() {
    init { this.sport = Sport.VOLLEY }
    @OneToMany(targetEntity = Team::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "team_volley",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "team_id")]
    )
    lateinit var teams: List<Team>

    override fun removePlayerFromTeams(player: Player) {
        teams.forEach { team -> team.players.remove(player) }
    }

    override fun createLineups(): List<Lineup> {
        return emptyList()
    }

    override fun updatePitchSize(size: Int?) {
        // TODO: averiguar de a cuantos jugadores se puede jugar al volley y acomodar un pitchSize para ello, si es que tiene sentido
    }

    override fun getTeam(teamId: Long): Team {
        return teams.find { it.id == teamId }
            ?: throw IllegalArgumentException("El equipo con id $teamId no pertenece a este evento")
    }

    override fun getFairnessScore(): Double {
        if (teams.isEmpty()) {
            return 0.0
        }

        // Calculamos el puntaje promedio de cada equipo
        val teamScores = teams.map { team ->
            team.players.map { it.calculateScore(sport) }.average()
        }

        // Para volley, calculamos la desviación estándar entre los puntajes de los equipos
        val mean = teamScores.average()
        val variance = teamScores.map { score ->
            (score - mean) * (score - mean)
        }.average()
        val standardDeviation = kotlin.math.sqrt(variance)

        // Una desviación de 0 significa equipos perfectamente parejos (10 puntos)
        // Una desviación de 3 o más significa equipos muy disparejos (0 puntos)
        return kotlin.math.max(10.0 - (standardDeviation * 3.33), 0.0)
    }

    override fun addTeam(team: Team) {
        teams = teams + team
    }

    override fun removeTeam(team: Team) {
        team.clear()
        teams = teams.filter { it != team }
    }

    override fun leave(user: SportUser): Player {
        val player = teams.flatMap { it.players }.find { it.user?.username == user.username }
        if (player == null) throw NotFoundException("Jugador no encontrado")

        this.removePlayerFromTeams(player)
        this.unnasignedPlayers.remove(player)
        player.event = null
        return player
    }

    override fun balanceTeams() {
        if (teams.isEmpty()) return

        val allPlayers = (teams.flatMap { it.players } + unnasignedPlayers).toMutableList()
        teams.forEach { it.players.clear() }
        unnasignedPlayers.clear()

        allPlayers.sortByDescending { it.calculateScore(sport) }

        var currentTeamIndex = 0
        var direction = 1

        allPlayers.forEach { player ->
            teams[currentTeamIndex].players.add(player)

            currentTeamIndex += direction
            if (currentTeamIndex >= teams.size - 1) {
                direction = -1
            } else if (currentTeamIndex <= 0) {
                direction = 1
            }
        }

    }

}