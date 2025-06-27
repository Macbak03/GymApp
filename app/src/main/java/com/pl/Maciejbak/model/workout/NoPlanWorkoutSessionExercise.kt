package com.pl.Maciejbak.model.workout

data class NoPlanWorkoutSessionExercise(
    var groupId: Int,
    var exerciseName: String?,
    var workoutSessionExerciseSets: ArrayList<WorkoutSessionSet>
)
