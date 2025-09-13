package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.NoSuchElementException

@Service
class EventService(
    val eventJpaRepository: EventJpaRepository,
    val playerJpaRepository: PlayerJpaRepository,
    val footballLineupService: FootballLineupService
) {

    @Transactional
    fun createEvent(event: Event) : Event {
        val savedEvent = eventJpaRepository.save(event)

        if (savedEvent is FootballEvent) {
            savedEvent.firstTeam?.let { team ->
                val lineup = footballLineupService.createLineup(savedEvent, team)
                team.players.forEach { player ->
                    lineup.addPlayerToBench(player)
                }
                footballLineupService.save(lineup)
            }

            savedEvent.secondTeam?.let { team ->
                val lineup = footballLineupService.createLineup(savedEvent, team)
                team.players.forEach { player ->
                    lineup.addPlayerToBench(player)
                }
                footballLineupService.save(lineup)
            }
        }

        return savedEvent
    }

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

    fun joinTeam(eventId: Long, teamId: Long, user: SportUser) : Event {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { NoSuchElementException("Evento no encontrado") }
        val player = playerJpaRepository.findByUserUsername(user.username!!)
            .orElseThrow { NoSuchElementException("Jugador no encontrado") }

        player.joinTeam(event, teamId)

        return eventJpaRepository.save(event)
    }

    private fun joinEvent(
        user: SportUser,
        event: Event
    ): Event {
        val player = playerJpaRepository.findByUserUsername(user.username!!).orElse(Player(name = user.name!!, user = user))
        event.join(player)
        eventJpaRepository.save(event)
        return event
    }

    fun leaveEvent(eventId: Long, user: SportUser) : Event {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { NoSuchElementException("Evento no encontrado") }
        val player = playerJpaRepository.findByUserUsername(user.username!!)
            .orElseThrow { NoSuchElementException("Jugador no encontrado") }

        event.leave(player)
        return eventJpaRepository.save(event)
    }
}