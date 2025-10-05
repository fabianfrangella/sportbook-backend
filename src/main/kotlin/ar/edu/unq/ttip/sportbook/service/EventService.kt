package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.request.FinishEventRequest
import ar.edu.unq.ttip.sportbook.controller.request.UpdateEventRequest
import ar.edu.unq.ttip.sportbook.controller.response.*
import ar.edu.unq.ttip.sportbook.exception.BadRequestException
import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.entity.team.TeamGoal
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.FinishedEventStatsRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.TeamJpaRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.math.max

@Service
class EventService(
    val eventJpaRepository: EventJpaRepository,
    val playerJpaRepository: PlayerJpaRepository,
    val teamJpaRepository: TeamJpaRepository,
    val lineupService: LineupService,
    val finishedEventStatsRepository: FinishedEventStatsRepository
) {

    @Transactional
    fun createEvent(event: Event): Event {
        val saved = eventJpaRepository.save(event)
        lineupService.createLineups(event)
        return saved
    }

    fun getEvent(id: Long): Event =
        eventJpaRepository.findById(id)
            .orElseThrow { NotFoundException("Evento no encontrado") }

    fun getFootballEvent(id: Long): FootballEvent {
        val event = getEvent(id)
        if (event !is FootballEvent) {
            throw BadRequestException("El evento no es de fútbol")
        }
        return event
    }

    fun getAllEvents(): List<Event> {
        return eventJpaRepository.findAll()
    }

    fun join(id: Long, user: SportUser) : Event = eventJpaRepository.findById(id)
        .map { joinEvent(user, it) }
        .orElseThrow { NotFoundException("Evento no encontrado") }

    fun joinTeam(eventId: Long, teamId: Long, user: SportUser) : Event {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { NotFoundException("Evento no encontrado") }
        val player = playerJpaRepository.findByUserUsername(user.username!!)
            .orElseThrow { NotFoundException("Jugador no encontrado") }
        player.joinTeam(event, teamId)

        lineupService.movePlayerFromTeamToBench(player, teamId, event)
        return eventJpaRepository.save(event)
    }

    private fun joinEvent(
        user: SportUser,
        event: Event
    ): Event {
        val player = playerJpaRepository.findByUserUsername(user.username!!).orElse(Player(name = user.name!!, user = user))
        event.join(player)
        eventJpaRepository.save(event)
        return event
    }

    fun leaveEvent(eventId: Long, user: SportUser) : Event {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { NotFoundException("Evento no encontrado") }
        val player = playerJpaRepository.findByUserUsername(user.username!!)
            .orElseThrow { NotFoundException("Jugador no encontrado") }

        event.leave(player)
        lineupService.removePlayerFromLineups(event, player)
        return eventJpaRepository.save(event)
    }

    @Transactional
    fun updateEvent(id: Long, updateRequest: UpdateEventRequest): Event {
        val event = eventJpaRepository.findById(id)
            .orElseThrow { NotFoundException("Evento no encontrado") }

        event.updateBasicFields(
            updateRequest.cost,
            updateRequest.creator,
            updateRequest.organizer
        )
        event.updateLocation(
            updateRequest.locationX,
            updateRequest.locationY,
            updateRequest.locationPlaceName
        )

        event.updateTransferData(
            updateRequest.transferDataCbu,
            updateRequest.transferDataAlias
        )

        event.updatePitchSize(updateRequest.pitchSize)

        return eventJpaRepository.save(event)
    }

    @Transactional
    fun finishEvent(eventId: Long, finishEventData: FinishEventRequest): FinishedEventStats {
        val event = getEvent(eventId)

        if (event.isFinished) {
            throw BusinessException("El evento $eventId ya fue finalizado")
        }
        event.finish()
        eventJpaRepository.save(event)
        val stats = FinishedEventStats(event, finishEventData)
        return finishedEventStatsRepository.save(stats)
    }

    @Transactional
    fun getStats(eventId: Long): EventStatsResponse {
        val event: Event = eventJpaRepository.findById(eventId)
            .orElseThrow { NotFoundException("Evento $eventId no existe") }

        if (!event.isFinished) {
            throw BadRequestException("El evento $eventId aún no está finalizado")
        }

        val stats: FinishedEventStats = finishedEventStatsRepository.findByEventIdWithGoals(eventId)
            ?: finishedEventStatsRepository.findByEventId(eventId)
            ?: throw NotFoundException("No hay estadísticas para el evento $eventId")

        val registered = event.players?.size ?: 0
        val missing = stats.missingPlayers.toList()
        val absent = missing.size
        val present = max(registered - absent, 0)
        val attendanceRate = if (registered > 0) present.toDouble() / registered else 0.0

        val goals: List<TeamGoal> = stats.goals.toList()
        val totalGoals = goals.size

        val goalsByTeam: Map<Long, Int> = goals
            .groupBy { it.team?.id ?: -1 }
            .mapValues { (_, v) -> v.size }
            .filterKeys { it != -1L }

        val winningId = stats.winningTeam?.id
        val winningColor = stats.winningTeam?.color

        val scores = goalsByTeam
            .map { (teamId, count) ->
                TeamScoreDTO(
                    teamId = teamId,
                    color = if (teamId == winningId) winningColor else goals.firstOrNull { it.team?.id == teamId }?.team?.color,
                    goals = count,
                    isWinner = (teamId == winningId)
                )
            }
            .sortedByDescending { it.goals }

        val goalsByPlayer: Map<Long, Pair<Player, Int>> = goals
            .groupBy { it.player?.id ?: -1 }
            .filterKeys { it != -1L }
            .mapValues { (_, list) ->
                val anyPlayer = list.first().player!!
                anyPlayer to list.size
            }

        val scorersRanking = goalsByPlayer
            .entries
            .sortedByDescending { it.value.second }
            .map { (playerId, pair) ->
                val (player, count) = pair
                PlayerGoalsDTO(
                    player = PlayerSummary(id = player.id, name = player.name),
                    teamId = goals.firstOrNull { it.player?.id == playerId }?.team?.id,
                    goals = count
                )
            }

        val mvpSummary = PlayerSummary(id = stats.mvp?.id, name = stats.mvp?.name)

        val missingSummaries = missing
            .map { PlayerSummary(id = it.id, name = it.name) }
            .sortedWith(compareBy { it.name })

        val winningTeamSummary = TeamSummary(id = winningId, color = winningColor)

        return EventStatsResponse(
            eventId = event.id,
            sport = event.sport,
            dateTime = event.dateTime,
            finished = event.isFinished,

            totalRegisteredPlayers = registered,
            presentPlayers = present,
            absentPlayers = absent,
            attendanceRate = attendanceRate,

            totalGoals = totalGoals,
            scores = scores,
            scorersRanking = scorersRanking,

            winningTeam = winningTeamSummary,
            mvp = mvpSummary,
            missingPlayers = missingSummaries,
        )
    }
}