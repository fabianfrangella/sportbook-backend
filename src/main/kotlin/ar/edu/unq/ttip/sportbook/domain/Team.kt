package ar.edu.unq.ttip.sportbook.domain

import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.SportUserJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.TeamJPA
import java.time.LocalDate

data class Team(val color: String, val players: List<Player>) {
    fun toEntity(): TeamJPA {
        val teamJPA = TeamJPA(color)
        val user = SportUserJPA(
            password = "pass123",
            username = "juanito",
            email = "juanito@mail.com",
            name = "Juan",
            lastName = "Pérez",
            dateOfBirth = LocalDate.of(2000, 1, 1)
        )
        teamJPA.players = players.map { PlayerJPA(it.name, SportUserJPA().apply {
            username = it.user.userName
            password = "123"
            email = "juan@mail.com"
            name = it.user.userName
            lastName = "Pérez"
            dateOfBirth = LocalDate.of(2000, 1, 1)
        }) }.toMutableList()
        return teamJPA
    }
}
