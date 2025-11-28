package ar.edu.unq.ttip.sportbook.persistence.entity.user

import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyProfileDetail
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = FootballProfileDetail::class, name = "FOOTBALL"),
    JsonSubTypes.Type(value = PaddleProfileDetail::class, name = "PADDLE"),
    JsonSubTypes.Type(value = VolleyProfileDetail::class, name = "VOLLEY")
)
// ---------------------------------------
abstract class SportProfileDetail() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Enumerated(EnumType.STRING)
    var playsOften: PlayFrequency? = null
    var ability: Int? = null

    constructor(playsOften: PlayFrequency?, ability: Int?) : this() {
        this.playsOften = playsOften
        this.ability = ability
    }
}