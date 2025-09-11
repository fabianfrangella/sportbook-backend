package ar.edu.unq.ttip.sportbook.security

import ar.edu.unq.ttip.sportbook.persistence.entity.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.SportUserJpaRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class ApplicationConfiguration(val sportUserJpaRepository: SportUserJpaRepository) {

    @Bean
    fun userDetailsService(): UserDetailsService = UserDetailsService {
        username: String ->
        sportUserJpaRepository.findByUsername(username)
            .map {
                UserDetailsImpl(it.username!!, it.password!!, it)
            }
            .orElseThrow { UsernameNotFoundException("User not found") }
    }


    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager? {
        return config.getAuthenticationManager()
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()

        authProvider.setUserDetailsService(userDetailsService())
        authProvider.setPasswordEncoder(passwordEncoder())

        return authProvider
    }
}

class UserDetailsImpl(val name: String, val pass: String, val sportUser: SportUser) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?>? = listOf()
    override fun getPassword() = pass
    override fun getUsername() = name
}