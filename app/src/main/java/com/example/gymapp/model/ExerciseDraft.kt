package com.example.gymapp.model

import com.example.gymapp.exception.ValidationException
import kotlin.time.Duration

data class ExerciseDraft (
    var name: String?,
    //val progression: EnumSet<ProgressionType>,
    var pause: String?,
    var pauseUnit: TimeUnit,
    var load: String?,
    var loadUnit: WeightUnit,
    var series: String?,
    var reps: String?,
    var rpe: String?,
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
        if(pause.isNullOrBlank())
        {
            throw ValidationException("pause cannot be empty")
        }
        if(pause?.toIntOrNull() == null)
        {
            throw ValidationException("pause must be a number")
        }
        val pauseDuration = Duration.parse(pause + pauseUnit.toString()[0])
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
        val rpe = Rpe.fromString(rpe)
        val pace = ExercisePace.fromString(pace)


        return Exercise(name, pauseDuration, weight, intSeries, reps, rpe, pace)
    }
}