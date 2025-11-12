package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.request.FinishEventRequest
import ar.edu.unq.ttip.sportbook.controller.request.UpdateEventRequest
import ar.edu.unq.ttip.sportbook.controller.response.*
import ar.edu.unq.ttip.sportbook.exception.BadRequestException
import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.exception.UnauthorizedException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.FinishedEventStatsRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.SportUserJpaRepository
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
    val userRepository: SportUserJpaRepository,
    private val calculator: EventStatsCalculator,
    private val mapper: EventStatsMapper,
) {

    @Transactional
    fun createEvent(event: Event, sportUser: SportUser): Event {
        // 1. **Manejar la bidireccionalidad antes del guardado.**
        //    Usamos 'apply' en el bucle para establecer el 'event' en cada jugador
        //    y ejecutar la lógica de validación/conversión.

        // 2. **Refactorizar el bucle:** Usar forEach para aplicar cambios directamente
        //    a la lista de jugadores recibida, eliminando el .map y la recreación de la lista.
        event.players.forEach { player ->

            // La validación interna del modelo debe estar en el modelo (ver punto 2).
            // Por ahora, usamos el código existente para la corrección inmediata:

            // --- Lógica de validación/conversión (a refactorizar) ---
            if (player.sportUsername != null) {
                val foundedUser = userRepository.findByUsername(player.sportUsername!!).orElse(null)

                if (foundedUser != null) {
                    player.user = foundedUser
                    player.name = foundedUser.name // 💡 CORRECCIÓN: Usar el nombre del usuario
                } else {
                    player.user = null
                    if (player.name.isNullOrBlank()) {
                        player.name = player.sportUsername!!
                    }
                    player.sportUsername = null // Limpiar el campo transitorio
                }
            } else if (player.name.isNullOrBlank()) {
                throw IllegalArgumentException("El jugador debe tener un ID de usuario o un nombre de invitado.")
            }
            // --- Fin de lógica de validación/conversión ---

            // Establecer la bidireccionalidad. ESTO ES CRUCIAL.
            player.event = event
        }

        // 3. **Simplificar el guardado:** La lista ya está modificada.
        //    Usamos el `apply` para configurar el organizador ANTES de guardar.
        event.organizer = sportUser
        val savedEvent = eventJpaRepository.save(event)

        // 4. Mover la creación de alineaciones al final, después de guardar el evento
        //    para asegurar que el evento tenga su ID persistido.
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

    fun joinTeam(eventId: Long, teamId: Long, user: SportUser) : Event {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { NotFoundException("Evento no encontrado") }
        val player = playerJpaRepository.findByUserUsernameAndEventId(user.username!!, eventId)
            .orElseThrow { NotFoundException("Jugador no encontrado") }
        val team = teamRepository.findById(teamId)
            .orElseThrow { NotFoundException("Equipo no encontrado") }
        player.joinTeam(event, team)

        lineupService.joinLineup(player, team, event)
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
        val organizer = if (updateRequest.organizer != null) {
            userRepository.findByUsername(updateRequest.organizer).orElseThrow()
        } else {
            event.organizer
        }

        event.updateBasicFields(
            updateRequest.cost,
            organizer
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