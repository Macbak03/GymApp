package com.pl.Maciejbak.model.workout

data class NoPlanWorkoutSession(
    var groupId: Int,
    var exerciseName: String?,
    var workoutSessionExercise: ArrayList<WorkoutSessionSet>
)
