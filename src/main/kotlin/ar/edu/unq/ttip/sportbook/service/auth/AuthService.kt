package ar.edu.unq.ttip.sportbook.service.auth

import ar.edu.unq.ttip.sportbook.controller.response.AuthResult
import ar.edu.unq.ttip.sportbook.exception.ConflictException
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.SportUserJpaRepository
import ar.edu.unq.ttip.sportbook.security.UserDetailsImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: SportUserJpaRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(username: String, rawPassword: String): AuthResult {
        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, rawPassword)
        )
        val principal = auth.principal as UserDetails
        val sportUser = userRepository.findByUsername(principal.username).orElseThrow()

        val token = jwtService.generateToken(principal)
        return AuthResult(token = token, user = sportUser)
    }

    @Transactional
    fun register(user: SportUser): AuthResult {
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
        val saved = userRepository.save(user)

        // Issue token for the newly registered user
        val userDetails = UserDetailsImpl(saved.username!!, saved.password!!, saved)
        val token = jwtService.generateToken(userDetails)

        return AuthResult(token = token, user = saved)
    }
}
