package ar.edu.unq.ttip.sportbook.exception

open class BusinessException(
    override val message: String,
) : RuntimeException(message)
