package ar.edu.unq.ttip.sportbook.domain

import ar.edu.unq.ttip.sportbook.controller.dto.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.EventJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import ar.edu.unq.ttip.sportbook.util.Either
import java.math.BigDecimal
import java.time.LocalDateTime

interface MatchDetails

abstract class Event(
    var id: Long,
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

    fun canJoin(username: String) : Either<Boolean, String> {
        if (isFull()) return Either.Right("El evento está completo")
        return Either.Left(players.find { player -> player.user.userName == username } == null)
    }

    private fun isFull() = players.size >= maxPlayers

}

