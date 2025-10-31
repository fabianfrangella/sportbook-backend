package ar.edu.unq.ttip.sportbook.persistence.entity.user

import ar.edu.unq.ttip.sportbook.config.SportProfileDetailDeserializer
import ar.edu.unq.ttip.sportbook.config.SportProfileDetailSerializer
import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyProfileDetail
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import jakarta.persistence.*

@Entity
@Table(name = "SPORT_PROFILE")
class SportProfile() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    var user: SportUser? = null

    @Enumerated(EnumType.STRING)
    var sport: Sport? = null

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonSerialize(using = SportProfileDetailSerializer::class)
    @JsonDeserialize(using = SportProfileDetailDeserializer::class)
    var details: SportProfileDetail? = null

    constructor(user: SportUser?, sport: Sport?, details: SportProfileDetail?) : this() {
        this.user = user
        this.sport = sport
        this.details = details
    }

    fun updateDetails(newDetails: Any) {
        when {
            this.sport == Sport.FOOTBALL && newDetails is FootballProfileDetail -> this.details = newDetails
            this.sport == Sport.VOLLEY   && newDetails is VolleyProfileDetail -> this.details = newDetails
            this.sport == Sport.PADDLE   && newDetails is PaddleProfileDetail -> this.details = newDetails
            else -> throw BusinessException("Tipo de detalle incompatible con el deporte ${this.sport}")
        }
    }
}
