package com.lifthub.lifthubandroid.model.workout

import com.lifthub.lifthubandroid.R
import com.lifthub.lifthubandroid.exception.ValidationException
import com.lifthub.lifthubandroid.model.routine.Weight
import com.lifthub.lifthubandroid.model.routine.WeightUnit

data class WorkoutSeriesDraft (
    var actualReps: String?,
    var load: String?,
    var loadUnit: WeightUnit,
    var isChecked: Boolean,
) {

    fun toWorkoutSeries(seriesCount: Int): WorkoutSeries {
        val actualReps = actualReps
        if(actualReps.isNullOrBlank())
        {
            throw ValidationException("reps cannot be empty", R.id.editTextWorkoutReps)
        }
        if(actualReps.toFloatOrNull() == null)
        {
            throw ValidationException("reps must be a number", R.id.editTextWorkoutReps)
        }
        val actualRepsFloat = actualReps.toFloat()
        val weight = Weight.fromStringWithUnit(load, loadUnit, R.id.editTextWorkoutWeight)
        return WorkoutSeries(actualRepsFloat, seriesCount, weight)
    }
}
