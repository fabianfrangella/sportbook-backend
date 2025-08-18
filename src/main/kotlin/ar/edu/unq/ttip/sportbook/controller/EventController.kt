package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.controller.dto.CreateEventRequestBody
import ar.edu.unq.ttip.sportbook.persistence.entity.EventJPA
import ar.edu.unq.ttip.sportbook.service.EventService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/event")
class EventController(val eventService: EventService) {

    @PostMapping
    fun createEvent(@RequestBody eventBody: CreateEventRequestBody): EventJPA {
        return eventService.createEvent(eventBody)
    }
}