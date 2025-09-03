package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.NoSuchElementException

@Service
class EventService(
    val eventJpaRepository: EventJpaRepository,
    val playerJpaRepository: PlayerJpaRepository) {

    fun createEvent(event: Event) : Event = eventJpaRepository.save(event)

    fun getEvent(id: Long): Event {
        return eventJpaRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Evento no encontrado") }
    }

    fun getAllEvents(): List<Event> {
        return eventJpaRepository.findAll()
    }

    fun join(id: Long, user: SportUser) : Event = eventJpaRepository.findById(id)
        .map { joinEvent(user, it) }
        .orElseThrow { NoSuchElementException("Evento no encontrado") }

    private fun joinEvent(
        user: SportUser,
        event: Event
    ): Event {
        val player = playerJpaRepository.findByUserUsername(user.username!!).orElse(Player(name = user.name!!, user = user))
        event.join(player)
        eventJpaRepository.save(event)
        return event
    }
}