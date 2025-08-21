package ar.edu.unq.ttip.sportbook.domain.football

enum class PitchSize(val size: Int) {
    FIVE(5),
    SEVEN(7),
    EIGTH(8),
    NINE(9),
    ELEVEN(11);

    companion object {
        fun fromNumber(number: Int?) : PitchSize {
            return when (number) {
                5 -> FIVE
                7 -> SEVEN
                8 -> EIGTH
                9 -> NINE
                11 -> ELEVEN
                else -> FIVE
            }
        }
    }
}