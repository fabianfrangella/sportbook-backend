package ar.edu.unq.ttip.sportbook.service.command

import ar.edu.unq.ttip.sportbook.persistence.entity.user.PlayFrequency

data class FootballProfileUpdate(
    val positions: MutableList<String> = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: PlayFrequency? = null
)

data class VolleyProfileUpdate(
    val positions: MutableList<String> = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: PlayFrequency? = null,
    val blockHeight: Int? = null,
    val rolePreference: String? = null,
    val serveType: String? = null,
    val offensiveLevel: Int? = null,
    val defensiveLevel: Int? = null
)

data class PaddleProfileUpdate(
    val preferredSide: String? = null,
    val ability: Int? = null,
    val playsOften: PlayFrequency? = null,
    val playStyle: String? = null,
    val playedTournaments: Boolean? = null
)