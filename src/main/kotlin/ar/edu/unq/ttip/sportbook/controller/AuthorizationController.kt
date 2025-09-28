package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.controller.request.LoginRequest
import ar.edu.unq.ttip.sportbook.controller.response.LoginResponse
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/auth"])
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(
    name = "Autenticación",
    description = "Endpoints de autenticación y registro de usuarios"
)
class AuthorizationController(val authService: AuthService) {

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        method = "POST",
        description = "Endpoint para loguearse mediante username y password."
    )
    fun login(@RequestBody req: LoginRequest): LoginResponse {
        val token = authService.login(req.username, req.password)
        return LoginResponse(token = token.token, expiresIn = token.expiresAt)
    }


    @PostMapping("/register")
    @Operation(
        summary = "Registro",
        method = "POST",
        description = "Endpoint para registrar un nuevo usuario."
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody body: SportUser): SportUser =
        authService.register(body)
}
