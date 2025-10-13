package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventJpaRepository : JpaRepository<Event, Long> {
    fun findByIsFinishedTrue(): List<Event>
    fun findByIsFinishedFalse(): List<Event>
}
