package ar.edu.unq.ttip.sportbook.persistence.entity

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
}