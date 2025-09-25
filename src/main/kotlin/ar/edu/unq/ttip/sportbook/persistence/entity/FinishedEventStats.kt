package ar.edu.unq.ttip.sportbook.persistence.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "FINISHED_EVENT_STATS")
class FinishedEventStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonIgnore
    var event: Event? = null

    @OneToMany(mappedBy = "finishedEventStats", cascade = [CascadeType.ALL], orphanRemoval = true)
    var goals: MutableList<TeamGoal> = mutableListOf()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winning_team_id")
    var winningTeam: Team? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mvp_id")
    var mvp: Player? = null

    @ManyToMany
    @JoinTable(
        name = "finished_event_missing_players",
        joinColumns = [JoinColumn(name = "finished_event_stats_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    var missingPlayers: MutableSet<Player> = mutableSetOf()
}