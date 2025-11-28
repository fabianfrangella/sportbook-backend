package ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Lineup
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "PADDLE_EVENT")
class PaddleEvent() : Event() {
    init { this.sport = Sport.PADDLE }

    override fun getScoreUnit(): String = "Sets"

    override fun createLineups(): List<Lineup> {
        return emptyList()
    }

    override fun updatePitchSize(size: Int) {
        // TODO: averiguar de a cuantos jugadores se puede jugar al paddle y acomodar un pitchSize para ello, si es que tiene sentido
    }

}