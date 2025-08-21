package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.dto.CreateEventRequestBody
import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.util.Either
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class EventService(val eventJpaRepository: EventJpaRepository) {

    fun createEvent(eventRequestBody: CreateEventRequestBody) : Either<Event, String> {
        val event = eventRequestBody.toModel()
        try {
            val eventJPA = eventJpaRepository.save(event.toEntity())
            return Either.Left(eventJPA.toModel())
        } catch(ex: Exception) {
            return Either.Right("Error creating event: ${ex.message}")
        }
    }

    fun getEvent(id: Long): Optional<Event> {
        return eventJpaRepository.findById(id).map { it.toModel() }
    }
}