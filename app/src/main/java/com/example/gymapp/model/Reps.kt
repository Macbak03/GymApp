package com.example.gymapp.model

sealed interface Reps

data class ExactReps(
    val value: Int
) : Reps
{
    override fun toString(): String {
        return value.toString()
    }
}

data class RangeReps(
    val from: Int,
    val to: Int
) : Reps
{
    override fun toString(): String {
        return "$from-$to"
    }
}