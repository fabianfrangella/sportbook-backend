package ar.edu.unq.ttip.sportbook.service.error

data class Error(val code: ErrorCode,
                 val message: String) {
    fun getCodeMessage() = code.fn(message)
}

enum class ErrorCode(val fn: (message: String) ->  String) {
    NOT_FOUND({
        "Entidad $it no encontrada"
    }),
    BUSINESS_ERROR({ it }),
    INTERNAL_ERROR( { "Internal error: $it" })
}