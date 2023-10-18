package com.example.gymapp.model

data class Rpe(
    val value: Int,
    val isRange: Boolean = false
) {
    override fun toString(): String {
        return value.toString()
    }
}
