package com.example.gymapp.model.routine

import com.example.gymapp.exception.ValidationException

sealed interface Rpe{
    companion object{
        fun fromString(rpe: String?): Rpe {
            if (rpe == null) {
                throw ValidationException("rpe cannot be empty")
            }
            val regex = Regex("""^(\d+)$|^(\d+)-(\d+)$""")
            val match = regex.matchEntire(rpe)
                ?: throw ValidationException("rpe must be a number (eg. 7) or range (eg. 7-8)")
            val (exactValue, rangeFrom, rangeTo) = match.destructured
            return if (exactValue.isEmpty()) {
                val intRangeFrom = rangeFrom.toInt()
                val intRangeTo = rangeTo.toInt()
                if(intRangeFrom >= intRangeTo)
                {
                    throw ValidationException("first number of the range must be lower than the second number")
                }
                RangeRpe(intRangeFrom, intRangeTo)
            } else {
                ExactRpe(exactValue.toInt())
            }

        }
    }
}
data class ExactRpe(
    val value: Int,
): Rpe {
    override fun toString(): String {
        return value.toString()
    }
}

data class RangeRpe(
    val from: Int,
    val to: Int
): Rpe {
    override fun toString(): String {
        return "$from-$to"
    }
}
