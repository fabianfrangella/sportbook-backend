package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.Event
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class EventService(
    val eventJpaRepository: EventJpaRepository,
    val playerJpaRepository: PlayerJpaRepository) {

    fun createEvent(event: Event) : Event = eventJpaRepository.save(event)

    fun getEvent(id: Long): Event {
        return eventJpaRepository
            .findById(id)
            .orElseThrow {  ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado") }
    }

    fun getAllEvents(): List<Event> {
        return eventJpaRepository.findAll()
    }

    fun join(id: Long, username: String) : Event = eventJpaRepository.findById(id)
        .map { joinEvent(username, it) }
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado") }

    private fun joinEvent(
        username: String,
        event: Event
    ): Event {
        val player = playerJpaRepository.findByUserUsername(username)
        event.join(player.get())
        eventJpaRepository.save(event)
        return event
    }
}