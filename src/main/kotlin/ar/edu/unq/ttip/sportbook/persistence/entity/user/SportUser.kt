package ar.edu.unq.ttip.sportbook.persistence.entity.user

import ar.edu.unq.ttip.sportbook.dto.request.UpdateUserDataRequest
import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.FinishedEventStats
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyProfileDetail
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

    var role: Role = Role.PLAYER

    @JoinColumn(name = "additional_info_id")
    @OneToOne(targetEntity = AdditionalInfo::class, cascade = [CascadeType.ALL])
    var additionalInfo: AdditionalInfo? = null

    constructor(
        password: String,
        username: String,
        email: String,
        name: String,
        lastName: String,
        dateOfBirth: LocalDate,
        role: Role = Role.PLAYER,
        additionalInfo: AdditionalInfo? = null,
    ) : this() {
        this.password = password
        this.username = username
        this.email = email
        this.name = name
        this.lastName = lastName
        this.dateOfBirth = dateOfBirth
        this.role = role
        this.additionalInfo = additionalInfo
    }

    fun addProfile(profile: SportProfile) {
        val existing = profiles.firstOrNull { it.sport == profile.sport }
        if (existing != null) {
            throw BusinessException("El usuario ya tiene perfil para ${profile.sport}")
        }
        profiles.add(profile)
        profile.user = this
    }

    fun calculatePlayerScore(sport: Sport): Double {
        val sportProfile = profiles.find { it.sport == sport }
        val details = sportProfile?.details
        var skillScore = details?.ability?.toDouble() ?: 5.0

        if (details != null) {
            when (sport) {
                Sport.VOLLEY -> {
                    val volley = details as VolleyProfileDetail
                    val off = volley.offensiveLevel?.toDouble() ?: 0.0
                    val def = volley.defensiveLevel?.toDouble() ?: 0.0
                    if (off > 0 || def > 0) {
                        val specificAvg = if (off > 0 && def > 0) (off + def) / 2 else maxOf(off, def)
                        skillScore = (skillScore * 0.6) + (specificAvg * 0.4)
                    }
                    if ((volley.blockHeight ?: 0) > 300) skillScore += 1.0
                }

                Sport.PADDLE -> {
                    val paddle = details as PaddleProfileDetail
                    if (paddle.playedTournaments == true) skillScore += 1.5
                }

                Sport.FOOTBALL -> {
                    val football = details as FootballProfileDetail
                    if (football.positions.size > 2) skillScore += 0.5
                }
            }
        }

        val playsOftenScore = when (details?.playsOften) {
            PlayFrequency.VERY_OFTEN -> 2.0
            PlayFrequency.OFTEN -> 1.0
            else -> 0.0
        }

        val pastStats = getPastEventStats(sport)

        val mvpCount = pastStats.count { it.mvp?.user?.id == this.id }
        val mvpBonus = if (mvpCount > 0) 1.5 else 0.0

        val wins = pastStats.count { stats ->
            stats.winningTeam?.players?.any { it.user?.id == this.id } == true
        }

        val winBonus = if (pastStats.isNotEmpty()) (wins.toDouble() / pastStats.size) * 2.0 else 0.0

        val totalGoals = pastStats.sumOf { stats ->
            stats.goals.count { it.player?.user?.id == this.id }
        }

        val goalsBonus = if (pastStats.isNotEmpty() && sport == Sport.FOOTBALL) {
            (totalGoals.toDouble() / pastStats.size) * 0.5
        } else 0.0

        val absences = pastStats.count { stats ->
            stats.missingPlayers.any { it.user?.id == this.id }
        }
        val absencePenalty = if (absences > 0) 3.0 else 0.0

        val finalScore = skillScore + playsOftenScore + mvpBonus + winBonus + goalsBonus - absencePenalty

        return finalScore.coerceAtLeast(0.0)
    }

    private fun getPastEventStats(sport: Sport): List<FinishedEventStats> {
        return players
            .mapNotNull { it.event }
            .filter { it.isFinished && it.sport == sport }
            .mapNotNull { it.finishedStats }
    }

    fun updateWith(updateUserData: UpdateUserDataRequest) {
        this.username = updateUserData.username ?: this.username
        this.email = updateUserData.email ?: this.email
        this.name = updateUserData.name ?: this.name
        this.lastName = updateUserData.lastName ?: this.lastName
        this.dateOfBirth = updateUserData.dateOfBirth ?: this.dateOfBirth
        this.role = updateUserData.role?: this.role
        if (this.additionalInfo == null) {
            val additionalInfo = AdditionalInfo()
            additionalInfo.gender = updateUserData.gender
            additionalInfo.phoneNumber = updateUserData.phoneNumber
            additionalInfo.address = updateUserData.address
            additionalInfo.city = updateUserData.city
            additionalInfo.country = updateUserData.country
            additionalInfo.languages = updateUserData.languages
            this.additionalInfo = additionalInfo
        } else {
            this.additionalInfo?.gender = updateUserData.gender ?: this.additionalInfo?.gender
            this.additionalInfo?.phoneNumber = updateUserData.phoneNumber ?: this.additionalInfo?.phoneNumber
            this.additionalInfo?.address = updateUserData.address ?: this.additionalInfo?.address
            this.additionalInfo?.city = updateUserData.city ?: this.additionalInfo?.city
            this.additionalInfo?.country = updateUserData.country ?: this.additionalInfo?.country
            if (updateUserData.languages.isNotEmpty()) {
                this.additionalInfo?.languages = updateUserData.languages
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SportUser) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}
