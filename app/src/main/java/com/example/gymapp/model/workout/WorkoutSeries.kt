package com.example.gymapp.model.workout

import com.example.gymapp.model.routine.Weight

data class WorkoutSeries(
    var actualReps: Float,
    var seriesCount: Int,
    var load: Weight
) {
}