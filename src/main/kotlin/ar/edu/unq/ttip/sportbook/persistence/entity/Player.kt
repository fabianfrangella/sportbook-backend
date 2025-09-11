package ar.edu.unq.ttip.sportbook.persistence.entity

import BusinessException
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "PLAYER")
class Player() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    lateinit var name: String
    @OneToOne(cascade = [CascadeType.ALL])
    lateinit var user: SportUser

    constructor(name: String, user: SportUser) : this() {
        this.name = name
        this.user = user
    }

    fun joinTeam(event: Event, teamId: Long) {
        if (event.players!!.none { it.id == this.id }) {
            throw BusinessException("No estás registrado en el evento")
        }
        when (event) {
            is FootballEvent -> joinFootballTeam(event, teamId)
            is PaddleEvent -> joinPaddleEvent(event, teamId)
            is VolleyEvent -> joinVolleyEvent(event, teamId)
            else -> throw BusinessException("El evento no es de futbol")
        }
    }

    private fun joinVolleyEvent(event: VolleyEvent, teamId: Long) {
        val team = event.teams.find { it.id == teamId } ?: throw BusinessException("El equipo no pertenece a este evento")
        joinTeamForMultipleTeamsEvent(team, event.teams, event.maxPlayers)
        removeFromTeams(event.teams, teamId)
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

    private fun removeFromTeams(teams: List<Team>, teamId: Long) {
        teams.forEach {
            if (it.id != teamId) {
                it.players.removeIf({ it.id == this.id })
            }
        }
    }

    private fun joinPaddleEvent(event: PaddleEvent, teamId: Long) {
        val team = event.teams.find { it.id == teamId } ?: throw BusinessException("El equipo no pertenece a este evento")
        joinTeamForMultipleTeamsEvent(team, event.teams, event.maxPlayers)
        removeFromTeams(event.teams, teamId)
    }

    private fun joinFootballTeam(event: FootballEvent, teamId: Long) {
        val team = getJoiningFootballTeam(event, teamId)
        val otherTeam = getOtherFootballTeam(event, teamId)
        validateTeam(team, event)
        team!!.players.add(this)
        if (otherTeam!!.players.any { it.id == this.id }) {
            otherTeam.players.removeIf({ it.id == this.id })
        }
    }

    private fun getJoiningFootballTeam(event: FootballEvent, teamId: Long) =
        when (teamId) {
            event.firstTeam?.id -> event.firstTeam
            event.secondTeam?.id -> event.secondTeam
            else -> throw BusinessException("El equipo no pertenece a este evento")
        }

    private fun getOtherFootballTeam(event: FootballEvent, teamId: Long) =
        when (teamId) {
            event.firstTeam?.id -> event.secondTeam
            event.secondTeam?.id -> event.firstTeam
            else -> throw BusinessException("El equipo no pertenece a este evento")
        }

    private fun validateTeam(
        team: Team?,
        event: FootballEvent
    ) {
        if (team!!.players.size >= event.maxPlayers / 2) {
            throw BusinessException("El equipo ya tiene la cantidad maxima de jugadores")
        }
        if (team.players.any { it.id == this.id }) {
            throw BusinessException("Ya eres parte del equipo!")
        }
    }
}
