package com.example.gymapp.model

import androidx.core.text.isDigitsOnly
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
        val series = series
        if(name == null)
        {
            throw ValidationException("name cannot be empty")
        }
        if(pause == null)
        {
            throw ValidationException("pause cannot be empty")
        }
        if(pause?.toIntOrNull() == null)
        {
            throw ValidationException("pause must be a number")
        }
        val pauseDuration = Duration.parse(pause + pauseUnit.toString())
        val weight = Weight.fromStringWithUnit(load, loadUnit)
        if(series == null)
        {
            throw ValidationException("series cannot be empty")
        }
        if(series.toIntOrNull() == null)
        {
            throw ValidationException("series must be a number")
        }
        val intSeries = series.toInt()
        val reps = Reps.fromString(reps)
        val rpe = Rpe.fromString(rpe)
        val pace = ExercisePace.fromString(pace)


        return Exercise(name, pauseDuration, weight, intSeries, reps, rpe, pace)
    }
}