package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.exception.UnauthorizedException
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FairnessService(val eventJpaRepository: EventJpaRepository, val lineupService: LineupService) {

    fun getFairnessScore(eventId: Long) : Int {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { Exception("Evento no encontrado") }
        return event.getFairnessScore().toInt()
    }

    @Transactional
    fun balance(eventId: Long, sportUser: SportUser) {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { Exception("Evento no encontrado") }

        if (event.organizer != sportUser) {
            throw UnauthorizedException("Solo el organizador del evento puede balancear los equipos")
        }

        lineupService.deleteLineups(event)
        event.balanceTeams()
        lineupService.createLineups(event)
        eventJpaRepository.save(event)
    }
}