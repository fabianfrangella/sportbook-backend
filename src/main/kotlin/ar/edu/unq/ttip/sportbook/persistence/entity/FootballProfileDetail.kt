package ar.edu.unq.ttip.sportbook.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "FOOTBALL_PROFILE_DETAIL")
class FootballProfileDetail(
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "FOOTBALL_POSITIONS", joinColumns = [JoinColumn(name = "profile_id")])
    @Column(name = "position")
    var positions: MutableList<String> = mutableListOf(),

    var favoritePosition: String? = null,
    var ability: Int? = null,
    var playsOften: Boolean? = null
) : SportProfileDetail()

