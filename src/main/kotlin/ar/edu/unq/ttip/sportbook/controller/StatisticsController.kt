package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.controller.response.SportStatsDTO
import ar.edu.unq.ttip.sportbook.controller.response.UserSportStatsDTO
import ar.edu.unq.ttip.sportbook.controller.response.UserStatsDTO
import ar.edu.unq.ttip.sportbook.service.StatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = ["http://localhost:5173"])
class StatisticsController(
    private val statisticsService: StatisticsService
) {

    @GetMapping("/sports/{sport}")
    fun getSportStats(
        @PathVariable sport: String
    ): ResponseEntity<SportStatsDTO> {
        val stats = statisticsService.getSportStats(sport)
        return ResponseEntity.ok(stats)
    }

    @GetMapping("/users/{userId}")
    fun getUserStats(
        @PathVariable userId: Long
    ): ResponseEntity<UserStatsDTO> {
        val stats = statisticsService.getUserStats(userId)
        return ResponseEntity.ok(stats)
    }

    @GetMapping("/users/{userId}/{sport}")
    fun getUserSportStats(
        @PathVariable userId: Long,
        @PathVariable sport: String
    ): ResponseEntity<UserSportStatsDTO> {
        val stats = statisticsService.getUserSportStats(userId, sport)
        return ResponseEntity.ok(stats)
    }
}
