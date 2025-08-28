package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.controller.dto.CreateEventRequestBody
import ar.edu.unq.ttip.sportbook.controller.dto.CreateEventResponseBody
import ar.edu.unq.ttip.sportbook.controller.dto.ErrorBody
import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.service.EventService
import ar.edu.unq.ttip.sportbook.service.error.ErrorCode
import ar.edu.unq.ttip.sportbook.util.Either
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/events", "/event"])
@CrossOrigin(origins = ["http://localhost:5173"])
class EventController(val eventService: EventService) {

    @PostMapping
    fun createEvent(@RequestBody eventBody: CreateEventRequestBody): ResponseEntity<Any> {
        val res = eventService.createEvent(eventBody)
        return when (res) {
            is Either.Left -> ResponseEntity.status(HttpStatus.CREATED).body(CreateEventResponseBody.fromEvent(res.value))
            is Either.Right-> ResponseEntity.internalServerError().body(ErrorBody(res.value.getCodeMessage()))
        }
    }

    @GetMapping("/{id}")
    fun getEvent(@PathVariable("id") id: Long) : ResponseEntity<Event?> {
        val event = eventService.getEvent(id)
        return event
            .map { ResponseEntity.ok(it) }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    @GetMapping
    fun getAllEvents(): ResponseEntity<List<Event>> {
        val events = eventService.getAllEvents()
        return ResponseEntity.ok(events)
    }

    @PutMapping("/{id}/join")
    fun join(@PathVariable("id") id: Long, @RequestParam("username") username: String): ResponseEntity<Any> {
        // IMPORTANT: el parametro de username es provisorio, mas adelante se cambia por un JWT del cual se sacaran los datos del usuario loggeado
        val response = eventService.join(id, username)
        return when (response) {
            is Either.Left -> ResponseEntity.ok().body(response.value)
            is Either.Right ->
                when (response.value.code) {
                    ErrorCode.NOT_FOUND -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ErrorBody("Evento con id $id no encontrado"))
                    ErrorCode.BUSINESS_ERROR -> ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ErrorBody(response.value.getCodeMessage()))
                    ErrorCode.INTERNAL_ERROR -> ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ErrorBody(response.value.getCodeMessage()))
                }
        }
    }
}