package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportProfile
import org.springframework.data.jpa.repository.JpaRepository

interface SportProfileRepository : JpaRepository<SportProfile, Long> {
    fun findByUserIdAndSport(userId: Long, sport: Sport): SportProfile?
    fun findAllByUserId(userId: Long): List<SportProfile>
}
