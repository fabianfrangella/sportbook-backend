package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerJpaRepository : JpaRepository<PlayerJPA, Long> {}