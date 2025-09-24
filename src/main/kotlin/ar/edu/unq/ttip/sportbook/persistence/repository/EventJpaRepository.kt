package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

@Repository
interface EventJpaRepository : JpaRepository<Event, Long> {
    @Modifying
    @Query("""
        UPDATE Event e 
        SET e.cost = CASE WHEN :cost IS NULL THEN e.cost ELSE :cost END,
            e.creator = CASE WHEN :creator IS NULL THEN e.creator ELSE :creator END,
            e.organizer = CASE WHEN :organizer IS NULL THEN e.organizer ELSE :organizer END
        WHERE e.id = :eventId
    """)
    fun updateEventFields(
        @Param("eventId") eventId: Long,
        @Param("cost") cost: BigDecimal?,
        @Param("creator") creator: String?,
        @Param("organizer") organizer: String?
    )

    @Modifying
    @Query("""
        UPDATE Location l 
        SET l.x = CASE WHEN :x IS NULL THEN l.x ELSE :x END,
            l.y = CASE WHEN :y IS NULL THEN l.y ELSE :y END,
            l.placeName = CASE WHEN :placeName IS NULL THEN l.placeName ELSE :placeName END
        WHERE l.id = (SELECT e.location.id FROM Event e WHERE e.id = :eventId)
    """)
    fun updateLocation(
        @Param("eventId") eventId: Long,
        @Param("x") x: String?,
        @Param("y") y: String?,
        @Param("placeName") placeName: String?
    )

    @Modifying
    @Query("""
        UPDATE TransferData t 
        SET t.cbu = CASE WHEN :cbu IS NULL THEN t.cbu ELSE :cbu END,
            t.alias = CASE WHEN :alias IS NULL THEN t.alias ELSE :alias END
        WHERE t.id = (SELECT e.transferData.id FROM Event e WHERE e.id = :eventId)
    """)
    fun updateTransferData(
        @Param("eventId") eventId: Long,
        @Param("cbu") cbu: String?,
        @Param("alias") alias: String?
    )

    @Modifying
    @Query("""
        UPDATE FootballEvent e 
        SET e.pitchSize = CASE WHEN :pitchSize IS NULL THEN e.pitchSize ELSE :pitchSize END
        WHERE e.id = :eventId
    """)
    fun updateFootballEventFields(
        @Param("eventId") eventId: Long,
        @Param("pitchSize") pitchSize: Int?
    )
}
