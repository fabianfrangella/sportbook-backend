package ar.edu.unq.ttip.sportbook.dto

import ar.edu.unq.ttip.sportbook.persistence.entity.Sport

data class UpdateFootballProfileRequest(
    val positions: MutableList<String>? = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null
)

data class FootballProfileDTO(
    val sport: String = "FOOTBALL",
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

data class VolleyProfileDTO(
    val sport: String = "VOLLEY",
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

data class PaddleProfileDTO(
    val sport: String = "PADDLE",
    val preferredSide: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null,
    val playStyle: String? = null,
    val playedTournaments: Boolean? = null
)

data class SportProfileDTO(
    val sport: Sport,
    val details: Any
)