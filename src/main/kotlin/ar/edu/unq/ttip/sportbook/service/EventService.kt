package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.request.FinishEventRequest
import ar.edu.unq.ttip.sportbook.controller.request.UpdateEventRequest
import ar.edu.unq.ttip.sportbook.controller.response.*
import ar.edu.unq.ttip.sportbook.exception.BadRequestException
import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.FinishedEventStatsRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.TeamJpaRepository
import ar.edu.unq.ttip.sportbook.service.event_stats.EventStatsCalculator
import ar.edu.unq.ttip.sportbook.service.event_stats.EventStatsMapper
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class EventService(
    val eventJpaRepository: EventJpaRepository,
    val playerJpaRepository: PlayerJpaRepository,
    val lineupService: LineupService,
    val finishedEventStatsRepository: FinishedEventStatsRepository,
    val teamRepository: TeamJpaRepository,
    private val calculator: EventStatsCalculator,
    private val mapper: EventStatsMapper,
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

    fun getAllEvents(): List<Event> {
        return eventJpaRepository.findAll()
    }

    fun join(id: Long, user: SportUser) : Event = eventJpaRepository.findById(id)
        .map { joinEvent(user, it) }
        .orElseThrow { NotFoundException("Evento no encontrado") }

    fun joinTeam(eventId: Long, teamId: Long, user: SportUser) : Event {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { NotFoundException("Evento no encontrado") }
        val player = playerJpaRepository.findByUserUsernameAndEventId(user.username!!, eventId)
            .orElseThrow { NotFoundException("Jugador no encontrado") }
        val team = teamRepository.findById(teamId)
            .orElseThrow { NotFoundException("Equipo no encontrado") }
        player.joinTeam(event, team)

        lineupService.movePlayerFromTeamToBench(player, team, event)
        return eventJpaRepository.save(event)
    }

    private fun joinEvent(
        user: SportUser,
        event: Event
    ): Event {
        val player = Player(name = user.name!!, user = user)
        event.join(player)
        eventJpaRepository.save(event)
        return event
    }

    fun leaveEvent(eventId: Long, user: SportUser) : Event {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { NotFoundException("Evento no encontrado") }
        val player = playerJpaRepository.findByUserUsernameAndEventId(user.username!!, eventId)
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

        val stats = finishedEventStatsRepository.fetchGraphByEventId(eventId)
            ?: throw NotFoundException("No hay estadísticas para el evento $eventId")

        val calc = calculator.compute(event, stats)
        return mapper.toResponse(event, calc)
    }
}