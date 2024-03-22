package com.example.gymapp.model.workout


data class WorkoutSessionSet(
    var groupId: Int,
    var childId: Int,
    var actualReps: String?,
    var load: String?,
    var note: String?,
    var isChecked: Boolean
)