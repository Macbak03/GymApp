package com.example.gymapp.model

import java.util.EnumSet
import kotlin.time.Duration


data class Routine(
    var exerciseName: String,
    //val progression: EnumSet<ProgressionType>,
    val pause: Duration,
    val load: Weight,
    val series: Int,
    val reps: Reps,
    val rpe: Rpe,
    val pace: Pace
    )

