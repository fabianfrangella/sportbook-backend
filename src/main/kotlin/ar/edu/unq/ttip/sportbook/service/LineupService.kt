package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.exception.UnauthorizedException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballLineup
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Position
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.LineupRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LineupService(
    private val lineupRepository: LineupRepository,
    private val playerRepository: PlayerJpaRepository,
    private val eventRepository: EventJpaRepository
) {

    @Transactional
    fun deleteLineups(event: Event) {
        val lineups = lineupRepository.findByEvent(event)

        if (lineups.isNotEmpty()) {
            lineupRepository.deleteAllInBatch(lineups)
        }
    }

    @Transactional
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

    fun joinLineup(player: Player, targetTeam: Team, event: Event) {
        val lineups = lineupRepository.findByEvent(event)
        lineups.forEach { lineup ->
            lineup.removePlayer(player)
        }

        lineups.find { it.team.id == targetTeam.id }?.addPlayerToBench(player)

        lineupRepository.saveAll(lineups)
    }

    @Transactional
    fun removePlayerFromLineups(event: Event, player: Player) {
        val lineups = lineupRepository.findByEvent(event)

        if (lineups.isNotEmpty()) {
            lineups.forEach { lineup ->
                lineup.removePlayer(player)
            }
            lineupRepository.saveAll(lineups)
        }
    }

    @Transactional
    fun autoConfigureLineups(eventId: Long, organizer: SportUser): List<Lineup> {
        val event = eventRepository.findById(eventId)
            .orElseThrow { NotFoundException("Evento no encontrado") }

        if (event.organizer?.id != organizer.id) {
            throw UnauthorizedException("Solo el organizador puede generar tácticas")
        }

        if (event.sport != Sport.FOOTBALL) {
            throw BusinessException("La táctica automática solo está disponible para fútbol")
        }

        val footballEvent = event as FootballEvent
        val lineups = lineupRepository.findByEvent(event) as List<FootballLineup>

        lineups.forEach { lineup ->
            optimizeLineup(lineup, footballEvent.pitchSize)
            lineupRepository.save(lineup)
        }

        return lineups
    }

    private fun optimizeLineup(lineup: FootballLineup, pitchSize: Int) {
        val allPlayers = (lineup.positionsByPlayer.values + lineup.bench).distinctBy { it.id }.toMutableList()
        lineup.positionsByPlayer.clear()
        lineup.bench.clear()

        val targetPositions = getIdealFormation(pitchSize)

        targetPositions.forEach { position ->
            if (allPlayers.isNotEmpty()) {
                val bestCandidate = allPlayers.maxByOrNull { player ->
                    calculateSuitability(player, position)
                }

                if (bestCandidate != null) {
                    lineup.positionsByPlayer[position] = bestCandidate
                    allPlayers.remove(bestCandidate)
                }
            }
        }

        lineup.bench.addAll(allPlayers)
    }

    private fun getIdealFormation(pitchSize: Int): List<Position> {
        return when (pitchSize) {
            5 -> listOf(Position.GK, Position.LB, Position.RB, Position.CM, Position.ST)
            6 -> listOf(Position.GK, Position.LB, Position.RB, Position.CM, Position.LW, Position.RW)
            7 -> listOf(Position.GK, Position.CB, Position.LB, Position.RB, Position.CM, Position.LM, Position.ST)
            8 -> listOf(Position.GK, Position.CB, Position.LB, Position.RB, Position.CM, Position.LM, Position.RM, Position.ST)
            9 -> listOf(Position.GK, Position.CB, Position.LB, Position.RB, Position.CM, Position.LM, Position.RM, Position.ST, Position.CT)
            11 -> listOf(Position.GK, Position.LB, Position.CB, Position.RB, Position.LM, Position.CM, Position.RM, Position.LW, Position.RW, Position.ST, Position.CT)
            else -> listOf(Position.GK, Position.CM, Position.ST)
        }
    }

    private fun calculateSuitability(player: Player, position: Position): Double {
        val user = player.user ?: return 0.0
        val profile = user.profiles.find { it.sport == Sport.FOOTBALL }?.details as? FootballProfileDetail
            ?: return 1.0

        var score = 0.0

        if (profile.favoritePosition == position.name) score += 50.0

        if (profile.positions.contains(position.name)) score += 20.0

        score += (profile.ability ?: 5) * 2

        val performanceScore = user.calculatePlayerScore(Sport.FOOTBALL)

        if (position in listOf(Position.ST, Position.RW, Position.LW, Position.CT)) {
            score += performanceScore
        }

        if (position == Position.GK && !profile.positions.contains("GK")) {
            score -= 100.0
        }

        return score
    }

}
