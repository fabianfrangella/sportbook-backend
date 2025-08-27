package ar.edu.unq.ttip.sportbook.persistence.entity

import ar.edu.unq.ttip.sportbook.controller.dto.Sport
import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.User
import ar.edu.unq.ttip.sportbook.domain.volley.VolleyEvent
import ar.edu.unq.ttip.sportbook.domain.volley.VolleyMatchDetails
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "VOLLEY_EVENT")
class VolleyEventJPA : EventJPA() {
    init { this.sport = Sport.VOLLEY }
    @OneToMany(targetEntity = TeamJPA::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "team_volley",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "team_id")]
    )
    lateinit var teams: List<TeamJPA>

    override fun toModel(): VolleyEvent {
        return VolleyEvent(id,
            minPlayers,
            maxPlayers,
            dateTime,
            location.toModel(),
            cost,
            transferData.toModel(),
            players.map { Player(it.name, User(it.user.username)) },
            creator,
            organizer,
            matchDetails = VolleyMatchDetails(
                teams = teams.map { it.toModel() }
            )
        )
    }
}