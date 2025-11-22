package ar.edu.unq.ttip.sportbook.persistence.entity.event.volley

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "VOLLEY_EVENT")
class VolleyEvent : Event() {
    init { this.sport = Sport.VOLLEY }

    override fun createLineups(): List<Lineup> {
        return emptyList()
    }

    override fun updatePitchSize(size: Int) {
        // TODO: averiguar de a cuantos jugadores se puede jugar al volley y acomodar un pitchSize para ello, si es que tiene sentido
    }

}