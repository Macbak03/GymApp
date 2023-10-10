package com.example.gymapp.model

import java.util.EnumSet
import kotlin.time.Duration


data class Routine(
    val exerciseName: String,
    val progression: EnumSet<ProgressionType>,
    val pause: Duration,
    val load: Weight,
    val series: Int,
    val reps: Reps,
    val rpe: Rpe
    )

