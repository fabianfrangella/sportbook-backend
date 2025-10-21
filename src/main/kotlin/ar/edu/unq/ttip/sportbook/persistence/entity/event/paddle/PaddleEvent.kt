package ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
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

    override fun balanceTeams() {
        if (teams.isEmpty()) return

        val allPlayers = teams.flatMap { it.players }.toMutableList()
        teams.forEach { it.players.clear() }

        // En paddle, generalmente hay equipos de 2 jugadores
        // Ordenamos los jugadores por puntaje de mayor a menor
        allPlayers.sortByDescending { it.calculateScore(sport) }

        // Para paddle, aseguramos que los equipos tengan la misma cantidad de jugadores
        val playersPerTeam = allPlayers.size / teams.size

        // Distribuimos los jugadores asegurando que cada equipo tenga un jugador fuerte y uno más débil
        allPlayers.forEachIndexed { index, player ->
            val teamIndex = if (index < teams.size) {
                // Los mejores jugadores van uno a cada equipo
                index
            } else {
                // Los jugadores restantes se distribuyen empezando por el último equipo
                teams.size - 1 - (index % teams.size)
            }

            if (teams[teamIndex].players.size < playersPerTeam) {
                teams[teamIndex].players.add(player)
            }
        }
    }

    override fun addTeam(team: Team) {
        teams = teams + team
    }

    override fun removeTeam(team: Team) {
        team.clear()
        teams = teams.filter { it != team }
    }

}