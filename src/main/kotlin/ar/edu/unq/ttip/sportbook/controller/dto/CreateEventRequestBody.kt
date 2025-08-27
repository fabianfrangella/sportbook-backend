package ar.edu.unq.ttip.sportbook.controller.dto

import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.Location
import ar.edu.unq.ttip.sportbook.domain.MatchDetails
import ar.edu.unq.ttip.sportbook.domain.Player
import ar.edu.unq.ttip.sportbook.domain.Team
import ar.edu.unq.ttip.sportbook.domain.TransferData
import ar.edu.unq.ttip.sportbook.domain.User
import ar.edu.unq.ttip.sportbook.domain.football.FootballEvent
import ar.edu.unq.ttip.sportbook.domain.football.FootballMatchDetails
import ar.edu.unq.ttip.sportbook.domain.football.PitchSize
import ar.edu.unq.ttip.sportbook.domain.paddel.PaddelEvent
import ar.edu.unq.ttip.sportbook.domain.paddel.PaddelMatchDetails
import ar.edu.unq.ttip.sportbook.domain.volley.VolleyEvent
import ar.edu.unq.ttip.sportbook.domain.volley.VolleyMatchDetails
import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.LinkedHashMap

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
    val matchDetails: Map<String, Any>
) {
    fun toModel(): Event {
        val mappedPlayers = players.map { Player("", User(userName = it)) }

        return when (sport) {
            Sport.FOOTBALL -> {
                val firstTeam = matchDetails["firstTeam"] as LinkedHashMap<*, *>
                val secondTeam = matchDetails["secondTeam"] as LinkedHashMap<*, *>
                val details = FootballMatchDetails(
                    firstTeam = Team(firstTeam["color"] as String, firstTeam["players"] as List<Player>),
                    secondTeam = Team(secondTeam["color"] as String, secondTeam["players"] as List<Player>),
                    pitchSize = PitchSize.fromString(matchDetails["pitchSize"] as String)
                )
                FootballEvent(
                    0, minPlayers, maxPlayers, dateTime, location, cost,
                    TransferData(cbu ?: "", alias ?: ""),
                    mappedPlayers, creator, organizer, details
                )
            }

            Sport.VOLLEY -> {
                val teams = matchDetails["teams"] as List<LinkedHashMap<*,*>>
                val details = VolleyMatchDetails(teams.map { Team(it["color"] as String, it["players"] as List<Player>) })
                VolleyEvent(
                    0, minPlayers, maxPlayers, dateTime, location, cost,
                    TransferData(cbu ?: "", alias ?: ""),
                    mappedPlayers, creator, organizer, details
                )
            }

            Sport.PADDEL -> {
                val teams = matchDetails["teams"] as List<LinkedHashMap<*,*>>
                val details = PaddelMatchDetails(teams.map { Team(it["color"] as String, it["players"] as List<Player>) })
                PaddelEvent(
                    0, minPlayers, maxPlayers, dateTime, location, cost,
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
