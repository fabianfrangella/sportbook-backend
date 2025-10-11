package ar.edu.unq.ttip.sportbook.persistence.entity.event.football

import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportProfileDetail
import jakarta.persistence.*

@Entity
@Table(name = "FOOTBALL_PROFILE_DETAIL")
class FootballProfileDetail(
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "FOOTBALL_POSITIONS", joinColumns = [JoinColumn(name = "profile_id")])
    @Column(name = "position")
    var positions: MutableList<String> = mutableListOf(),
    var favoritePosition: String? = null,
    playsOften: Boolean = false,
    ability: Int? = null
) : SportProfileDetail(playsOften, ability)

