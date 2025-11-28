package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.dto.request.FinishEventRequest
import ar.edu.unq.ttip.sportbook.dto.request.UpdateEventRequest
import ar.edu.unq.ttip.sportbook.dto.response.EventStatsResponse
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Position
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Role
import ar.edu.unq.ttip.sportbook.security.UserDetailsImpl
import ar.edu.unq.ttip.sportbook.security.annotation.PermittedRoles
import ar.edu.unq.ttip.sportbook.service.EventService
import ar.edu.unq.ttip.sportbook.service.FairnessService
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
    val lineupService: LineupService,
    val fairnessService: FairnessService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Crear evento",
        method = "POST",
        description = "Crea un nuevo evento y devuelve el evento creado."
    )
    @PermittedRoles(roles = [Role.ORGANIZER])
    fun createEvent(@RequestBody eventBody: Event,
                          @AuthenticationPrincipal user: UserDetailsImpl): Event =
        eventService.createEvent(eventBody, user.sportUser)

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
    @PermittedRoles(roles = [Role.ORGANIZER])
    fun addPlayerToPosition(
        @PathVariable("lineupId") lineupId: Long,
        @RequestParam position: Position,
        @RequestParam playerId: Long,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): Lineup =
        lineupService.addPlayerToPosition(lineupId, playerId, position, user.sportUser)

    @DeleteMapping("/lineup/{lineupId}/position")
    @Operation(
        summary = "Quitar jugador de una posición",
        method = "DELETE",
        description = "Quita el jugador asignado a la posición indicada dentro de una alineación."
    )
    @PermittedRoles(roles = [Role.ORGANIZER])
    fun removePlayerFromPosition(
        @PathVariable("lineupId") lineupId: Long,
        @RequestParam position: Position,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): Lineup =
        lineupService.removePlayerFromPosition(lineupId, position, user.sportUser)

    @PostMapping("/{eventId}/lineups/auto")
    @Operation(summary = "Generar táctica automática")
    @PermittedRoles(roles = [Role.ORGANIZER])
    fun autoGenerateLineups(
        @PathVariable eventId: Long,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): List<Lineup> {
        return lineupService.autoConfigureLineups(eventId, user.sportUser)
    }

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
    @PermittedRoles(roles = [Role.ORGANIZER])
    fun finishEvent(
        @PathVariable eventId: Long,
        @RequestBody finishEventData: FinishEventRequest,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): FinishedEventStats =
        eventService.finishEvent(eventId, finishEventData, user.sportUser)

    @GetMapping("/{eventId}/stats")
    @Operation(
        summary = "Obtener estadísticas del evento",
        method = "GET",
        description = "Devuelve las estadísticas de un evento finalizado."
    )
    fun getEventStats(
        @PathVariable eventId: Long,
    ): EventStatsResponse = eventService.getStats(eventId)

    @GetMapping("/{eventId}/fairness-score")
    @Operation(
        summary = "Obtener fairness score de un evento",
        method = "GET",
        description = "Devuelve el fairness score de un evento."
    )
    fun getFairnessScore(@PathVariable eventId: Long): Int = fairnessService.getFairnessScore(eventId)

    @PostMapping("/{eventId}/balance")
    @Operation(
        summary = "Balancear evento",
        method = "POST",
        description = "Arma los equipos de un evento de manera balanceada."
    )
    @PermittedRoles(roles = [Role.ORGANIZER])
    fun balance(@PathVariable eventId: Long, @AuthenticationPrincipal user: UserDetailsImpl) = fairnessService.balance(eventId, user.sportUser)

    @GetMapping("/finished")
    @Operation(
        summary = "Obtener los eventos finalizados",
        method = "GET",
        description = "Devuelve los eventos finalizados"
    )
    fun getFinishedEvents(): List<Event> = eventService.getFinishedEvents()

    @PutMapping("/{eventId}/add-team")
    @Operation(
        summary = "Agregar equipo a un evento",
        method = "PUT",
        description = "Agrega un equipo a un evento existente."
    )
    @PermittedRoles(roles = [Role.ORGANIZER])
    fun addTeam(
        @PathVariable eventId: Long,
        @RequestBody team: Team,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): Event = eventService.addTeam(eventId, team, user.sportUser)

    @DeleteMapping("/{eventId}/remove-team/{teamId}")
    @Operation(
        summary = "Remover equipo de un evento",
        method = "DELETE",
        description = "Remueve un equipo de un evento existente."
    )
    @PermittedRoles(roles = [Role.ORGANIZER])
    fun removeTeam(
        @PathVariable eventId: Long,
        @PathVariable teamId: Long,
        @AuthenticationPrincipal user: UserDetailsImpl
    ): Event = eventService.removeTeam(eventId, teamId, user.sportUser)

}
