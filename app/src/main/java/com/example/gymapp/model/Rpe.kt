package com.example.gymapp.model

sealed interface Rpe
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
): Rpe{
    override fun toString(): String {
        return "$from-$to"
    }
}
