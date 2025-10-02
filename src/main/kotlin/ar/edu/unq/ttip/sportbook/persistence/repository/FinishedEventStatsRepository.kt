package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FinishedEventStatsRepository : JpaRepository<FinishedEventStats, Long> {

    @Query("""
        select fes 
        from FinishedEventStats fes
        join fetch fes.event e
        where e.sport = :sport
    """)
    fun findAllBySport(@Param("sport") sport: Sport): List<FinishedEventStats>

    @Query("""
        select fes
        from FinishedEventStats fes
        join fes.event e
        join e.players p
        where p.user.id = :userId
    """)
    fun findAllByUserId(@Param("userId") userId: Long): List<FinishedEventStats>

    @Query("""
        select fes
        from FinishedEventStats fes
        join fes.event e
        join e.players p
        where p.user.id = :userId and e.sport = :sport
    """)
    fun findAllByUserIdAndSport(@Param("userId") userId: Long, @Param("sport") sport: Sport): List<FinishedEventStats>

    @Query("""
        select fes
        from FinishedEventStats fes
        join fetch fes.goals g
        join fetch g.team t
        join fetch g.player p
        join fetch p.user u
        where fes.event.id = :eventId
    """)
    fun findByEventIdWithGoals(@Param("eventId") eventId: Long): FinishedEventStats?

    // fallback liviano si no hay goles
    @Query("""
        select fes
        from FinishedEventStats fes
        where fes.event.id = :eventId
    """)
    fun findByEventId(@Param("eventId") eventId: Long): FinishedEventStats?
}
