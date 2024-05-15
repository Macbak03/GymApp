package com.example.gymapp.model.routine

import com.example.gymapp.exception.ValidationException

sealed interface Pause {
        companion object {
            fun fromString(pause: String?, unit: TimeUnit, position: Int): Pause {
                if (pause.isNullOrBlank()) {
                    throw ValidationException("pause cannot be empty", position)
                }
                val regex = Regex("""^(\d+)$|^(\d+)-(\d+)$""")
                val match = regex.matchEntire(pause)
                    ?: throw ValidationException("pause must be a number (eg. 5) or range (eg. 3-5) and cannot be negative", position)
                val (exactValue, rangeFrom, rangeTo) = match.destructured
                val seconds = 60
                if (exactValue.isEmpty()) {
                    val intRangeFrom = rangeFrom.toInt()
                    val intRangeTo = rangeTo.toInt()
                    if (intRangeFrom >= intRangeTo) {
                        throw ValidationException("first number of the range must be lower than the second number", position)
                    }
                    return if(unit == TimeUnit.min) {
                        RangePause(intRangeFrom * seconds, intRangeTo * seconds, unit)
                    } else {
                        RangePause(intRangeFrom, intRangeTo, unit)
                    }

                } else {
                    return if (unit == TimeUnit.min) {
                        ExactPause(exactValue.toInt() * seconds, unit)
                    }else{
                        ExactPause(exactValue.toInt(), unit)
                    }

                }

            }

        }
}

data class ExactPause(
    val value: Int,
    val pauseUnit: TimeUnit
) : Pause {
    override fun toString(): String {
        return value.toString()
    }
}

data class RangePause(
    val from: Int,
    val to: Int,
    val pauseUnit: TimeUnit
) : Pause {
    override fun toString(): String {
        return "$from-$to"
    }
}