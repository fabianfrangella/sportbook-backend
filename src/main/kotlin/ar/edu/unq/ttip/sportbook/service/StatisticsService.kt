package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.response.SportStatsDTO
import ar.edu.unq.ttip.sportbook.controller.response.UserSportStatsDTO
import ar.edu.unq.ttip.sportbook.controller.response.UserStatsDTO
import ar.edu.unq.ttip.sportbook.persistence.entity.Sport
import ar.edu.unq.ttip.sportbook.persistence.repository.FinishedEventStatsRepository
import org.springframework.stereotype.Service

@Service
class StatisticsService(
    private val finishedEventStatsRepository: FinishedEventStatsRepository
) {

    fun getSportStats(sport: String): SportStatsDTO {
        val sportEnum = Sport.valueOf(sport.uppercase())
        val stats = finishedEventStatsRepository.findAll()
            .filter { it.event?.sport == sportEnum }

        val matchesPlayed = stats.size
        val mostMvpUser = stats
            .mapNotNull { it.mvp?.user?.username }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

        return SportStatsDTO(
            sport = sportEnum.name,
            matchesPlayed = matchesPlayed,
            mostMvpUser = mostMvpUser ?: "N/A"
        )
    }

    fun getUserStats(userId: Long): UserStatsDTO {
        val stats = finishedEventStatsRepository.findAll()
            .filter { fes -> fes.event?.players?.any { it.user.id == userId } == true }

        val statsBySport: Map<String, UserSportStatsDTO> =
            stats.groupBy { it.event?.sport?.name ?: "UNKNOWN" }
                .mapValues { (sport, events) ->
                    buildUserSportStats(userId, sport, events)
                }

        return UserStatsDTO(userId = userId, statsBySport = statsBySport)
    }

    fun getUserSportStats(userId: Long, sport: String): UserSportStatsDTO {
        val sportEnum = Sport.valueOf(sport.uppercase())
        val stats = finishedEventStatsRepository.findAll()
            .filter {
                it.event?.sport == sportEnum &&
                        it.event?.players?.any { p -> p.user.id == userId } == true
            }

        return buildUserSportStats(userId, sportEnum.name, stats)
    }

    private fun buildUserSportStats(
        userId: Long,
        sport: String,
        stats: List<ar.edu.unq.ttip.sportbook.persistence.entity.FinishedEventStats>
    ): UserSportStatsDTO {
        val matchesPlayed = stats.size
        val victories = stats.count { fes ->
            fes.winningTeam?.players?.any { it.user.id == userId } == true
        }
        val mvps = stats.count { fes -> fes.mvp?.user?.id == userId }

        return UserSportStatsDTO(
            sport = sport,
            matchesPlayed = matchesPlayed,
            victories = victories,
            mvps = mvps
        )
    }
}
