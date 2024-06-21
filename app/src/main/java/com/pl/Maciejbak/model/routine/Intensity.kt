package com.pl.Maciejbak.model.routine

import com.pl.Maciejbak.exception.ValidationException

sealed interface Intensity {
    companion object {
        fun fromString(intensity: String?, index: IntensityIndex, viewId: Int): Intensity {
            if (intensity == null) {
                throw ValidationException("rpe cannot be empty", viewId)
            }
            val regex = Regex("""^(\d|10)${'$'}|^(\d|10)-(\d|10)${'$'}""")
            val match = regex.matchEntire(intensity)
                ?: throw ValidationException("intensity must be a number (eg. 7) or range (eg. 7-8), numbers must be from 0 to 10", viewId)
            val (exactValue, rangeFrom, rangeTo) = match.destructured
            return if (exactValue.isEmpty()) {
                val intRangeFrom = rangeFrom.toInt()
                val intRangeTo = rangeTo.toInt()
                if (intRangeFrom >= intRangeTo) {
                    throw ValidationException("first number of the range must be lower than the second number", viewId)
                }
                RangeIntensity(intRangeFrom, intRangeTo, index)
            } else {
                ExactIntensity(exactValue.toInt(), index)
            }

        }
    }
}

data class ExactIntensity(
    val value: Int,
    val index: IntensityIndex
) : Intensity {
    override fun toString(): String {
        return value.toString()
    }
}

data class RangeIntensity(
    val from: Int,
    val to: Int,
    val index: IntensityIndex
) : Intensity {
    override fun toString(): String {
        return "$from-$to"
    }
}
