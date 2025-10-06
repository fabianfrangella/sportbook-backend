package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.controller.request.FinishEventRequest
import ar.edu.unq.ttip.sportbook.controller.request.UpdateEventRequest
import ar.edu.unq.ttip.sportbook.controller.response.EventStatsResponse
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Position
import ar.edu.unq.ttip.sportbook.security.UserDetailsImpl
import ar.edu.unq.ttip.sportbook.service.EventService
import ar.edu.unq.ttip.sportbook.service.LineupService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.ResponseStatus

@RestController
@RequestMapping(value = ["/event"])
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(name = "Eventos", description = "Endpoints para gestionar eventos deportivos")
class EventController(
    val eventService: EventService,
    val lineupService: LineupService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Crear evento",
        method = "POST",
        description = "Crea un nuevo evento y devuelve el evento creado."
    )
    fun createEvent(@RequestBody eventBody: Event): Event =
        eventService.createEvent(eventBody)

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener evento por ID",
        method = "GET",
        description = "Devuelve el detalle de un evento existente por su identificador."
    )
    fun getEvent(@PathVariable("id") id: Long): Event =
        eventService.getEvent(id)

    @GetMapping
    @Operation(
        summary = "Listar eventos",
        method = "GET",
        description = "Devuelve el listado completo de eventos."
    )
    fun getAllEvents(): List<Event> =
        eventService.getAllEvents()

    @PutMapping("/{id}/join")
    @Operation(
        summary = "Unirse a un evento",
        method = "PUT",
        description = "El usuario autenticado se une al evento indicado."
    )
    fun join(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): Event = eventService.join(id, user.sportUser)

    @PutMapping("/{id}/join/{teamId}")
    @Operation(
        summary = "Unirse a un equipo del evento",
        method = "PUT",
        description = "El usuario autenticado se une al equipo indicado dentro del evento."
    )
    fun joinTeam(
        @PathVariable("id") id: Long,
        @PathVariable("teamId") teamId: Long,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): Event = eventService.joinTeam(id, teamId, user.sportUser)

    @DeleteMapping("/{id}/leave")
    @Operation(
        summary = "Salir de un evento",
        method = "DELETE",
        description = "El usuario autenticado abandona el evento indicado."
    )
    fun leaveEvent(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): Event = eventService.leaveEvent(id, user.sportUser)

    @GetMapping("/{eventId}/lineup")
    @Operation(
        summary = "Obtener alineaciones del evento",
        method = "GET",
        description = "Devuelve las alineaciones (lineups) del evento indicado."
    )
    fun getEventLineups(@PathVariable("eventId") eventId: Long): List<Lineup> {
        val footballEvent = eventService.getEvent(eventId)
        return lineupService.getEventLineups(footballEvent)
    }

    @PutMapping("/lineup/{lineupId}/position")
    @Operation(
        summary = "Agregar jugador a una posición",
        method = "PUT",
        description = "Agrega un jugador a la posición indicada dentro de una alineación."
    )
    fun addPlayerToPosition(
        @PathVariable("lineupId") lineupId: Long,
        @RequestParam position: Position,
        @RequestParam playerId: Long
    ): Lineup =
        lineupService.addPlayerToPosition(lineupId, playerId, position)

    @DeleteMapping("/lineup/{lineupId}/position")
    @Operation(
        summary = "Quitar jugador de una posición",
        method = "DELETE",
        description = "Quita el jugador asignado a la posición indicada dentro de una alineación."
    )
    fun removePlayerFromPosition(
        @PathVariable("lineupId") lineupId: Long,
        @RequestParam position: Position
    ): Lineup =
        lineupService.removePlayerFromPosition(lineupId, position)

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar evento",
        method = "PUT",
        description = "Actualiza los datos de un evento existente."
    )
    fun updateEvent(
        @PathVariable("id") id: Long,
        @RequestBody updateRequest: UpdateEventRequest
    ): Event = eventService.updateEvent(id, updateRequest)

    @PostMapping("/{eventId}/finish")
    @Operation(
        summary = "Finalizar evento",
        method = "POST",
        description = "Marca el evento como finalizado y devuelve estadísticas del partido."
    )
    fun finishEvent(
        @PathVariable eventId: Long,
        @RequestBody finishEventData: FinishEventRequest
    ): FinishedEventStats =
        eventService.finishEvent(eventId, finishEventData)

    @GetMapping("/{eventId}/stats")
    @Operation(
        summary = "Obtener estadísticas del evento",
        method = "GET",
        description = "Devuelve las estadísticas de un evento finalizado."
    )
    fun getEventStats(
        @PathVariable eventId: Long,
    ): EventStatsResponse = eventService.getStats(eventId)
}
