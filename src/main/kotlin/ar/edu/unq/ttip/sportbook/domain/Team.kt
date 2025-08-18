package ar.edu.unq.ttip.sportbook.domain

import ar.edu.unq.ttip.sportbook.persistence.entity.TeamJPA

data class Team(val color: String, val players: List<Player>) {
    fun toEntity() : TeamJPA {
        return TeamJPA(color)
    }
}
