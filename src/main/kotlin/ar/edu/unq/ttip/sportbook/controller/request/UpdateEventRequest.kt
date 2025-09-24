package ar.edu.unq.ttip.sportbook.controller.request

import java.math.BigDecimal

data class UpdateEventRequest(
    val cost: BigDecimal?,
    val creator: String?,
    val organizer: String?,
    val pitchSize: Int?,
    val transferDataCbu: String?,
    val transferDataAlias: String?,
    val locationX: String?,
    val locationY: String?,
    val locationPlaceName: String?
)
