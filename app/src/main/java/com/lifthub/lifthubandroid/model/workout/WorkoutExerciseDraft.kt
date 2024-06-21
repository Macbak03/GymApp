package com.lifthub.lifthubandroid.model.workout

import com.lifthub.lifthubandroid.model.routine.IntensityIndex
import com.lifthub.lifthubandroid.model.routine.TimeUnit

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