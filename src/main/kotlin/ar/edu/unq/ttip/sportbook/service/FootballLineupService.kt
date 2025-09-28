package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballLineup
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Position
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.repository.FootballLineupRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import org.springframework.stereotype.Service

@Service
class FootballLineupService(
    private val footballLineupRepository: FootballLineupRepository,
    private val playerRepository: PlayerJpaRepository
) {
    fun createLineup(event: FootballEvent, team: Team): FootballLineup {
        val lineup = FootballLineup().apply {
            this.event = event
            this.team = team
        }
        return footballLineupRepository.save(lineup)
    }

    fun getEventLineups(event: FootballEvent): List<FootballLineup> {
        return footballLineupRepository.findByEvent(event)
    }

    fun addPlayerToPosition(lineupId: Long, playerId: Long, position: Position): FootballLineup {
        val lineup = footballLineupRepository.findById(lineupId)
            .orElseThrow { NotFoundException("Lineup not found") }

        val player = playerRepository.findById(playerId)
            .orElseThrow { NotFoundException("Player not found") }

        lineup.addPlayerToPosition(player, position)
        return footballLineupRepository.save(lineup)
    }

    fun removePlayerFromPosition(lineupId: Long, position: Position): FootballLineup {
        val lineup = footballLineupRepository.findById(lineupId)
            .orElseThrow { NotFoundException("Lineup not found") }

        lineup.removePlayerFromPosition(position)
        return footballLineupRepository.save(lineup)
    }

    fun save(lineup: FootballLineup): FootballLineup {
        return footballLineupRepository.save(lineup)
    }
}
