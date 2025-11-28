package ar.edu.unq.ttip.sportbook.service.event_stats

import ar.edu.unq.ttip.sportbook.dto.response.MatchSnapshotDTO
import ar.edu.unq.ttip.sportbook.dto.response.PlayerHistoryDTO
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.repository.FinishedEventStatsRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class HistoricalInsightService(
    private val statsRepo: FinishedEventStatsRepository
) {

    fun getRawHistory(currentStats: FinishedEventStats): List<PlayerHistoryDTO> {
        val histories = mutableListOf<PlayerHistoryDTO>()
        val currentDate = currentStats.event!!.dateTime

        val playersToAnalyze = mutableSetOf<Player>()
        currentStats.mvp?.let { playersToAnalyze.add(it) }

        val topScorerGoal = currentStats.goals
            .groupBy { it.player }
            .maxByOrNull { it.value.size }?.key
        topScorerGoal?.let { playersToAnalyze.add(it) }

        playersToAnalyze.forEach { player ->
            val pastMatches = statsRepo.findHistoryByPlayer(player.id, currentDate, PageRequest.of(0, 10))

            val snapshots = pastMatches.map { match ->
                val goalsInThatMatch = match.goals.count { it.player?.id == player.id }
                val wonThatMatch = match.isVictoryFor(player.user!!.id)
                val wasMvpThatMatch = match.mvp?.id == player.id

                MatchSnapshotDTO(
                    date = match.event!!.dateTime.toLocalDate(),
                    won = wonThatMatch,
                    goalsScored = goalsInThatMatch,
                    wasMvp = wasMvpThatMatch
                )
            }

            histories.add(PlayerHistoryDTO(
                playerName = player.name!!,
                totalMatchesPlayed = snapshots.size,
                totalWins = snapshots.count { it.won },
                totalGoals = snapshots.sumOf { it.goalsScored },
                lastMatches = snapshots
            ))
        }

        return histories
    }
}