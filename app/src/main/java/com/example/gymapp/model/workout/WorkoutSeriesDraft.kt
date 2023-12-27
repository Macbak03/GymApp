package com.example.gymapp.model.workout

import com.example.gymapp.model.routine.WeightUnit

data class WorkoutSeriesDraft (
    var actualReps: String?,
    var load: String?,
    var loadUnit: WeightUnit,
    var wasModified: Boolean
)