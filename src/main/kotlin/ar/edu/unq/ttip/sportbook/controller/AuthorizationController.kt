package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.persistence.entity.SportUser
import ar.edu.unq.ttip.sportbook.service.LoginBody
import ar.edu.unq.ttip.sportbook.service.LoginResponse
import ar.edu.unq.ttip.sportbook.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/auth"])
@CrossOrigin(origins = ["http://localhost:5173"])
class AuthorizationController(val authService: AuthService) {

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        method = "POST",
        description = "Endpoint para loggearse mediante username y password")
    fun login(@RequestBody body: LoginBody) : LoginResponse = authService.login(body)

    @PostMapping("/register")
    @Operation(
        summary = "Register",
        method = "POST",
        description = "Endpoint para registrar un nuevo usuario")
    fun register(@RequestBody body: SportUser) : SportUser = authService.register(body)
}

