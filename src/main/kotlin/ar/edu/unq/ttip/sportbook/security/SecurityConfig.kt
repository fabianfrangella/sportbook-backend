package ar.edu.unq.ttip.sportbook.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableJpaAuditing
@EnableWebSecurity
class SecurityConfig(
    val authenticationProvider: AuthenticationProvider,
    val jwtAuthenticationFilter: JwtAuthenticationFilter) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { }
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        "/sportbook/auth/login",
                        "/sportbook/auth/register",
                        "/auth/login",
                        "/auth/register",
                        "/h2-console/**",
                        "/actuator/prometheus"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .headers { headers -> headers.frameOptions { it.disable() } }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

}