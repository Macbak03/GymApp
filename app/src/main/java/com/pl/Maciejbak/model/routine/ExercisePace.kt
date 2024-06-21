package com.pl.Maciejbak.model.routine

import com.pl.Maciejbak.exception.ValidationException

sealed interface Pace {
    companion object {
        fun fromChar(pace: Char): Pace {
            val intPace = pace.digitToIntOrNull()
            return if (intPace != null) {
                NumericPace(intPace)
            } else if (pace.compareTo('x') == 0) {
                MaxPace
            } else {
                throw ValidationException("pace must be in correct form, eg. 21x1")
            }
        }
    }
}

data class NumericPace(
    val value: Int
) : Pace {
    override fun toString(): String {
        return value.toString()
    }
}

object MaxPace : Pace {
    override fun toString(): String {
        return "x"
    }
}

data class ExercisePace(
    val eccentricPhase: Pace,
    val midLiftPause: Pace,
    val concentricPhase: Pace,
    val endLiftPause: Pace
) {
    override fun toString(): String {
        return "$eccentricPhase$midLiftPause$concentricPhase$endLiftPause"
    }

    companion object {
        fun fromString(pace: String?, position: Int): ExercisePace {
            if (pace == null) {
                throw ValidationException("pace cannot be empty", position)
            }
            val regex = Regex("""^[x\d]{4}$""")
            val match = regex.matchEntire(pace)
                ?: throw ValidationException("pace must be in correct form, eg. 21x1", position)
            val exercisePace = match.value
            return ExercisePace(
                Pace.fromChar(exercisePace[0]),
                Pace.fromChar(exercisePace[1]),
                Pace.fromChar(exercisePace[2]),
                Pace.fromChar(exercisePace[3]),
            )
        }
    }
}
