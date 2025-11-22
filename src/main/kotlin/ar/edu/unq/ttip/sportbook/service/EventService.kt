package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.dto.request.UpdateEventRequest
import ar.edu.unq.ttip.sportbook.dto.request.FinishEventRequest
import ar.edu.unq.ttip.sportbook.dto.response.EventStatsResponse
import ar.edu.unq.ttip.sportbook.exception.BadRequestException
import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.exception.UnauthorizedException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.FinishedEventStatsRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.SportUserJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.TeamJpaRepository
import ar.edu.unq.ttip.sportbook.service.event_stats.EventStatsCalculator
import ar.edu.unq.ttip.sportbook.service.event_stats.EventStatsMapper
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class EventService(
    val eventJpaRepository: EventJpaRepository,
    val lineupService: LineupService,
    val finishedEventStatsRepository: FinishedEventStatsRepository,
    val teamRepository: TeamJpaRepository,
    val userRepository: SportUserJpaRepository,
    private val calculator: EventStatsCalculator,
    private val mapper: EventStatsMapper,
) {

    @Transactional
    fun createEvent(event: Event, sportUser: SportUser): Event {
        event.organizer = sportUser
        val savedEvent = eventJpaRepository.save(event)

        lineupService.createLineups(savedEvent)

        return savedEvent
    }

    fun getEvent(id: Long): Event =
        eventJpaRepository.findById(id)
            .orElseThrow { NotFoundException("Evento no encontrado") }

    fun getAllEvents(): List<Event> {
        return eventJpaRepository.findByIsFinishedFalse()
    }

    fun join(id: Long, user: SportUser) : Event = eventJpaRepository.findById(id)
        .map { joinEvent(user, it) }
        .orElseThrow { NotFoundException("Evento no encontrado") }

    @Transactional
    fun joinTeam(eventId: Long, teamId: Long, user: SportUser): Event {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { NotFoundException("Evento no encontrado") }

        val team = event.teams.find { it.id == teamId }
            ?: throw NotFoundException("Equipo no encontrado")


        var player = event.unnasignedPlayers.find { it.user?.id == user.id }


        if (player == null) {
            player = event.teams.flatMap { it.players }.find { it.user?.id == user.id }
        }

        if (player == null) {
            throw BusinessException("No estás registrado en este evento. Únete primero.")
        }


        player.joinTeam(event, team)



        if (event.sport == Sport.FOOTBALL) {
            lineupService.joinLineup(player, team, event)
        }

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

    @Transactional
    fun leaveEvent(eventId: Long, user: SportUser): Event {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { NotFoundException("Evento no encontrado") }


        val player = event.leave(user)


        lineupService.removePlayerFromLineups(event, player)





        return event
    }

    @Transactional
    fun updateEvent(id: Long, updateRequest: UpdateEventRequest): Event {
        val event = eventJpaRepository.findById(id)
            .orElseThrow { NotFoundException("Evento no encontrado") }


        val organizer = if (updateRequest.organizerId != null) {
            userRepository.findById(updateRequest.organizerId).orElseThrow()
        } else {
            event.organizer
        }


        val parsedDateTime = updateRequest.dateTime?.let {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                LocalDateTime.parse(it, formatter)
            } catch (e: Exception) {

                LocalDateTime.parse(it)
            }
        }


        event.updateBasicFields(
            cost = updateRequest.cost,
            organizer = organizer,
            name = updateRequest.name,
            newDateTime = parsedDateTime,
            minPlayers = updateRequest.minPlayers,
            maxPlayers = updateRequest.maxPlayers
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

        updateRequest.pitchSize?.let { event.updatePitchSize(it) }

        return eventJpaRepository.save(event)
    }

    @Transactional
    fun finishEvent(eventId: Long, finishEventData: FinishEventRequest, sportUser: SportUser): FinishedEventStats {
        val event = getEvent(eventId)
        if (event.organizer != sportUser) {
            throw UnauthorizedException("Solo el organizador del evento puede finalizarlo")
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

    fun getFinishedEvents(): List<Event> = eventJpaRepository.findByIsFinishedTrue()
    fun addTeam(eventId: Long, team: Team, sportUser: SportUser): Event {
        val event = getEvent(eventId)
        if (event.organizer != sportUser) {
            throw UnauthorizedException("Solo el organizador del evento puede agregar equipos")
        }
        event.addTeam(team)
        return eventJpaRepository.save(event)
    }

    fun removeTeam(eventId: Long, teamId: Long, sportUser: SportUser): Event {
        val event = getEvent(eventId)
        if (event.organizer != sportUser) {
            throw UnauthorizedException("Solo el organizador del evento puede remover equipos")
        }
        val team = teamRepository.findById(teamId).orElseThrow { NotFoundException("El equipo no existe") }
        event.removeTeam(team)
        teamRepository.delete(team)
        return eventJpaRepository.findById(eventId).orElseThrow()
    }
}