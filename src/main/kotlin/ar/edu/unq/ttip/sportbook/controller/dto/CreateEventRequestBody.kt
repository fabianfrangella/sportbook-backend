package ar.edu.unq.ttip.sportbook.controller.dto

import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.Location
import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.TransferData
import ar.edu.unq.ttip.sportbook.domain.User
import ar.edu.unq.ttip.sportbook.domain.football.FootballEvent
import ar.edu.unq.ttip.sportbook.domain.football.FootballMatchDetails
import ar.edu.unq.ttip.sportbook.domain.paddel.PaddelEvent
import ar.edu.unq.ttip.sportbook.domain.paddel.PaddelMatchDetails
import ar.edu.unq.ttip.sportbook.domain.volley.VolleyEvent
import ar.edu.unq.ttip.sportbook.domain.volley.VolleyMatchDetails
import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateEventRequestBody(
    val sport: Sport,
    val minPlayers: Int,
    val maxPlayers: Int,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val dateTime: LocalDateTime,
    val location: Location,
    val cost: BigDecimal,
    val cbu: String?,
    val alias: String?,
    val players: List<String>,
    val creator: String,
    val organizer: String,
    val matchDetails: Any
) {
    fun toModel(): Event {
        val mappedPlayers = players.map { Player("", User(userName = it)) }

        return when (sport) {
            Sport.FOOTBALL -> {
                val details = matchDetails as FootballMatchDetails
                FootballEvent(
                    minPlayers, maxPlayers, dateTime, location, cost,
                    TransferData(cbu ?: "", alias ?: ""),
                    mappedPlayers, creator, organizer, details
                )
            }

            Sport.VOLLEY -> {
                val details = matchDetails as VolleyMatchDetails
                VolleyEvent(
                    minPlayers, maxPlayers, dateTime, location, cost,
                    TransferData(cbu ?: "", alias ?: ""),
                    mappedPlayers, creator, organizer, details
                )
            }

            Sport.PADDEL -> {
                val details = matchDetails as PaddelMatchDetails
                PaddelEvent(
                    minPlayers, maxPlayers, dateTime, location, cost,
                    TransferData(cbu ?: "", alias ?: ""),
                    mappedPlayers, creator, organizer, details
                )
            }
        }
    }
}


enum class Sport {
    FOOTBALL, VOLLEY, PADDEL
}
