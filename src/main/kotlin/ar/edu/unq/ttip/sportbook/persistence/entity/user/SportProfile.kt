package ar.edu.unq.ttip.sportbook.persistence.entity.user

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "SPORT_PROFILE")
class SportProfile() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    var user: SportUser? = null

    @Enumerated(EnumType.STRING)
    var sport: Sport? = null

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "detail_id")
    var details: SportProfileDetail? = null

    constructor(user: SportUser, sport: Sport, details: SportProfileDetail) : this() {
        this.user = user
        this.sport = sport
        this.details = details
    }

    fun updateDetails(newDetails: SportProfileDetail) {
        this.details = newDetails
    }
}