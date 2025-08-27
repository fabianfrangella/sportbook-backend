package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.controller.dto.CreateEventRequestBody
import ar.edu.unq.ttip.sportbook.controller.dto.CreateEventResponseBody
import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.service.EventService
import ar.edu.unq.ttip.sportbook.util.BusinessResult
import ar.edu.unq.ttip.sportbook.util.Either
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/event")
class EventController(val eventService: EventService) {

    @PostMapping
    fun createEvent(@RequestBody eventBody: CreateEventRequestBody): ResponseEntity<Any> {
        val res = eventService.createEvent(eventBody)
        return when (res) {
            is Either.Left -> ResponseEntity.status(HttpStatus.CREATED).body(CreateEventResponseBody.fromEvent(res.value))
            is Either.Right-> ResponseEntity.internalServerError().body(res.value)
        }
    }

    @GetMapping("/{id}")
    fun getEvent(@PathVariable("id") id: Long) : ResponseEntity<Event?> {
        val event = eventService.getEvent(id)
        return event
            .map { ResponseEntity.ok(it) }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    @PutMapping("/{id}/join")
    fun join(@PathVariable("id") id: Long, @RequestParam("username") username: String): ResponseEntity<Any> {
        // IMPORTANT: el parametro de username es provisorio, mas adelante se cambia por un JWT del cual se sacaran los datos del usuario loggeado
        val response = eventService.join(id, username)
        return when (response) {
            is Either.Left -> ResponseEntity.ok().body(response.value)
            is Either.Right ->
                when (response.value) {
                    BusinessResult.NOT_FOUND -> ResponseEntity.notFound().build()
                    BusinessResult.ERROR -> ResponseEntity.internalServerError().body(response.value.fn("Ya estás en este evento"))
                }
        }
    }
}