package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.EventJPA
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class EventService(
    val eventJpaRepository: EventJpaRepository,
    val playerJpaRepository: PlayerJpaRepository) {

    fun createEvent(event: EventJPA) : EventJPA = eventJpaRepository.save(event)

    fun getEvent(id: Long): EventJPA {
        return eventJpaRepository
            .findById(id)
            .orElseThrow {  ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado") }
    }

    fun getAllEvents(): List<EventJPA> {
        return eventJpaRepository.findAll()
    }

    fun join(id: Long, username: String) : EventJPA = eventJpaRepository.findById(id)
        .map { joinEvent(username, it) }
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado") }

    private fun joinEvent(
        username: String,
        event: EventJPA
    ): EventJPA {
        val player = playerJpaRepository.findByUserUsername(username)
        event.join(player.get())
        eventJpaRepository.save(event)
        return event
    }
}