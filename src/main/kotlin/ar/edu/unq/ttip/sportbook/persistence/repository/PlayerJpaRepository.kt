package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PlayerJpaRepository : JpaRepository<Player, Long> {
    fun findByUserUsernameAndEventId(username: String, eventId: Long) : Optional<Player>
}