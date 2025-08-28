package ar.edu.unq.ttip.sportbook.domain.paddel

import ar.edu.unq.ttip.sportbook.controller.dto.Sport
import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.Location
import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.TransferData
import ar.edu.unq.ttip.sportbook.persistence.entity.PaddelEventJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.TransferDataJPA
import java.math.BigDecimal
import java.time.LocalDateTime

class PaddelEvent(id: Long = 0,
                  minPlayers: Int,
                  maxPlayers: Int,
                  dateTime: LocalDateTime,
                  location: Location,
                  cost: BigDecimal,
                  transferData: TransferData,
                  players: List<Player>,
                  creator: String,
                  organizer: String,
                  override val matchDetails: PaddelMatchDetails,
                  ) : Event(id, minPlayers, maxPlayers, dateTime, location, cost, transferData, players, creator, organizer) {
    override val sport: Sport = Sport.PADDEL

    override fun toEntity(): PaddelEventJPA {
        return toEntity(players.map { PlayerJPA(it.name) })
    }

    override fun toEntity(players: List<PlayerJPA>): PaddelEventJPA {
        val paddelEventJpa = PaddelEventJPA()
        val transferDataJpa = TransferDataJPA()
        transferDataJpa.cbu = transferData.cbu
        transferDataJpa.alias = transferData.alias
        paddelEventJpa.minPlayers = minPlayers
        paddelEventJpa.maxPlayers = maxPlayers
        paddelEventJpa.dateTime = dateTime
        paddelEventJpa.location = location.toEntity()
        paddelEventJpa.cost = cost
        paddelEventJpa.transferData = transferDataJpa
        paddelEventJpa.players = players
        paddelEventJpa.creator = creator
        paddelEventJpa.organizer = organizer
        paddelEventJpa.teams = matchDetails.teams.map { it.toEntity() }
        return paddelEventJpa
    }
}