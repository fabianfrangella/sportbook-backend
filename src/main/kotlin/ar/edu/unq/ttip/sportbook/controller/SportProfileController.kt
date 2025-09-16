package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.persistence.entity.Sport
import ar.edu.unq.ttip.sportbook.security.UserDetailsImpl
import ar.edu.unq.ttip.sportbook.service.SportProfileService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

data class UpdateFootballProfileRequest(
    val positions: MutableList<String>? = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null
)

data class FootballProfileDTO(
    val sport: String = "FOOTBALL",
    val positions: MutableList<String>? = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null
)

data class UpdateVolleyProfileRequest(
    val positions: MutableList<String>? = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null,
    val blockHeight: Int? = null,
    val rolePreference: String? = null
)

data class VolleyProfileDTO(
    val sport: String = "VOLLEY",
    val positions: MutableList<String>? = mutableListOf(),
    val favoritePosition: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null,
    val blockHeight: Int? = null,
    val rolePreference: String? = null
)

data class UpdatePaddleProfileRequest(
    val preferredSide: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null,
    val playStyle: String? = null,
    val playedTournaments: Boolean? = null
)

data class PaddleProfileDTO(
    val sport: String = "PADDLE",
    val preferredSide: String? = null,
    val ability: Int? = null,
    val playsOften: Boolean? = null,
    val playStyle: String? = null,
    val playedTournaments: Boolean? = null
)

data class SportProfileDTO(
    val sport: Sport,
    val details: Any
)


@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = ["http://localhost:5173"])
class SportProfileController(
    private val sportProfileService: SportProfileService
) {

    @GetMapping
    fun getMyProfiles(
        @AuthenticationPrincipal user: UserDetailsImpl
    ): ResponseEntity<List<SportProfileDTO>> {
        val profiles = sportProfileService.getProfiles(user.sportUser)
        return ResponseEntity.ok(profiles)
    }

    @PutMapping("/football")
    fun updateFootballProfile(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody req: UpdateFootballProfileRequest
    ): ResponseEntity<FootballProfileDTO> {
        val profile = sportProfileService.updateFootballProfile(user.sportUser, req)
        return ResponseEntity.ok(profile)
    }

    @PutMapping("/volley")
    fun updateVolleyProfile(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody req: UpdateVolleyProfileRequest
    ): ResponseEntity<VolleyProfileDTO> {
        val profile = sportProfileService.updateVolleyProfile(user.sportUser, req)
        return ResponseEntity.ok(profile)
    }

    @PutMapping("/paddle")
    fun updatePaddleProfile(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody req: UpdatePaddleProfileRequest
    ): ResponseEntity<PaddleProfileDTO> {
        val profile = sportProfileService.updatePaddleProfile(user.sportUser, req)
        return ResponseEntity.ok(profile)
    }

}
