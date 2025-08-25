package ar.edu.unq.ttip.sportbook.domain.volley

import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.Location
import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.Team
import ar.edu.unq.ttip.sportbook.domain.TransferData
import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.TransferDataJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.VolleyEventJPA
import java.math.BigDecimal
import java.time.LocalDateTime

class VolleyEvent (minPlayers: Int,
                   maxPlayers: Int,
                   dateTime: LocalDateTime,
                   location: Location,
                   cost: BigDecimal,
                   transferData: TransferData,
                   players: List<Player>,
                   creator: String,
                   organizer: String,
                   val teams: List<Team>) : Event(minPlayers, maxPlayers, dateTime, location, cost, transferData, players, creator, organizer) {

    override fun toEntity(): VolleyEventJPA {
        val volleyEventJpa = VolleyEventJPA()
        val transferDataJpa = TransferDataJPA()
        transferDataJpa.cbu = transferData.cbu
        transferDataJpa.alias = transferData.alias
        volleyEventJpa.minPlayers = minPlayers
        volleyEventJpa.maxPlayers = maxPlayers
        volleyEventJpa.dateTime = dateTime
        volleyEventJpa.location = location.toEntity()
        volleyEventJpa.cost = cost
        volleyEventJpa.transferData = transferDataJpa
        volleyEventJpa.players = players.map { PlayerJPA(it.name) }
        volleyEventJpa.creator = creator
        volleyEventJpa.organizer = organizer
        volleyEventJpa.teams = teams.map { it.toEntity() }
        return volleyEventJpa
    }
}