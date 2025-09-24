package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

@Repository
interface EventJpaRepository : JpaRepository<Event, Long> {}
