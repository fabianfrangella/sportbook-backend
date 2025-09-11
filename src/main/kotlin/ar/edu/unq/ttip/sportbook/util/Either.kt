package ar.edu.unq.ttip.sportbook.util

sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()

    fun <T> map(mapLeft: (left: L) -> T, mapRight:(right: R) -> T) : T {
        return when(this) {
            is Left -> mapLeft(this.value)
            is Right -> mapRight(this.value)
        }
    }
}