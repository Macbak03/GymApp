package com.pl.Maciejbak.model.workout

import com.pl.Maciejbak.model.routine.IntensityIndex
import com.pl.Maciejbak.model.routine.TimeUnit

data class WorkoutExerciseDraft(
    var exerciseName: String?,
    var pause: String?,
    var pauseUnit: TimeUnit,
    var reps: String?,
    var series: String?,
    var intensity: String?,
    var intensityIndex: IntensityIndex,
    var pace: String?,
    var note: String?,
    var isChecked: Boolean,
)