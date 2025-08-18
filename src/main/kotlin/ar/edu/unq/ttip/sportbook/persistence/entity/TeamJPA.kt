package ar.edu.unq.ttip.sportbook.persistence.entity

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
class TeamJPA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    @ManyToMany(targetEntity = PlayerJPA::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "team_player",
        joinColumns = [JoinColumn(name = "team_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    lateinit var players: MutableList<PlayerJPA>
    @ManyToOne(targetEntity = EventJPA::class)
    lateinit var event: EventJPA
}