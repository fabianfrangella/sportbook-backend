package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.SportProfile
import ar.edu.unq.ttip.sportbook.persistence.entity.SportUser
import org.springframework.data.jpa.repository.JpaRepository

interface SportProfileRepository : JpaRepository<SportProfile, Long> {
    fun findByUserAndSport(user: SportUser, sport: Sport): SportProfile?
    fun findAllByUser(user: SportUser): List<SportProfile>
}
