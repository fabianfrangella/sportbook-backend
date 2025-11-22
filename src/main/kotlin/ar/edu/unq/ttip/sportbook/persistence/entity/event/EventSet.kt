package ar.edu.unq.ttip.sportbook.persistence.entity.event

import jakarta.persistence.*

@Entity
@Table(name = "EVENT_SET")
class EventSet() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var setOrder: Int = 0



    var team1Score: Int = 0
    var team2Score: Int = 0

    @ManyToOne
    @JoinColumn(name = "finished_event_stats_id")
    var finishedEventStats: FinishedEventStats? = null

    constructor(order: Int, t1Score: Int, t2Score: Int, stats: FinishedEventStats) : this() {
        this.setOrder = order
        this.team1Score = t1Score
        this.team2Score = t2Score
        this.finishedEventStats = stats
    }
}