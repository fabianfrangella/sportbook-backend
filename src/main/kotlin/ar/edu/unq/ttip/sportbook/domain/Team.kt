package ar.edu.unq.ttip.sportbook.domain

import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.TeamJPA

data class Team(val color: String, val players: List<Player>) {
    fun toEntity(playersJpa: List<PlayerJPA>): TeamJPA {
        val teamJPA = TeamJPA(color)
        teamJPA.players = playersJpa.toMutableList()
        return teamJPA
    }
}
