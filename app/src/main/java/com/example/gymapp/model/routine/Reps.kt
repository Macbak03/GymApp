package com.example.gymapp.model.routine

import com.example.gymapp.exception.ValidationException

sealed interface Reps {
    companion object {
        fun fromString(reps: String?): Reps {
            if (reps.isNullOrBlank()) {
                throw ValidationException("reps cannot be empty")
            }
            val regex = Regex("""^(\d+)$|^(\d+)-(\d+)$""")
            val match = regex.matchEntire(reps)
                ?: throw ValidationException("reps must be a number (eg. 5) or range (eg. 3-5)")
            val (exactValue, rangeFrom, rangeTo) = match.destructured
            return if (exactValue.isEmpty()) {
                val intRangeFrom = rangeFrom.toInt()
                val intRangeTo = rangeTo.toInt()
                if(intRangeFrom >= intRangeTo)
                {
                    throw ValidationException("first number of the range must be lower than the second number")
                }
                RangeReps(intRangeFrom, intRangeTo)
            } else {
                ExactReps(exactValue.toInt())
            }

        }
    }

}

data class ExactReps(
    val value: Int
) : Reps {
    override fun toString(): String {
        return value.toString()
    }
}

data class RangeReps(
    val from: Int,
    val to: Int
) : Reps {
    override fun toString(): String {
        return "$from-$to"
    }
}


