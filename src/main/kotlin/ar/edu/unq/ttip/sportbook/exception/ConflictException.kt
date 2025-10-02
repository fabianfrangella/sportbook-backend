package ar.edu.unq.ttip.sportbook.exception

class ConflictException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)