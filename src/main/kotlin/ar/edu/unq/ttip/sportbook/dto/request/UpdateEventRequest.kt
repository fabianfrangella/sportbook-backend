package ar.edu.unq.ttip.sportbook.dto.request

import java.math.BigDecimal

data class UpdateEventRequest(
    val cost: BigDecimal?,
    val pitchSize: Int?,
    val locationPlaceName: String?,
    val transferDataCbu: String?,
    val transferDataAlias: String?,
    val creator: String?,
    val organizerId: Long?,
    val locationX: String?,
    val locationY: String?,


    val name: String?,
    val dateTime: String?,
    val minPlayers: Int?,
    val maxPlayers: Int?
)