package ar.edu.unq.ttip.sportbook.persistence.entity

import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.Team
import ar.edu.unq.ttip.sportbook.domain.volley.VolleyEvent
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "VOLLEY_EVENT")
class VolleyEventJPA : EventJPA() {
    @OneToMany(targetEntity = TeamJPA::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "team_volley",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "team_id")]
    )
    lateinit var teams: List<TeamJPA>

    override fun toModel(): VolleyEvent {
        return VolleyEvent(minPlayers,
            maxPlayers,
            dateTime,
            location.toModel(),
            cost,
            transferData.toModel(),
            players.map { Player(it.name) },
            creator,
            organizer,
            teams.map { Team (it.color, it.players.map { p -> Player(p.name)}) })
    }
}