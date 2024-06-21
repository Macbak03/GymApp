package com.pl.Maciejbak.model.workout

import com.pl.Maciejbak.model.routine.Weight

data class WorkoutSeries(
    var actualReps: Float,
    var seriesCount: Int,
    var load: Weight
)