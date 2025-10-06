package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LineupRepository : JpaRepository<Lineup, Long> {
    fun findByEvent(event: Event): List<Lineup>
}
