package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.request.FinishEventRequest
import ar.edu.unq.ttip.sportbook.controller.request.UpdateEventRequest
import ar.edu.unq.ttip.sportbook.exception.BadRequestException
import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.entity.team.TeamGoal
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.PlayerJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.FinishedEventStatsRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.TeamJpaRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class EventService(
    val eventJpaRepository: EventJpaRepository,
    val playerJpaRepository: PlayerJpaRepository,
    val teamJpaRepository: TeamJpaRepository,
    val footballLineupService: FootballLineupService,
    val finishedEventStatsRepository: FinishedEventStatsRepository
) {

    @Transactional
    fun createEvent(event: Event): Event {
        val saved = eventJpaRepository.save(event)

        if (saved is FootballEvent) {
            val teams = listOfNotNull(saved.firstTeam, saved.secondTeam)
            if (teams.isEmpty()) {
                throw BadRequestException("El evento de fútbol debe tener al menos un equipo asignado")
            }

            teams.forEach { team -> createAndPopulateFootballLineup(saved, team) }
        }

        return saved
    }

    private fun createAndPopulateFootballLineup(event: FootballEvent, team: Team) {
        val lineup = footballLineupService.createLineup(event, team)

        val players: List<Player> = team.players
        val distinctCount = players.map { it.id }.toSet().size
        if (distinctCount != players.size) {
            throw BadRequestException("El equipo ${team.color} contiene jugadores duplicados")
        }

        players.forEach { player ->
            lineup.addPlayerToBench(player)
        }
    }

    fun getEvent(id: Long): Event =
        eventJpaRepository.findById(id)
            .orElseThrow { NotFoundException("Evento no encontrado") }

    fun getFootballEvent(id: Long): FootballEvent {
        val event = getEvent(id)
        if (event !is FootballEvent) {
            throw BadRequestException("El evento no es de fútbol")
        }
        return event
    }

    fun getAllEvents(): List<Event> {
        return eventJpaRepository.findAll()
    }

    fun join(id: Long, user: SportUser) : Event = eventJpaRepository.findById(id)
        .map { joinEvent(user, it) }
        .orElseThrow { NotFoundException("Evento no encontrado") }

    fun joinTeam(eventId: Long, teamId: Long, user: SportUser) : Event {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { NotFoundException("Evento no encontrado") }
        val player = playerJpaRepository.findByUserUsername(user.username!!)
            .orElseThrow { NotFoundException("Jugador no encontrado") }

        player.joinTeam(event, teamId)

        if (event is FootballEvent) {
            val lineups = footballLineupService.getEventLineups(event)
            lineups.forEach {
                if (it.team.id != teamId) {
                    it.removePlayer(player)
                } else {
                    it.addPlayerToBench(player)
                }
                footballLineupService.save(it)
            }
        }

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
            .orElseThrow { NotFoundException("Evento no encontrado") }
        val player = playerJpaRepository.findByUserUsername(user.username!!)
            .orElseThrow { NotFoundException("Jugador no encontrado") }

        event.leave(player)

        if (event is FootballEvent) {
            val lineups = footballLineupService.getEventLineups(event)
            lineups.forEach { lineup ->
                lineup.removePlayer(player)
                footballLineupService.save(lineup)
            }
        }

        return eventJpaRepository.save(event)
    }

    @Transactional
    fun updateEvent(id: Long, updateRequest: UpdateEventRequest): Event {
        val event = eventJpaRepository.findById(id)
            .orElseThrow { NotFoundException("Evento no encontrado") }

        event.updateBasicFields(
            updateRequest.cost,
            updateRequest.creator,
            updateRequest.organizer
        )

        if (updateRequest.locationX != null || updateRequest.locationY != null || updateRequest.locationPlaceName != null) {
            event.updateLocation(
                updateRequest.locationX,
                updateRequest.locationY,
                updateRequest.locationPlaceName
            )
        }

        if (updateRequest.transferDataCbu != null || updateRequest.transferDataAlias != null) {
            event.updateTransferData(
                updateRequest.transferDataCbu,
                updateRequest.transferDataAlias
            )
        }

        if (event is FootballEvent) {
            event.updatePitchSize(updateRequest.pitchSize)
        }

        return eventJpaRepository.save(event)
    }

    @Transactional
    fun finishEvent(eventId: Long, finishEventData: FinishEventRequest): FinishedEventStats {
        val event = getEvent(eventId)
        event.isFinished = true
        eventJpaRepository.save(event)

        val stats = FinishedEventStats()
        stats.event = event

        // Crear y asociar los goles
        stats.goals = finishEventData.goals.map { goalRequest ->
            TeamGoal().apply {
                team = teamJpaRepository.findById(goalRequest.teamId)
                    .orElseThrow { NotFoundException("Equipo ${goalRequest.teamId} no encontrado") }
                player = playerJpaRepository.findById(goalRequest.playerId)
                    .orElseThrow { NotFoundException("Jugador ${goalRequest.playerId} no encontrado") }
                finishedEventStats = stats
            }
        }.toMutableList()

        // Asociar equipo ganador
        stats.winningTeam = teamJpaRepository.findById(finishEventData.winningTeamId)
            .orElseThrow { NotFoundException("Equipo ganador ${finishEventData.winningTeamId} no encontrado") }

        // Asociar MVP
        stats.mvp = playerJpaRepository.findById(finishEventData.mvpId)
            .orElseThrow { NotFoundException("Jugador MVP ${finishEventData.mvpId} no encontrado") }

        // Asociar jugadores ausentes
        stats.missingPlayers = finishEventData.missingPlayerIds
            .mapTo(mutableSetOf()) { playerId ->
                playerJpaRepository.findById(playerId)
                    .orElseThrow { NotFoundException("Jugador ausente $playerId no encontrado") }
            }

        return finishedEventStatsRepository.save(stats)
    }
}