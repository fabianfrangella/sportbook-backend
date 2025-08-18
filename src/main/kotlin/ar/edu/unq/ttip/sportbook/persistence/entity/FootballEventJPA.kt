package ar.edu.unq.ttip.sportbook.persistence.entity


import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "FOOTBALL_EVENT")
class FootballEventJPA : EventJPA() {
    @ManyToOne(cascade = [CascadeType.ALL])
    var firstTeam: TeamJPA? = null
    @ManyToOne(cascade = [CascadeType.ALL])
    var secondTeam: TeamJPA? = null
    var pitchSize: Int = 0
}