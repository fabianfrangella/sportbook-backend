package ar.edu.unq.ttip.sportbook.persistence.entity.user

import ar.edu.unq.ttip.sportbook.exception.BusinessException
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.persistence.FetchType
import java.time.LocalDate

@Entity
@Table(name = "SPORT_USER")
class SportUser() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @get:JsonIgnore
    @set:JsonProperty
    @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null

    final var username: String? = null
    final var email: String? = null
    final var name: String? = null
    final var lastName: String? = null

    @JsonFormat(pattern = "yyyy-MM-dd")
    var dateOfBirth: LocalDate? = null

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    var profiles: MutableList<SportProfile> = mutableListOf()

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @JsonIgnore
    var players: MutableList<Player> = mutableListOf()

    constructor(
        password: String,
        username: String,
        email: String,
        name: String,
        lastName: String,
        dateOfBirth: LocalDate
    ) : this() {
        this.password = password
        this.username = username
        this.email = email
        this.name = name
        this.lastName = lastName
        this.dateOfBirth = dateOfBirth
    }

    fun addProfile(profile: SportProfile) {
        val existing = profiles.firstOrNull { it.sport == profile.sport }
        if (existing != null) {
            throw BusinessException("El usuario ya tiene perfil para ${profile.sport}")
        }
        profiles.add(profile)
        profile.user = this // owning side
    }

    fun wasMvpInPastEvents(): Boolean {
        return players
            .mapNotNull { it.event?.finishedStats }
            .mapNotNull { it.mvp }
            .any { it.user.id == this.id }
    }

    fun getGoalsInPastEvents(): Int {
        return players
            .mapNotNull { it.event?.finishedStats }
            .sumOf { stats -> stats.goals.count { it.player!!.user.id == this.id } }
    }

    fun wasAbsentInPastEvents(): Boolean {
        return players
            .mapNotNull { it.event?.finishedStats }
            .any { stats -> stats.missingPlayers.any { it.user.id == this.id } }
    }

    fun calculatePlayerScore(sport: Sport): Double {
        val sportProfile = profiles.find { it.sport == sport }

        val mvpScore = if (wasMvpInPastEvents()) 10.0 else 0.0
        val goalScore = getGoalsInPastEvents().toDouble()
        val skillScore = if (sportProfile != null) sportProfile.details.ability!!.toDouble() else 5.0
        val playsOftenScore = if (sportProfile != null && sportProfile.details.playsOften) 10.0 else 5.0
        val absenceScore = if (wasAbsentInPastEvents()) 0.0 else 10.0

        return (mvpScore + goalScore + skillScore + playsOftenScore + absenceScore) / 5.0
    }

}
