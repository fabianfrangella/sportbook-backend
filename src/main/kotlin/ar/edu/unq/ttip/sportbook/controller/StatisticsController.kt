package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.service.StatisticsService
import ar.edu.unq.ttip.sportbook.service.command.SportStats
import ar.edu.unq.ttip.sportbook.service.command.UserSportStats
import ar.edu.unq.ttip.sportbook.service.command.UserStats
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(
    name = "Estadísticas",
    description = "Endpoints para consultar estadísticas deportivas y de usuarios"
)
class StatisticsController(
    private val statisticsService: StatisticsService
) {

    @GetMapping("/sports/{sport}")
    @Operation(
        summary = "Estadísticas por deporte",
        method = "GET",
        description = "Devuelve estadísticas agregadas del deporte indicado."
    )
    fun getSportStats(@PathVariable sport: Sport): SportStats =
        statisticsService.getSportStats(sport)

    @GetMapping("/users/{userId}")
    @Operation(
        summary = "Estadísticas de un usuario",
        method = "GET",
        description = "Devuelve estadísticas generales del usuario indicado."
    )
    fun getUserStats(
        @PathVariable userId: Long
    ): UserStats =
        statisticsService.getUserStats(userId)

    @GetMapping("/users/{userId}/{sport}")
    @Operation(
        summary = "Estadísticas de un usuario por deporte",
        method = "GET",
        description = "Devuelve estadísticas del usuario para el deporte indicado."
    )
    fun getUserSportStats(@PathVariable userId: Long, @PathVariable sport: Sport): UserSportStats =
        statisticsService.getUserSportStats(userId, sport)
}
