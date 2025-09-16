package ar.edu.unq.ttip.sportbook.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "VOLLEY_PROFILE_DETAIL")
class VolleyProfileDetail(
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "VOLLEY_POSITIONS", joinColumns = [JoinColumn(name = "profile_id")])
    @Column(name = "position")
    var positions: MutableList<String> = mutableListOf(),

    var favoritePosition: String? = null,
    var ability: Int? = null,
    var playsOften: Boolean? = null,
    var blockHeight: Int? = null,
    var rolePreference: String? = null
) : SportProfileDetail()

