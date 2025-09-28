package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.repository.FinishedEventStatsRepository
import ar.edu.unq.ttip.sportbook.service.command.SportStats
import ar.edu.unq.ttip.sportbook.service.command.UserSportStats
import ar.edu.unq.ttip.sportbook.service.command.UserStats
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StatisticsService(
    private val finishedEventStatsRepository: FinishedEventStatsRepository
) {

    fun getSportStats(sport: Sport): SportStats {
        val stats = finishedEventStatsRepository.findAllBySport(sport)

        val matchesPlayed = stats.size
        val mostMvpUsername = stats.asSequence()
            .mapNotNull { it.mvpUsernameOrNull() }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

        return SportStats(sport = sport, matchesPlayed = matchesPlayed, mostMvpUsername = mostMvpUsername)
    }

    fun getUserStats(userId: Long): UserStats {
        val stats = finishedEventStatsRepository.findAllByUserId(userId)

        val bySport: Map<Sport, UserSportStats> = stats
            .groupBy { it.event!!.sport }
            .mapValues { (sport, events) -> buildUserSportStats(userId, sport, events) }

        return UserStats(userId = userId, bySport = bySport)
    }

    fun getUserSportStats(userId: Long, sport: Sport): UserSportStats {
        val stats = finishedEventStatsRepository.findAllByUserIdAndSport(userId, sport)
        return buildUserSportStats(userId, sport, stats)
    }

    private fun buildUserSportStats(
        userId: Long,
        sport: Sport,
        stats: List<FinishedEventStats>
    ): UserSportStats =
        UserSportStats(
            sport = sport,
            matchesPlayed = stats.size,
            victories = stats.count { it.isVictoryFor(userId) },
            mvps = stats.count { it.mvp?.user?.id == userId }
        )
}

