package ar.edu.unq.ttip.sportbook.persistence.entity

import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.Team
import ar.edu.unq.ttip.sportbook.domain.paddel.PaddelEvent
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "PADDEL_EVENT")
class PaddelEventJPA : EventJPA() {

    @OneToMany(targetEntity = TeamJPA::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "team_paddel",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "team_id")]
    )
    lateinit var teams: List<TeamJPA>

    override fun toModel(): PaddelEvent {
        return PaddelEvent(minPlayers,
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