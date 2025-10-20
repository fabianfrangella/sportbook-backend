package ar.edu.unq.ttip.sportbook.security.annotation

import ar.edu.unq.ttip.sportbook.persistence.entity.user.Role


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PermittedRoles(
    val roles: Array<Role>
)
