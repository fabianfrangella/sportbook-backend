package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.request.LoginRequest
import ar.edu.unq.ttip.sportbook.controller.response.LoginResponse
import ar.edu.unq.ttip.sportbook.exception.ConflictException
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.SportUserJpaRepository
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class AuthToken(val token: String, val expiresAt: Long)

data class RegisterCommand(val username: String, val password: String)

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: SportUserJpaRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(username: String, rawPassword: String): AuthToken {
        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, rawPassword)
        )
        val principal = auth.principal as UserDetails

        val token = jwtService.generateToken(principal)
        return AuthToken(token, jwtService.expirationTime)
    }

    @Transactional
    fun register(user: SportUser): SportUser {
        val username = user.username?.trim()
        if (username != null) {
            require(username.isNotBlank()) { "Username requerido" }
        }
        user.password?.let { require(it.isNotBlank()) { "Password requerida" } }

        if (username?.let { userRepository.existsByUsername(it) } == true) {
            throw ConflictException("El usuario ya existe: $username")
        }

        val encrypted = passwordEncoder.encode(user.password)
        user.password = encrypted
        return userRepository.save(user)
    }
}

