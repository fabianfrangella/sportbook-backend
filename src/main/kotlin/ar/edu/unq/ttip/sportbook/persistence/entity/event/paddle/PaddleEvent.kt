package ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "PADDLE_EVENT")
class PaddleEvent() : Event() {
    init { this.sport = Sport.PADDLE }
    @OneToMany(targetEntity = Team::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "team_paddle",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "team_id")]
    )
    lateinit var teams: List<Team>

    override fun removePlayerFromTeams(player: Player) {
        teams.forEach { team -> team.players.remove(player) }
    }
}