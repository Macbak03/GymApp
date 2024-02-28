package com.example.gymapp.model.workout


data class WorkoutSessionSet(
    val groupId: Int,
    val childId: Int,
    val actualReps: String,
    val load: String,
    val note: String
)