package ar.edu.unq.ttip.sportbook.persistence.entity

import ar.edu.unq.ttip.sportbook.domain.Event
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.data.geo.Point
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "EVENT")
@Inheritance(strategy = InheritanceType.JOINED)
abstract class EventJPA() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    var minPlayers: Int = 0
    var maxPlayers: Int = 0
    lateinit var dateTime: LocalDateTime;
    lateinit var location: Point
    lateinit var cost: BigDecimal
    @OneToOne(cascade = [CascadeType.ALL])
    lateinit var transferData: TransferDataJPA
    @ManyToMany(targetEntity = PlayerJPA::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "event_player",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    lateinit var players: List<PlayerJPA>
    lateinit var creator: String
    lateinit var organizer: String

    constructor(
        minPlayers: Int,
        maxPlayers: Int,
        dateTime: LocalDateTime,
        location: Point,
        cost: BigDecimal,
        transferData: TransferDataJPA,
        players: List<PlayerJPA>,
        creator: String,
        organizer: String
    ) : this() {
        this.minPlayers = minPlayers
        this.maxPlayers = maxPlayers
        this.dateTime = dateTime
        this.location = location
        this.cost = cost
        this.transferData = transferData
        this.players = players
        this.creator = creator
        this.organizer = organizer
    }

    abstract fun toModel() : Event
}