package ar.edu.unq.ttip.sportbook.security.aspect

import ar.edu.unq.ttip.sportbook.security.UserDetailsImpl
import ar.edu.unq.ttip.sportbook.security.annotation.PermittedRoles
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Aspect
@Component
class RoleSecurityAspect {

    @Before("@annotation(ar.edu.unq.ttip.sportbook.security.annotation.PermittedRoles)")
    fun checkRole(joinPoint: JoinPoint) {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val permittedRoles = method.getAnnotation(PermittedRoles::class.java)

        val authentication = SecurityContextHolder.getContext().authentication
        val userDetails = authentication.principal as UserDetailsImpl

        if (!permittedRoles.roles.contains(userDetails.sportUser.role)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED,"Usuario no tiene los roles necesarios para acceder a este recurso")
        }
    }
}
