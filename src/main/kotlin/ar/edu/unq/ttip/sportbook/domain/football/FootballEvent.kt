package ar.edu.unq.ttip.sportbook.domain.football

import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.Team
import ar.edu.unq.ttip.sportbook.domain.TransferData
import ar.edu.unq.ttip.sportbook.persistence.entity.EventJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.FootballEventJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.TeamJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.TransferDataJPA
import org.springframework.data.geo.Point
import java.math.BigDecimal
import java.time.LocalDateTime

class FootballEvent(
    minPlayers: Int,
    maxPlayers: Int,
    dateTime: LocalDateTime,
    location: Point,
    cost: BigDecimal,
    transferData: TransferData,
    players: List<Player>,
    creator: String,
    organizer: String,
    val firstTeam: Team,
    val secondTeam: Team,
    val pitchSize: PitchSize
) : Event(minPlayers, maxPlayers, dateTime, location, cost, transferData, players, creator, organizer) {
    override fun toEntity(): EventJPA {
        val transferDataJpa = TransferDataJPA()
        transferDataJpa.cbu = transferData.cbu
        transferDataJpa.alias = transferData.alias
        val footballEventJPA = FootballEventJPA()
        val firstTeam = TeamJPA()
        val secondTeam = TeamJPA()
        firstTeam.event = footballEventJPA
        secondTeam.event = footballEventJPA
        footballEventJPA.minPlayers = minPlayers
        footballEventJPA.maxPlayers = maxPlayers
        footballEventJPA.dateTime = dateTime
        footballEventJPA.location = location
        footballEventJPA.cost = cost
        footballEventJPA.transferData = transferDataJpa
        footballEventJPA.players = players.map { PlayerJPA(it.name) }
        footballEventJPA.creator = creator
        footballEventJPA.organizer = organizer
        footballEventJPA.pitchSize = pitchSize.size
        footballEventJPA.firstTeam = firstTeam
        footballEventJPA.secondTeam = secondTeam
        return footballEventJPA
    }

}