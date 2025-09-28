package ar.edu.unq.ttip.sportbook.service.command

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport

data class SportStats(val sport: Sport, val matchesPlayed: Int, val mostMvpUsername: String?)
data class UserSportStats(val sport: Sport, val matchesPlayed: Int, val victories: Int, val mvps: Int)
data class UserStats(val userId: Long, val bySport: Map<Sport, UserSportStats>)
