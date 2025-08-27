package ar.edu.unq.ttip.sportbook.persistence.entity

import ar.edu.unq.ttip.sportbook.controller.dto.Sport
import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.User
import ar.edu.unq.ttip.sportbook.domain.football.FootballEvent
import ar.edu.unq.ttip.sportbook.domain.football.FootballMatchDetails
import ar.edu.unq.ttip.sportbook.domain.football.PitchSize
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "FOOTBALL_EVENT")
class FootballEventJPA : EventJPA() {
    init { this.sport = Sport.FOOTBALL }
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
            players.map { Player(it.name, User(it.user.username)) },
            creator,
            organizer,
            matchDetails = FootballMatchDetails(
                pitchSize = PitchSize.fromNumber(pitchSize),
                firstTeam!!.toModel(),
                secondTeam!!.toModel()
            ),
        )
    }
}