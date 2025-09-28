package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportProfile
import ar.edu.unq.ttip.sportbook.security.UserDetailsImpl
import ar.edu.unq.ttip.sportbook.service.SportProfileService
import ar.edu.unq.ttip.sportbook.service.command.FootballProfileUpdate
import ar.edu.unq.ttip.sportbook.service.command.PaddleProfileUpdate
import ar.edu.unq.ttip.sportbook.service.command.VolleyProfileUpdate
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(name = "Perfiles deportivos", description = "Endpoints para gestionar perfiles por deporte")
class SportProfileController(
    private val sportProfileService: SportProfileService
) {

    @GetMapping
    @Operation(
        summary = "Mis perfiles",
        method = "GET",
        description = "Devuelve todos los perfiles deportivos del usuario autenticado."
    )
    fun getMyProfiles(
        @AuthenticationPrincipal user: UserDetailsImpl
    ): List<SportProfile> =
        sportProfileService.getProfiles(user.sportUser)

    @PutMapping("/football")
    @Operation(
        summary = "Actualizar perfil de fútbol",
        method = "PUT",
        description = "Actualiza el perfil de fútbol del usuario autenticado."
    )
    fun updateFootballProfile(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody req: FootballProfileUpdate
    ): SportProfile =
        sportProfileService.updateFootballProfile(user.sportUser, req)

    @PutMapping("/volley")
    @Operation(
        summary = "Actualizar perfil de vóley",
        method = "PUT",
        description = "Actualiza el perfil de vóley del usuario autenticado."
    )
    fun updateVolleyProfile(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody req: VolleyProfileUpdate
    ): SportProfile =
        sportProfileService.updateVolleyProfile(user.sportUser, req)

    @PutMapping("/paddle")
    @Operation(
        summary = "Actualizar perfil de pádel",
        method = "PUT",
        description = "Actualiza el perfil de pádel del usuario autenticado."
    )
    fun updatePaddleProfile(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody req: PaddleProfileUpdate
    ): SportProfile =
        sportProfileService.updatePaddleProfile(user.sportUser, req)
}
