package ar.edu.unq.ttip.sportbook.controller.response

data class SportStatsDTO(
    val sport: String,
    val matchesPlayed: Int,
    val mostMvpUser: String
)

data class UserStatsDTO(
    val userId: Long,
    val statsBySport: Map<String, UserSportStatsDTO>
)

data class UserSportStatsDTO(
    val sport: String,
    val matchesPlayed: Int,
    val victories: Int,
    val mvps: Int
)