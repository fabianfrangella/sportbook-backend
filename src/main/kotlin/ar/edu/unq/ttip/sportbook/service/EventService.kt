package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.dto.CreateEventRequestBody
import ar.edu.unq.ttip.sportbook.persistence.entity.EventJPA
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import org.springframework.stereotype.Service

@Service
class EventService(val eventJpaRepository: EventJpaRepository) {

    fun createEvent(eventRequestBody: CreateEventRequestBody) : EventJPA {
        val event = eventRequestBody.toModel()
        return eventJpaRepository.save(event.toEntity())
    }

}