package ar.edu.unq.ttip.sportbook.persistence.entity.user

import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyEvent
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn

@Entity
@Table(name = "PLAYER")
class Player() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    final var name: String? = null
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    var user: SportUser? = null

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true)
    @JsonIgnore
    var event: Event? = null

    @Transient
    var sportUsername: String? = null

    constructor(name: String, user: SportUser) : this() {
        this.name = name
        this.user = user
    }

    fun calculateScore(sport: Sport): Double {
        return user?.calculatePlayerScore(sport) ?: 0.0
    }

    fun joinTeam(event: Event, team: Team) {
        if (event.unnasignedPlayers.none { it.id == this.id }) {
            throw BusinessException("No estás registrado en el evento")
        }
        when (event) {
            is FootballEvent -> joinFootballTeam(event, team)
            is PaddleEvent -> joinPaddleEvent(event, team)
            is VolleyEvent -> joinVolleyEvent(event, team)
            else -> throw BusinessException("El evento no es de futbol")
        }
    }

    private fun joinVolleyEvent(event: VolleyEvent, team: Team) {
        joinTeamForMultipleTeamsEvent(team, event.teams, event.maxPlayers)
        removeFromTeams(event.teams, team)
    }

    private fun joinTeamForMultipleTeamsEvent(
        team: Team,
        teams: List<Team>,
        maxPlayers: Int
    ) {
        if (team.players.size >= maxPlayers / teams.size) {
            throw BusinessException("El equipo ya tiene la cantidad maxima de jugadores")
        }
        if (team.players.any { it.id == this.id }) {
            throw BusinessException("Ya eres parte del equipo!")
        }
        team.players.add(this)
    }

    private fun removeFromTeams(teams: List<Team>, team: Team) {
        teams.forEach { otherTeam ->
            if (otherTeam != team) {
                otherTeam.players.removeIf { it == this }
            }
        }
    }

    private fun joinPaddleEvent(event: PaddleEvent, team: Team) {
        joinTeamForMultipleTeamsEvent(team, event.teams, event.maxPlayers)
        removeFromTeams(event.teams, team)
    }

    private fun joinFootballTeam(event: FootballEvent, team: Team) {
        val otherTeam = getOtherFootballTeam(event, team)
        validateTeam(team, event)
        team.players.add(this)
        if (otherTeam!!.players.any { it == this }) {
            otherTeam.players.removeIf { it == this }
        }
    }

    private fun getOtherFootballTeam(event: FootballEvent, team: Team) =
        when (team) {
            event.firstTeam -> event.secondTeam
            event.secondTeam -> event.firstTeam
            else -> throw BusinessException("El equipo no pertenece a este evento")
        }

    private fun validateTeam(
        team: Team?,
        event: FootballEvent
    ) {
        if (team!!.players.size >= event.maxPlayers / 2) {
            throw BusinessException("El equipo ya tiene la cantidad maxima de jugadores")
        }
        if (team.players.any { it == this }) {
            throw BusinessException("Ya eres parte del equipo!")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Player) return false

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }


}
