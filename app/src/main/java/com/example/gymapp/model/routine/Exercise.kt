package com.example.gymapp.model.routine

import kotlin.time.Duration


data class Exercise(
    var name: String,
    //val progression: EnumSet<ProgressionType>,
    val pause: Pause,
    var load: Weight,
    val series: Int,
    val reps: Reps,
    val rpe: Rpe?,
    val pace: ExercisePace?
    )

