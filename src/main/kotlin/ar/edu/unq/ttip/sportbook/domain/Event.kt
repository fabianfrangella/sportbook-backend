package ar.edu.unq.ttip.sportbook.domain

import ar.edu.unq.ttip.sportbook.controller.dto.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.EventJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import java.math.BigDecimal
import java.time.LocalDateTime

interface MatchDetails

abstract class Event(
    val minPlayers: Int,
    val maxPlayers: Int,
    val dateTime: LocalDateTime,
    val location: Location,
    val cost: BigDecimal,
    val transferData: TransferData,
    val players: List<Player>,
    val creator: String,
    val organizer: String,
) {
    abstract val sport: Sport
    abstract val matchDetails: MatchDetails
    abstract fun toEntity(): EventJPA
    abstract fun toEntity(players: List<PlayerJPA>): EventJPA

}

