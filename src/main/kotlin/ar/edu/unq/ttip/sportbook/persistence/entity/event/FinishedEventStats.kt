package ar.edu.unq.ttip.sportbook.persistence.entity.event

import ar.edu.unq.ttip.sportbook.dto.request.FinishEventRequest
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.team.TeamGoal
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "FINISHED_EVENT_STATS")
class FinishedEventStats() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToOne
    @JoinColumn(name = "event_id")
    @JsonIgnore
    var event: Event? = null


    @OneToMany(mappedBy = "finishedEventStats", cascade = [CascadeType.ALL], orphanRemoval = true)
    var goals: MutableSet<TeamGoal> = mutableSetOf()


    @OneToMany(mappedBy = "finishedEventStats", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("setOrder ASC")
    var sets: MutableSet<EventSet> = mutableSetOf()

    @ManyToOne
    @JoinColumn(name = "winning_team_id")
    var winningTeam: Team? = null

    @ManyToOne
    @JoinColumn(name = "mvp_id")
    var mvp: Player? = null

    @ManyToMany
    @JoinTable(
        name = "finished_event_missing_players",
        joinColumns = [JoinColumn(name = "finished_event_stats_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    var missingPlayers: MutableSet<Player> = mutableSetOf()

    fun isVictoryFor(userId: Long): Boolean = winningTeam?.players?.any { it.user?.id == userId } == true

    fun mvpUsernameOrNull(): String? = mvp?.user?.username

    constructor(event: Event, finishEventRequest: FinishEventRequest) : this() {
        this.event = event


        this.goals = finishEventRequest.goals.map { teamGoalRequest ->
            TeamGoal(
                team = event.getTeam(teamGoalRequest.teamId),
                player = event.getPlayer(teamGoalRequest.playerId),
                finishedEventStats = this
            )
        }.toMutableSet()


        finishEventRequest.sets?.forEachIndexed { index, setReq ->
            this.sets.add(
                EventSet(
                    order = index + 1,
                    t1Score = setReq.team1Score,
                    t2Score = setReq.team2Score,
                    stats = this
                )
            )
        }

        this.winningTeam = finishEventRequest.winningTeamId?.let {
            event.getTeam(it)
        }

        this.mvp = finishEventRequest.mvpId?.let { event.getPlayer(it) }

        this.missingPlayers = finishEventRequest.missingPlayerIds
            .map { event.getPlayer(it) }
            .toMutableSet()
    }
}