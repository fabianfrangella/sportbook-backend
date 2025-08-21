package ar.edu.unq.ttip.sportbook.controller.dto

import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.football.FootballEvent
import ar.edu.unq.ttip.sportbook.domain.paddel.PaddelEvent
import ar.edu.unq.ttip.sportbook.domain.volley.VolleyEvent
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.geo.Point
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateEventResponseBody(val sport: Sport,
                                   val minPlayers: Int,
                                   val maxPlayers: Int,
                                   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                   val dateTime: LocalDateTime,
                                   val location: Point,
                                   val cost: BigDecimal,
                                   val cbu: String?,
                                   val alias: String?,
                                   val players: List<String>,
                                   val creator: String,
                                   val organizer: String) {
    companion object {
        fun fromEvent(event: Event) : CreateEventResponseBody {
            return when(event) {
                is FootballEvent -> buildResponse(Sport.FOOTBALL, event)
                is VolleyEvent -> buildResponse(Sport.VOLLEY, event)
                is PaddelEvent -> buildResponse(Sport.PADDEL, event)
                else -> { throw RuntimeException("Not implemented Sport") }
            }
        }

        private fun buildResponse(sport: Sport, event: Event) : CreateEventResponseBody {
            return CreateEventResponseBody(sport,
                event.minPlayers,
                event.maxPlayers,
                event.dateTime,
                event.location,
                event.cost,
                event.transferData.cbu,
                event.transferData.alias,
                event.players.map { it.name },
                event.creator,
                event.organizer)
        }
    }
}
