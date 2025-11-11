package ar.edu.unq.ttip.sportbook.persistence.entity.team

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "TEAM")
class Team() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToMany(targetEntity = Player::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "team_player",
        joinColumns = [JoinColumn(name = "team_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    var players: MutableList<Player> = mutableListOf()

    lateinit var color: TeamColor

    fun hasPlayerId(playerId: Long): Boolean = players.any { it.id == playerId }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Team) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun clear() = players.clear()


}