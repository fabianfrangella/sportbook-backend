package ar.edu.unq.ttip.sportbook.persistence.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "SPORT_PROFILE",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "sport"])]
)
class SportProfile(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: SportUser,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var sport: Sport,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "details_id")
    var details: SportProfileDetail
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
