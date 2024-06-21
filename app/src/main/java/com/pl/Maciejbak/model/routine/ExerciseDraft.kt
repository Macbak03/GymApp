package com.pl.Maciejbak.model.routine

import com.pl.Maciejbak.R
import com.pl.Maciejbak.exception.ValidationException

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
            throw ValidationException("name cannot be empty", R.id.editTextExerciseName)

        }
        val pauseDuration = Pause.fromString(pause, pauseUnit, R.id.editTextPause)
        val weight = Weight.fromStringWithUnit(load, loadUnit, R.id.editTextLoad)
        val reps = Reps.fromString(reps, R.id.editTextReps)
        val series = series
        if(series.isNullOrBlank())
        {
            throw ValidationException("series cannot be empty", R.id.editTextSeries)
        }
        if(series.toIntOrNull() == null)
        {
            throw ValidationException("series must be a number", R.id.editTextSeries)
        }
        val intSeries = series.toInt()
        val intensity = Intensity.fromString(intensity, intensityIndex, R.id.editTextIntensity)
        val pace = ExercisePace.fromString(pace, R.id.editTextPace)


        return Exercise(name, pauseDuration, weight, intSeries, reps, intensity, pace)
    }
}