package com.pl.Maciejbak.layout

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.pl.Maciejbak.R
import com.pl.Maciejbak.exception.ValidationException
import com.pl.Maciejbak.model.routine.Weight
import com.pl.Maciejbak.model.routine.WeightUnit
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutHints
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft

class WorkoutExpandableLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {

    private val repsEditText: EditText
    private val weightEditText: EditText
    private val noteEditText: EditText
    private var workoutSeriesDraft: WorkoutSeriesDraft? = null
    private var workoutExerciseDraft: WorkoutExerciseDraft? = null


    init {
        inflate(context, R.layout.workout_expandable_layout, this)
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutExpandableLayout, 0, 0)


        repsEditText = findViewById(R.id.editTextWorkoutReps)
        weightEditText = findViewById(R.id.editTextWorkoutWeight)
        noteEditText = findViewById(R.id.editTextNote)

        repsEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    workoutSeriesDraft?.actualReps = s.toString()
                }
            })

        }
        weightEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    workoutSeriesDraft?.load = s.toString()
                }
            })

        }
        noteEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                workoutExerciseDraft?.note = s.toString()
            }
        })


        //validateReps()
        //validateWeight()

        customAttributesStyle.recycle()
    }

    fun setSeries(
        seriesDraft: WorkoutSeriesDraft?,
        exerciseDraft: WorkoutExerciseDraft?,
        count: Int
    ) {
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutExpandableLayout, 0, 0)
        val weightUnitText = findViewById<TextView>(R.id.textViewWeightUnitValue)
        val seriesCount = findViewById<TextView>(R.id.textViewSeriesCount)
        workoutSeriesDraft = seriesDraft
        workoutExerciseDraft = exerciseDraft
        val seriesCountFormat = ContextCompat.getString(context, R.string.series_count_format)
        val formattedCount = String.format(seriesCountFormat, count)
        try {
            if (seriesDraft != null && exerciseDraft != null) {
                seriesCount.text = formattedCount
                weightUnitText.text = seriesDraft.loadUnit.toString()
                repsEditText.setText(seriesDraft.actualReps)
                weightEditText.setText(seriesDraft.load)
                noteEditText.setText(exerciseDraft.note)
            }
        } finally {
            customAttributesStyle.recycle()
        }
    }

    fun setHints(workoutHints: WorkoutHints?) {
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutExpandableLayout, 0, 0)
        if (workoutHints != null) {
            repsEditText.hint = workoutHints.repsHint
            weightEditText.hint = workoutHints.weightHint
            noteEditText.hint = workoutHints.noteHint
        }
        customAttributesStyle.recycle()
    }

    fun convertHintsToData(workoutHints: WorkoutHints?): Boolean {
        var hasConverted = false
        if (workoutHints != null) {
            val reps = workoutHints.repsHint
            val weight = workoutHints.weightHint
            if (repsEditText.text.isNullOrBlank()) {
                if (reps?.toFloatOrNull() == null) {
                    repsEditText.error = "Reps can't be in ranged value"
                } else {
                    workoutSeriesDraft?.actualReps = reps
                    repsEditText.setText(reps)
                    hasConverted = true
                }
            }
            if (weightEditText.text.isNullOrBlank()) {
                workoutSeriesDraft?.load = weight
                weightEditText.setText(weight)
            }
        }
        return hasConverted
    }


    fun getWorkoutSeriesDraft(): WorkoutSeriesDraft? {
        return this.workoutSeriesDraft
    }


    fun getNoteEditText(): EditText {
        return this.noteEditText
    }

    fun getNote(): String? {
        return this.workoutExerciseDraft?.note
    }

    fun getRepsEditText(): EditText {
        return this.repsEditText
    }

    fun getWeightEditText(): EditText {
        return this.weightEditText
    }

    private fun validateReps() {
        repsEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                try {
                    handleRepsException()
                } catch (exception: ValidationException) {
                    repsEditText.error = exception.message
                }
            }
        }
    }

    private fun handleRepsException() {
        val actualReps = workoutSeriesDraft?.actualReps
        if (actualReps.isNullOrBlank()) {
            throw ValidationException("reps cannot be empty")
        }
        if (actualReps.toFloatOrNull() == null) {
            throw ValidationException("reps must be a number")
        }
    }

    private fun validateWeight() {
        weightEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                try {
                    Weight.fromStringWithUnit(
                        weightEditText.text.toString(),
                        WeightUnit.kg,
                        R.id.editTextWorkoutWeight
                    )
                } catch (exception: ValidationException){
                    weightEditText.error = exception.message
                }
            }
        }
    }


}