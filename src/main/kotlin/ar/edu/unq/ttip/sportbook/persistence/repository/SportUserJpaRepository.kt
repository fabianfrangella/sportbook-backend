package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.SportUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SportUserJpaRepository : JpaRepository<SportUser, Long> {
    fun findByUsername(username: String) : Optional<SportUser>
}