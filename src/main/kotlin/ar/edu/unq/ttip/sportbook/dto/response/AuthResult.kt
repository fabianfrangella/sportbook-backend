package ar.edu.unq.ttip.sportbook.dto.response

import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser

data class AuthResult(
    val token: String,
    val user: SportUser
)
