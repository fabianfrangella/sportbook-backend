package ar.edu.unq.ttip.sportbook.persistence.entity

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
}
