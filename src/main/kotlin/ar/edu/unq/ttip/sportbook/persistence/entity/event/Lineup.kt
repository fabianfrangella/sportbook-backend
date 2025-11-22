package ar.edu.unq.ttip.sportbook.persistence.entity.event

import ar.edu.unq.ttip.sportbook.persistence.entity.team.Position
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "LINEUP")
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Lineup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "event_id")
    lateinit var event: Event

    @ManyToOne
    @JoinColumn(name = "team_id")
    lateinit var team: Team

    @ManyToMany
    @JoinTable(
        name = "football_lineup_initial",
        joinColumns = [JoinColumn(name = "lineup_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    var initialLineup: MutableList<Player> = mutableListOf()
    abstract fun addPlayerToPosition(player: Player, position: Position)
    abstract fun removePlayerFromPosition(position: Position)
    abstract fun addPlayerToBench(player: Player)
    abstract fun removePlayer(player: Player)

    fun getEventOrganizer() = event.organizer
}