package ar.edu.unq.ttip.sportbook.persistence.entity.user

import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
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
    final var user: SportUser? = null

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true)
    @JsonIgnore
    var event: Event? = null

    constructor(name: String, user: SportUser) : this() {
        this.name = name
        this.user = user
    }

    fun calculateScore(sport: Sport): Double {
        return user?.calculatePlayerScore(sport) ?: 0.0
    }

    fun joinTeam(event: Event, team: Team) {


        val maxPerTeam = event.maxPlayers / event.teams.size
        if (team.players.size >= maxPerTeam) {
            throw BusinessException("El equipo ya está completo.")
        }

        if (team.players.any { it.id == this.id }) {
            throw BusinessException("Ya eres parte de este equipo.")
        }


        if (event.unnasignedPlayers.remove(this))

        event.teams.forEach { otherTeam ->
            if (otherTeam != team) {
                otherTeam.players.remove(this)
            }
        }

        team.players.add(this)
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
