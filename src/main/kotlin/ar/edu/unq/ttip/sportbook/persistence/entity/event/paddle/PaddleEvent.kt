package ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle

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
@Table(name = "PADDLE_EVENT")
class PaddleEvent() : Event() {
    init { this.sport = Sport.PADDLE }
    @OneToMany(targetEntity = Team::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "team_paddle",
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
        // TODO: averiguar de a cuantos jugadores se puede jugar al paddle y acomodar un pitchSize para ello, si es que tiene sentido
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

        // Encontramos la diferencia máxima entre cualquier par de equipos
        var maxDifference = 0.0
        for (i in teamScores.indices) {
            for (j in i + 1 until teamScores.size) {
                val difference = kotlin.math.abs(teamScores[i] - teamScores[j])
                if (difference > maxDifference) {
                    maxDifference = difference
                }
            }
        }

        return kotlin.math.max(10.0 - maxDifference, 0.0)
    }

    override fun addTeam(team: Team) {
        teams = teams + team
    }

    override fun removeTeam(team: Team) {
        team.clear()
        teams = teams.filter { it != team }
    }

    override fun leave(user: SportUser): Player {
        val allPlayersInTeams = teams
            .flatMap { it.players }

        val player = (allPlayersInTeams + this.unnasignedPlayers)
            .find { it.user?.username == user.username }
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

        val playersPerTeam = allPlayers.size / teams.size

        allPlayers.forEachIndexed { index, player ->
            val teamIndex = if (index < teams.size) {
                index
            } else {
                teams.size - 1 - (index % teams.size)
            }

            if (teams[teamIndex].players.size < playersPerTeam) {
                teams[teamIndex].players.add(player)
            }
        }

    }

}