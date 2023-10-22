package com.example.gymapp.model

sealed interface Pace

data class NumericPace(
    val value: Int
): Pace
{
    override fun toString(): String {
        return value.toString()
    }
}

object MaxPace: Pace
{
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
}
