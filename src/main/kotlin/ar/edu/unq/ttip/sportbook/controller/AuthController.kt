package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.dto.request.LoginRequest
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.security.UserDetailsImpl
import ar.edu.unq.ttip.sportbook.service.auth.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/auth"])
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(
    name = "Autenticación",
    description = "Endpoints de autenticación y registro de usuarios"
)
class AuthController(val authService: AuthService) {

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        method = "POST",
        description = "Endpoint para loguearse mediante username y password."
    )
    fun login(@RequestBody req: LoginRequest): ResponseEntity<SportUser> {
        val result = authService.login(req.username, req.password)

        return ResponseEntity
            .ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${result.token}")
            .header("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION)
            .body(result.user)
    }

    @PostMapping("/register")
    @Operation(
        summary = "Registro",
        method = "POST",
        description = "Endpoint para registrar un nuevo usuario."
    )
    fun register(@RequestBody body: SportUser): ResponseEntity<SportUser> {
        val result = authService.register(body)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${result.token}")
            .header("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION)
            .body(result.user)
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get current authenticated user",
        method = "GET",
        description = "Returns the user associated with the current valid JWT.",
    )
    fun me(@AuthenticationPrincipal user: UserDetailsImpl) = user.sportUser
}
