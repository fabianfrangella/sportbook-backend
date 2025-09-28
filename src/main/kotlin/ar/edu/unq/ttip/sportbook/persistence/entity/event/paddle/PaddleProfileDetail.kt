package ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle

import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportProfileDetail
import jakarta.persistence.*

@Entity
@Table(name = "PADDLE_PROFILE_DETAIL")
class PaddleProfileDetail(
    var preferredSide: String? = null,
    var ability: Int? = null,
    var playsOften: Boolean? = null,
    var playStyle: String? = null,
    var playedTournaments: Boolean? = null
) : SportProfileDetail()

