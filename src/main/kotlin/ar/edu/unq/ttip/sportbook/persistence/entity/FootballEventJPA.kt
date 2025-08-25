package ar.edu.unq.ttip.sportbook.persistence.entity


import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.football.FootballEvent
import ar.edu.unq.ttip.sportbook.domain.football.PitchSize
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
    override fun toModel(): Event {
        return FootballEvent(minPlayers,
            maxPlayers,
            dateTime,
            location.toModel(),
            cost,
            transferData.toModel(),
            players.map { Player(it.name) },
            creator,
            organizer,
            firstTeam!!.toModel(),
            secondTeam!!.toModel(),
            PitchSize.fromNumber(pitchSize))
    }
}