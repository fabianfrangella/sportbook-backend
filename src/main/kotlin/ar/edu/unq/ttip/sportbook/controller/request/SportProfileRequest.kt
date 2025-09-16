package ar.edu.unq.ttip.sportbook.controller.request

data class UpdateFootballProfileRequest(
    val positions: MutableList<String>? = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null
)

data class UpdateVolleyProfileRequest(
    val positions: MutableList<String>? = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null,
    val blockHeight: Int? = null,
    val rolePreference: String? = null
)

data class UpdatePaddleProfileRequest(
    val preferredSide: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null,
    val playStyle: String? = null,
    val playedTournaments: Boolean? = null
)