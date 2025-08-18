package ar.edu.unq.ttip.sportbook.controller.dto

import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.Team
import ar.edu.unq.ttip.sportbook.domain.TransferData
import ar.edu.unq.ttip.sportbook.domain.football.FootballEvent
import ar.edu.unq.ttip.sportbook.domain.football.PitchSize
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.geo.Point
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateEventRequestBody(val sport: Sport,
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
                                  val organizer: String,
                                  val pitchSize: Int?
    ) {
    fun toModel() : Event {
       return when (sport) {
            Sport.FOOTBALL -> FootballEvent(minPlayers,
                maxPlayers,
                dateTime,
                location,
                cost,
                TransferData(cbu?:"", alias?:""),
                players.map { Player(it) },
                creator,
                organizer,
                Team("Black"),
                Team("White"),
                PitchSize.fromNumber(pitchSize))

           Sport.VOLLEY -> TODO()
           Sport.PADDEL -> TODO()
       }
    }
}


enum class Sport {
    FOOTBALL, VOLLEY, PADDEL
}
