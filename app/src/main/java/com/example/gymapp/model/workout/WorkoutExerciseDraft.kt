package com.example.gymapp.model.workout

import com.example.gymapp.model.routine.TimeUnit

data class WorkoutExerciseDraft(
    var exerciseName: String?,
    var pause: String?,
    var pauseUnit: TimeUnit,
    var reps: String?,
    var series: String?,
    var rpe: String?,
    var pace: String?,
    var note: String?,
    var isChecked: Boolean,
)