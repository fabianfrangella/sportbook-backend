package ar.edu.unq.ttip.sportbook.persistence.entity.event.football

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "FOOTBALL_EVENT")
class FootballEvent : Event() {
    init { this.sport = Sport.FOOTBALL }
    @ManyToOne(cascade = [CascadeType.ALL])
    var firstTeam: Team? = null
    @ManyToOne(cascade = [CascadeType.ALL])
    var secondTeam: Team? = null
    var pitchSize: Int = 0

    override fun removePlayerFromTeams(player: Player) {
        firstTeam?.players?.remove(player)
        secondTeam?.players?.remove(player)
    }

    fun updatePitchSize(size: Int?) {
        size?.let { pitchSize = it }
    }
}