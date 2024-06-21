package com.pl.Maciejbak.model.routine


data class Exercise(
    var name: String,
    //val progression: EnumSet<ProgressionType>,
    val pause: Pause,
    var load: Weight,
    val series: Int,
    val reps: Reps,
    val intensity: Intensity?,
    val pace: ExercisePace?
    )

