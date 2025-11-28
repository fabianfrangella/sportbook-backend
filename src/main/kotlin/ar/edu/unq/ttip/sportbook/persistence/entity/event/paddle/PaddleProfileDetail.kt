package ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle

import ar.edu.unq.ttip.sportbook.persistence.entity.user.PlayFrequency
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportProfileDetail
import jakarta.persistence.*

@Entity
@Table(name = "PADDLE_PROFILE_DETAIL")
@PrimaryKeyJoinColumn(name = "id")
class PaddleProfileDetail(
    var preferredSide: String? = null,
    var playStyle: String? = null,
    var playedTournaments: Boolean? = null,

    playsOften: PlayFrequency? = null,
    ability: Int? = null
) : SportProfileDetail(playsOften, ability)