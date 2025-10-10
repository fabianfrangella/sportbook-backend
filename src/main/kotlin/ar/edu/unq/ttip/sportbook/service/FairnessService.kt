package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import org.springframework.stereotype.Service

@Service
class FairnessService(val eventJpaRepository: EventJpaRepository) {

    fun getFairnessScore(eventId: Long) : Int {
        val event = eventJpaRepository.findById(eventId)
            .orElseThrow { Exception("Evento no encontrado") }
        return event.getFairnessScore().toInt()
    }
}