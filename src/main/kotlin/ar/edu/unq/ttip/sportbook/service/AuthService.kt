package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.SportUserJpaRepository
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthService(
    val authenticationManager: AuthenticationManager,
    val userRepository: SportUserJpaRepository,
    val jwtService: JwtService,
    val passwordEncoder: PasswordEncoder) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(AuthService::class.java)
    }

    fun login(loginBody: LoginBody) : LoginResponse {
        val authenticatedUser = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginBody.username,
                loginBody.password
            )
        )
        val jwtToken = jwtService.generateToken(authenticatedUser.principal as UserDetails)
        return LoginResponse(jwtToken, jwtService.expirationTime)
    }

    fun register(user: SportUser): SportUser {
        val maybeUser = userRepository.findByUsername(user.username!!)
        maybeUser.ifPresent({ existentUser ->
            logger.info("User {} already exists", user.username)
            throw RuntimeException()
        })
        val encryptedPassword: String = passwordEncoder.encode(user.password)
        user.password = encryptedPassword
        userRepository.save(user)
        return user
    }
}

data class LoginBody(val username: String, val password: String)

data class LoginResponse(val token: String, val expiresIn: Long)