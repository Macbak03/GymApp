package com.example.gymapp.model.routine

import com.example.gymapp.exception.ValidationException

data class ExerciseDraft (
    val id: Long,
    var name: String?,
    //val progression: EnumSet<ProgressionType>,
    var pause: String?,
    var pauseUnit: TimeUnit,
    var load: String?,
    var loadUnit: WeightUnit,
    var series: String?,
    var reps: String?,
    var intensity: String?,
    var intensityIndex: IntensityIndex,
    var pace: String?,
    var wasModified: Boolean
) {
    fun toExercise() : Exercise
    {
        val name = name
        if(name.isNullOrBlank())
        {
            throw ValidationException("name cannot be empty")

        }
        val pauseDuration = Pause.fromString(pause, pauseUnit)
        val weight = Weight.fromStringWithUnit(load, loadUnit)
        val reps = Reps.fromString(reps)
        val series = series
        if(series.isNullOrBlank())
        {
            throw ValidationException("series cannot be empty")
        }
        if(series.toIntOrNull() == null)
        {
            throw ValidationException("series must be a number")
        }
        val intSeries = series.toInt()
        val intensity = Intensity.fromString(intensity, intensityIndex)
        val pace = ExercisePace.fromString(pace)


        return Exercise(name, pauseDuration, weight, intSeries, reps, intensity, pace)
    }
}