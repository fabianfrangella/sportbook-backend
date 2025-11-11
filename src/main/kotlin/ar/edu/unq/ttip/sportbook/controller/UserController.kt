package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.service.SportUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/users"])
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(
    name = "Users",
    description = "Endpoints para gestión de usuarios"
)
class UserController(val sportUserService: SportUserService) {

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Buscar usuarios por username",
        method = "GET",
        description = "Endpoint para buscar usuarios registrados por username. Devuelve una lista de usuarios que coincidan con el término de búsqueda."
    )
    fun searchUsers(
        @Parameter(description = "Término de búsqueda para el username", required = true)
        @RequestParam("q") query: String
    ): List<SportUser> = sportUserService.searchUsersByUsername(query)
}