package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.dto.CreateEventRequestBody
import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import ar.edu.unq.ttip.sportbook.util.Either
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class EventService(
    val eventJpaRepository: EventJpaRepository,
    val playerJpaRepository: PlayerJpaRepository) {

    fun createEvent(eventRequestBody: CreateEventRequestBody) : Either<Event, String> {
        val event = eventRequestBody.toModel()
        try {
            val players = findRegisteredPlayers(event)
            val eventJPA = eventJpaRepository.save(event.toEntity(players))
            return Either.Left(eventJPA.toModel())
        } catch(ex: Exception) {
            return Either.Right("Error creating event: ${ex.message}")
        }
    }

    private fun findRegisteredPlayers(event: Event): List<PlayerJPA> {
        return event.players
            .map { playerJpaRepository.findByUserUsername(it.user.userName) }
            .filter { it.isPresent }
            .map { it.get() }
    }

    fun getEvent(id: Long): Optional<Event> {
        return eventJpaRepository.findById(id).map { it.toModel() }
    }

    fun getAllEvents(): List<Event> {
        return eventJpaRepository.findAll().map { it.toModel() }
    }
}