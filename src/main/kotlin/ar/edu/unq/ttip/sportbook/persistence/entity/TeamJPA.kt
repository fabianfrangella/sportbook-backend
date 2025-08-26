package ar.edu.unq.ttip.sportbook.persistence.entity

import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.Team
import ar.edu.unq.ttip.sportbook.domain.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "TEAM")
class TeamJPA() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    @ManyToMany(targetEntity = PlayerJPA::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "team_player",
        joinColumns = [JoinColumn(name = "team_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    var players: MutableList<PlayerJPA> = mutableListOf()
    @ManyToOne(targetEntity = EventJPA::class)
    lateinit var event: EventJPA
    lateinit var color: String

    constructor(color: String) : this() {
        this.color = color
    }

    fun toModel(): Team {
        return Team(color, players.map { Player(it.name, User(it.user.username)) })
    }
}