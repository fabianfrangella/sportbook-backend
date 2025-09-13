package ar.edu.unq.ttip.sportbook.controller


import ar.edu.unq.ttip.sportbook.persistence.entity.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.FootballLineup
import ar.edu.unq.ttip.sportbook.persistence.entity.Position
import ar.edu.unq.ttip.sportbook.security.UserDetailsImpl
import ar.edu.unq.ttip.sportbook.service.EventService
import ar.edu.unq.ttip.sportbook.service.FootballLineupService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/event"])
@CrossOrigin(origins = ["http://localhost:5173"])
class EventController(
    val eventService: EventService,
    val footballLineupService: FootballLineupService
) {

    @PostMapping
    fun createEvent(@RequestBody eventBody: Event): ResponseEntity<Any> {
        val res = eventService.createEvent(eventBody)
        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }

    @GetMapping("/{id}")
    fun getEvent(@PathVariable("id") id: Long) : ResponseEntity<Event> {
        val event = eventService.getEvent(id)
        return ResponseEntity.ok(event)

    }

    @GetMapping
    fun getAllEvents(): ResponseEntity<List<Event>> {
        val events = eventService.getAllEvents()
        return ResponseEntity.ok(events)
    }

    @PutMapping("/{id}/join")
    fun join(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: UserDetailsImpl): ResponseEntity<Event> {
        return ResponseEntity.ok(eventService.join(id, user.sportUser))
    }

    @PutMapping("/{id}/join/{teamId}")
    fun joinTeam(
        @PathVariable("id") id: Long,
        @PathVariable("teamId") teamId: Long,
        @AuthenticationPrincipal user: UserDetailsImpl): ResponseEntity<Event> {
        return ResponseEntity.ok(eventService.joinTeam(id, teamId, user.sportUser))
    }

    @DeleteMapping("/{id}/leave")
    fun leaveEvent(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: UserDetailsImpl): ResponseEntity<Event> {
        return ResponseEntity.ok(eventService.leaveEvent(id, user.sportUser))
    }

    @PostMapping("/{eventId}/team/{teamId}/lineup")
    fun createLineup(
        @PathVariable("eventId") eventId: Long,
        @PathVariable ("teamId") teamId: Long
    ): ResponseEntity<FootballLineup> {
        val event = eventService.getEvent(eventId) as? FootballEvent
            ?: return ResponseEntity.badRequest().build()

        val team = when (teamId) {
            event.firstTeam?.id -> event.firstTeam
            event.secondTeam?.id -> event.secondTeam
            else -> null
        } ?: return ResponseEntity.badRequest().build()

        val lineup = footballLineupService.createLineup(event, team)
        return ResponseEntity.status(HttpStatus.CREATED).body(lineup)
    }

    @GetMapping("/{eventId}/lineup")
    fun getEventLineups(@PathVariable("eventId") eventId: Long): ResponseEntity<List<FootballLineup>> {
        val event = eventService.getEvent(eventId) as? FootballEvent
            ?: return ResponseEntity.badRequest().build()

        val lineups = footballLineupService.getEventLineups(event)
        return ResponseEntity.ok(lineups)
    }

    @PutMapping("/lineup/{lineupId}/position")
    fun addPlayerToPosition(
        @PathVariable("lineupId") lineupId: Long,
        @RequestParam position: Position,
        @RequestParam playerId: Long
    ): ResponseEntity<FootballLineup> {
        val lineup = footballLineupService.addPlayerToPosition(lineupId, playerId, position)
        return ResponseEntity.ok(lineup)
    }

    @DeleteMapping("/lineup/{lineupId}/position")
    fun removePlayerFromPosition(
        @PathVariable("lineupId") lineupId: Long,
        @RequestParam position: Position
    ): ResponseEntity<FootballLineup> {
        val lineup = footballLineupService.removePlayerFromPosition(lineupId, position)
        return ResponseEntity.ok(lineup)
    }
}