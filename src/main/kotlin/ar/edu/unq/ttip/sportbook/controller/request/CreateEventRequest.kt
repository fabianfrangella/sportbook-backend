package ar.edu.unq.ttip.sportbook.controller.request

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import java.math.BigDecimal

data class CreateEventRequest(
    val sport: Sport,
    val minPlayers: Int,
    val maxPlayers: Int,
    val cost: BigDecimal?,
    val location: LocationInput,
    val pitchSize: Int?,
    val dateTime: String,
    val transferData: TransferDataInput?,
    val allEventPlayersInput: AllEventPlayersInput? = null, // usado en volley y paddle
    val firstTeamColor: String? = null,                     // usado en football
    val secondTeamColor: String? = null,
    val firstTeamRegisteredUsers: List<String>? = null,
    val firstTeamGuests: List<String>? = null,
    val secondTeamRegisteredUsers: List<String>? = null,
    val secondTeamGuests: List<String>? = null,
    val teams: String? = null                               // usado en volley/paddle
)

data class LocationInput(
    val x: Double,
    val y: Double,
    val placeName: String
)

data class TransferDataInput(
    val cbu: String?,
    val alias: String?
)

data class AllEventPlayersInput(
    val registeredUsers: List<String>,
    val guests: List<String>
)
