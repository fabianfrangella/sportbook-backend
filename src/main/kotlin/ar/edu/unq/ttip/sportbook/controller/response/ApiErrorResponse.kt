package ar.edu.unq.ttip.sportbook.controller.response

data class ApiErrorResponse(
    val status: Int,
    val error: String,
    val message: String?,
    val path: String? = null
)