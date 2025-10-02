package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballLineup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FootballLineupRepository : JpaRepository<FootballLineup, Long> {
    fun findByEvent(event: FootballEvent): List<FootballLineup>
}
