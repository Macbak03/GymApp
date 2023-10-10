package com.example.gymapp.model

sealed interface Reps

data class ExactReps(
    val value: Int
) : Reps

data class RangeReps(
    val from: Int,
    val to: Int
) : Reps