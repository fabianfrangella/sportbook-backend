package ar.edu.unq.ttip.sportbook.persistence.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "PADDLE_EVENT")
class PaddleEventJPA() : EventJPA() {
    init { this.sport = Sport.PADDLE }
    @OneToMany(targetEntity = TeamJPA::class, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "team_paddle",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "team_id")]
    )
    lateinit var teams: List<TeamJPA>

}