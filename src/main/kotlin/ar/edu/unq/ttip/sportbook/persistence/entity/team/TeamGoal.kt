package ar.edu.unq.ttip.sportbook.persistence.entity.team

import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "TEAM_GOAL")
class TeamGoal() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "team_id")
    var team: Team? = null

    @ManyToOne
    @JoinColumn(name = "player_id")
    var player: Player? = null

    @ManyToOne
    @JoinColumn(name = "finished_event_stats_id")
    @JsonIgnore
    var finishedEventStats: FinishedEventStats? = null

    constructor(team: Team, player: Player, finishedEventStats: FinishedEventStats) : this() {
        this.team = team
        this.player = player
        this.finishedEventStats = finishedEventStats
    }
}