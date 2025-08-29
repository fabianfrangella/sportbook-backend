package ar.edu.unq.ttip.sportbook.controller.dto

import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.domain.Location
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
    val transferData: TransferData,
    val players: List<Player>,
    val creator: String,
    val organizer: String,
    val matchDetails: Map<String, Any>
) {
    fun toModel(): Event {

        return when (sport) {
            Sport.FOOTBALL -> {
                val firstTeamMap = matchDetails["firstTeam"] as Map<*, *>
                val firstTeam = Team(
                    color = firstTeamMap["color"] as String,
                    players = (firstTeamMap["players"] as List<*>).map {
                        val playerMap = it as Map<*, *>
                        val userMap = playerMap["user"] as Map<*, *>
                        Player(
                            name = playerMap["name"] as String,
                            user = User(userName = userMap["userName"] as String)
                        )
                    }
                )

                val secondTeamMap = matchDetails["secondTeam"] as Map<*, *>
                val secondTeam = Team(
                    color = secondTeamMap["color"] as String,
                    players = (secondTeamMap["players"] as List<*>).map {
                        val playerMap = it as Map<*, *>
                        val userMap = playerMap["user"] as Map<*, *>
                        Player(
                            name = playerMap["name"] as String,
                            user = User(userName = userMap["userName"] as String)
                        )
                    }
                )
                val details = FootballMatchDetails(
                    pitchSize = PitchSize.fromString(matchDetails["pitchSize"] as String),
                    firstTeam,
                    secondTeam,
                )
                FootballEvent(
                    0, minPlayers, maxPlayers, dateTime, location, cost,
                    transferData,
                    players, creator, organizer, details
                )
            }

            Sport.VOLLEY -> {
                val teams = matchDetails["teams"] as List<LinkedHashMap<*,*>>
                val details = VolleyMatchDetails(teams.map { Team(it["color"] as String, it["players"] as List<Player>) })
                VolleyEvent(
                    0, minPlayers, maxPlayers, dateTime, location, cost,
                    transferData,
                    players, creator, organizer, details
                )
            }

            Sport.PADDEL -> {
                val teams = matchDetails["teams"] as List<LinkedHashMap<*,*>>
                val details = PaddelMatchDetails(teams.map { Team(it["color"] as String, it["players"] as List<Player>) })
                PaddelEvent(
                    0, minPlayers, maxPlayers, dateTime, location, cost,
                    transferData,
                    players, creator, organizer, details
                )
            }
        }
    }
}


enum class Sport {
    FOOTBALL, VOLLEY, PADDEL
}
