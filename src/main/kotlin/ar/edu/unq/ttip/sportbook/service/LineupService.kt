package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.exception.UnauthorizedException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Position
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.LineupRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import org.springframework.stereotype.Service

@Service
class LineupService(
    private val lineupRepository: LineupRepository,
    private val playerRepository: PlayerJpaRepository
) {

    fun createLineups(event: Event): List<Lineup> {
        val lineups = event.createLineups()
        return lineupRepository.saveAll(lineups)
    }

    fun getEventLineups(event: Event): List<Lineup> {
        return lineupRepository.findByEvent(event)
    }

    fun addPlayerToPosition(lineupId: Long, playerId: Long, position: Position, sportUser: SportUser): Lineup {
        val lineup = lineupRepository.findById(lineupId)
            .orElseThrow { NotFoundException("Lineup not found") }
        if (lineup.getEventOrganizer() != sportUser) {
            throw UnauthorizedException("Solo el organizador del evento puede modificar la formación")
        }
        val player = playerRepository.findById(playerId)
            .orElseThrow { NotFoundException("Player not found") }

        lineup.addPlayerToPosition(player, position)
        return lineupRepository.save(lineup)
    }

    fun removePlayerFromPosition(lineupId: Long, position: Position, sportUser: SportUser): Lineup {
        val lineup = lineupRepository.findById(lineupId)
            .orElseThrow { NotFoundException("Lineup not found") }
        if (lineup.getEventOrganizer() != sportUser) {
            throw UnauthorizedException("Solo el organizador del evento puede modificar la formación")
        }
        lineup.removePlayerFromPosition(position)
        return lineupRepository.save(lineup)
    }

    fun joinLineup(player: Player, team: Team, event: Event) {
        val lineups = lineupRepository.findByEvent(event)
        lineups.find { it.team == team }?.addPlayerToBench(player)
        lineups.filter { it.team != team }.forEach { it.removePlayer(player) }
        lineupRepository.saveAll(lineups)
    }

    fun removePlayerFromLineups(event: Event, player: Player) {
        val lineups = lineupRepository.findByEvent(event)
        lineups.forEach { lineup ->
            lineup.removePlayer(player)
        }
        lineupRepository.saveAll(lineups)
    }

    fun deleteLineups(event: Event) {
        val lineups = lineupRepository.findByEvent(event)
        lineupRepository.deleteAll(lineups)
    }
}
