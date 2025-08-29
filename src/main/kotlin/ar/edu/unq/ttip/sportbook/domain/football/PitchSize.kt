package ar.edu.unq.ttip.sportbook.domain.football

enum class PitchSize(val size: Int) {
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGTH(8),
    NINE(9),
    ELEVEN(11);

    companion object {
        fun fromNumber(number: Int?) : PitchSize {
            return when (number) {
                5 -> FIVE
                6 -> SIX
                7 -> SEVEN
                8 -> EIGTH
                9 -> NINE
                11 -> ELEVEN
                else -> FIVE
            }
        }

        fun fromString(str: String) : PitchSize {
            return when (str) {
                "FIVE" -> FIVE
                "SIX" -> SIX
                "SEVEN" -> SEVEN
                "EIGTH" -> EIGTH
                "NINE" -> NINE
                "ELEVEN" -> ELEVEN
                else -> FIVE
            }
        }
    }
}