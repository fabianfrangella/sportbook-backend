package ar.edu.unq.ttip.sportbook.persistence.entity.event.volley

import ar.edu.unq.ttip.sportbook.persistence.entity.user.PlayFrequency
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportProfileDetail
import jakarta.persistence.*

@Entity
@Table(name = "VOLLEY_PROFILE_DETAIL")
@PrimaryKeyJoinColumn(name = "id")
class VolleyProfileDetail(
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "VOLLEY_POSITIONS", joinColumns = [JoinColumn(name = "profile_id")])
    @Column(name = "position")
    var positions: MutableList<String> = mutableListOf(),

    var favoritePosition: String? = null,
    var blockHeight: Int? = null,
    var rolePreference: String? = null,

    var serveType: String? = null,
    var offensiveLevel: Int? = null,
    var defensiveLevel: Int? = null,

    playsOften: PlayFrequency? = null,
    ability: Int? = null
) : SportProfileDetail(playsOften, ability)