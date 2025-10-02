package ar.edu.unq.ttip.sportbook.service.command

data class FootballProfileUpdate(
    val positions: MutableList<String> = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null
)

data class VolleyProfileUpdate(
    val positions: MutableList<String> = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null,
    val blockHeight: Int? = null,
    val rolePreference: String? = null
)

data class PaddleProfileUpdate(
    val preferredSide: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null,
    val playStyle: String? = null,
    val playedTournaments: Boolean? = null
)