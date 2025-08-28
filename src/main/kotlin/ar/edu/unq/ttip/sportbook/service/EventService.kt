package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.dto.CreateEventRequestBody
import ar.edu.unq.ttip.sportbook.domain.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.EventJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import ar.edu.unq.ttip.sportbook.service.error.Error
import ar.edu.unq.ttip.sportbook.service.error.ErrorCode
import ar.edu.unq.ttip.sportbook.util.Either
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class EventService(
    val eventJpaRepository: EventJpaRepository,
    val playerJpaRepository: PlayerJpaRepository) {

    fun createEvent(eventRequestBody: CreateEventRequestBody) : Either<Event, Error> {
        val event = eventRequestBody.toModel()
        try {
            val players = findRegisteredPlayers(event)
            val eventJPA = eventJpaRepository.save(event.toEntity(players))
            return Either.Left(eventJPA.toModel())
        } catch(ex: Exception) {
            return Either.Right(Error(ErrorCode.INTERNAL_ERROR,"Error creating event: ${ex.message}"))
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

    fun join(id: Long, username: String) : Either<Event, Error> {
        return eventJpaRepository.findById(id)
            .map {
                val eitherCanJoin = it.toModel().canJoin(username)
                eitherCanJoin.map(
                    { canJoin ->
                        if (canJoin) {
                            joinEvent(username, it)
                        } else {
                            Either.Right(Error(ErrorCode.BUSINESS_ERROR, "Ya estás en este evento"))
                        }
                    },
                    { errorMessage -> Either.Right(Error(ErrorCode.BUSINESS_ERROR, errorMessage))
                    })
            }
            .orElseGet { Either.Right(Error(ErrorCode.NOT_FOUND, "Evento")) }
    }

    private fun joinEvent(
        username: String,
        event: EventJPA
    ): Either.Left<Event> {
        val player = playerJpaRepository.findByUserUsername(username)
        event.addPlayer(player.get())
        eventJpaRepository.save(event)
        return Either.Left(event.toModel())
    }
}