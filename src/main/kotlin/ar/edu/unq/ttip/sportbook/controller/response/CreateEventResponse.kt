package ar.edu.unq.ttip.sportbook.controller.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateEventResponse(
    val id: Long,
    val sport: String,
    val cost: BigDecimal?,
    val dateTime: LocalDateTime,
    val location: LocationResponse,
    val organizerUsername: String,
    val playersCount: Int
)

data class LocationResponse(
    val x: Double,
    val y: Double,
    val placeName: String
)
