package ar.edu.unq.ttip.sportbook.controller


import ar.edu.unq.ttip.sportbook.persistence.entity.EventJPA
import ar.edu.unq.ttip.sportbook.service.EventService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/event"])
@CrossOrigin(origins = ["http://localhost:5173"])
class EventController(val eventService: EventService) {

    @PostMapping
    fun createEvent(@RequestBody eventBody: EventJPA): ResponseEntity<Any> {
        val res = eventService.createEvent(eventBody)
        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }

    @GetMapping("/{id}")
    fun getEvent(@PathVariable("id") id: Long) : ResponseEntity<EventJPA> {
        val event = eventService.getEvent(id)
        return ResponseEntity.ok(event)

    }

    @GetMapping
    fun getAllEvents(): ResponseEntity<List<EventJPA>> {
        val events = eventService.getAllEvents()
        return ResponseEntity.ok(events)
    }

    @PutMapping("/{id}/join")
    fun join(@PathVariable("id") id: Long, @RequestParam("username") username: String): ResponseEntity<EventJPA> {
        // IMPORTANT: el parametro de username es provisorio, mas adelante se cambia por un JWT del cual se sacaran los datos del usuario loggeado
        return ResponseEntity.ok(eventService.join(id, username))
    }
}