package com.lifthub.lifthubandroid.model.workout

import com.lifthub.lifthubandroid.model.routine.Weight

data class WorkoutSeries(
    var actualReps: Float,
    var seriesCount: Int,
    var load: Weight
)