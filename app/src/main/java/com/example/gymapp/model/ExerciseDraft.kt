package com.example.gymapp.model

import java.util.EnumSet
import kotlin.time.Duration

data class ExerciseDraft (
    var name: String?,
    //val progression: EnumSet<ProgressionType>,
    var pause: Float?,
    var pauseUnit: TimeUnit,
    var load: Float?,
    var loadUnit: WeightUnit,
    var series: Int?,
    var reps: String?,
    var rpe: String?,
    var pace: String?,
    var wasModified: Boolean
)