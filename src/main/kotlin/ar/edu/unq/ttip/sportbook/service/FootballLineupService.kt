package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.FootballLineup
import ar.edu.unq.ttip.sportbook.persistence.entity.Position
import ar.edu.unq.ttip.sportbook.persistence.entity.Team
import ar.edu.unq.ttip.sportbook.persistence.repository.FootballLineupRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

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
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Lineup not found") }

        val player = playerRepository.findById(playerId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found") }

        if (!lineup.team.players.contains(player)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Player is not in the team")
        }

        lineup.addPlayerToPosition(player, position)
        return footballLineupRepository.save(lineup)
    }

    fun removePlayerFromPosition(lineupId: Long, position: Position): FootballLineup {
        val lineup = footballLineupRepository.findById(lineupId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Lineup not found") }

        lineup.removePlayerFromPosition(position)
        return footballLineupRepository.save(lineup)
    }

    fun addPlayerToBench(lineupId: Long, playerId: Long) : FootballLineup {
        val lineup = footballLineupRepository.findById(lineupId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Lineup not found") }
        val player = playerRepository.findById(playerId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found") }

        lineup.addPlayerToBench(player)
        return lineup
    }

    fun save(lineup: FootballLineup): FootballLineup {
        return footballLineupRepository.save(lineup)
    }
}
