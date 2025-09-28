package ar.edu.unq.ttip.sportbook.persistence.entity.user

import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyProfileDetail
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "SPORT_PROFILE")
class SportProfile(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    var user: SportUser,

    @Enumerated(EnumType.STRING)
    var sport: Sport,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var details: SportProfileDetail
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun updateDetails(newDetails: Any) {
        when {
            this.sport == Sport.FOOTBALL && newDetails is FootballProfileDetail -> this.details = newDetails
            this.sport == Sport.VOLLEY   && newDetails is VolleyProfileDetail -> this.details = newDetails
            this.sport == Sport.PADDLE   && newDetails is PaddleProfileDetail -> this.details = newDetails
            else -> throw BusinessException("Tipo de detalle incompatible con el deporte ${this.sport}")
        }
    }
}
