package ar.edu.unq.ttip.sportbook.domain.football

import ar.edu.unq.ttip.sportbook.controller.dto.Sport
import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.Location
import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.TransferData
import ar.edu.unq.ttip.sportbook.persistence.entity.FootballEventJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.SportUserJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.TransferDataJPA
import java.math.BigDecimal
import java.time.LocalDateTime

class FootballEvent(
    id: Long = 0,
    minPlayers: Int,
    maxPlayers: Int,
    dateTime: LocalDateTime,
    location: Location,
    cost: BigDecimal,
    transferData: TransferData,
    players: List<Player>,
    creator: String,
    organizer: String,
    override val matchDetails: FootballMatchDetails
) : Event(id, minPlayers, maxPlayers, dateTime, location, cost, transferData, players, creator, organizer) {
    override val sport: Sport = Sport.FOOTBALL
    override fun toEntity(): FootballEventJPA {
        return toEntity(players = players.map { PlayerJPA(it.name, SportUserJPA()) })
    }

    override fun toEntity(players: List<PlayerJPA>): FootballEventJPA {
        val transferDataJpa = TransferDataJPA()
        transferDataJpa.cbu = transferData.cbu
        transferDataJpa.alias = transferData.alias

        // TODO: Esta logica luego vuela, por ahora solo se pueden agregar jugadores registrados
        // mas adelante se va a modelar los "invitados"
        val firstTeamPlayers = matchDetails.firstTeam.players.map {
            players.find { player -> player.user.username === it.user.userName }
        }
            .filter { it != null }
        val secondTeamPlayers = matchDetails.secondTeam.players.map {
            players.find { player -> player.user.username === it.user.userName }
        }
            .filter { it != null }

        val footballEventJPA = FootballEventJPA()
        footballEventJPA.minPlayers = minPlayers
        footballEventJPA.maxPlayers = maxPlayers
        footballEventJPA.dateTime = dateTime
        footballEventJPA.location = location.toEntity()
        footballEventJPA.cost = cost
        footballEventJPA.transferData = transferDataJpa
        footballEventJPA.players = players
        footballEventJPA.creator = creator
        footballEventJPA.organizer = organizer
        footballEventJPA.pitchSize = matchDetails.pitchSize.size
        footballEventJPA.firstTeam = matchDetails.firstTeam.toEntity(firstTeamPlayers as List<PlayerJPA>)
        footballEventJPA.secondTeam = matchDetails.secondTeam.toEntity(secondTeamPlayers as List<PlayerJPA>)
        return footballEventJPA
    }

}