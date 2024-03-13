package com.example.gymapp.model.workout

import com.example.gymapp.exception.ValidationException
import com.example.gymapp.model.routine.Weight
import com.example.gymapp.model.routine.WeightUnit

data class WorkoutSeriesDraft (
    var actualReps: String?,
    var load: String?,
    var loadUnit: WeightUnit,
    var isChecked: Boolean,
    var wasModified: Boolean
) {

    fun toWorkoutSeries(seriesCount: Int): WorkoutSeries {
        val actualReps = actualReps
        if(actualReps.isNullOrBlank())
        {
            throw ValidationException("reps cannot be empty")
        }
        if(actualReps.toFloatOrNull() == null)
        {
            throw ValidationException("series must be a number")
        }
        val actualRepsFloat = actualReps.toFloat()
        val weight = Weight.fromStringWithUnit(load, loadUnit)
        return WorkoutSeries(actualRepsFloat, seriesCount, weight)
    }
}
