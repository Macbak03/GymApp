package com.example.gymapp.model

data class Pace (
    val eccentricPhase: Int,
    val midLiftPause: Int,
    val concentricPhase: Int,
    val endLiftPause: Int
){
    override fun toString(): String {
        return eccentricPhase.toString() + midLiftPause.toString() + concentricPhase.toString() + endLiftPause.toString()
    }
}
