package ar.edu.unq.ttip.sportbook.util

sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()
}

enum class BusinessResult(val fn: (message: String) ->  String) {
    NOT_FOUND({
        "Entidad $it no encontrada"
    }),
    ERROR({ "Error de negocio: $it" })
}
