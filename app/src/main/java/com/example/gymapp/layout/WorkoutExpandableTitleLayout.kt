package com.example.gymapp.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.example.gymapp.R
import com.example.gymapp.model.workout.WorkoutExerciseDraft

class WorkoutExpandableTitleLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {
    private var workoutExerciseDraft: WorkoutExerciseDraft? = null
    private val checkBox: CheckBox

    init {
        inflate(context, R.layout.workout_expandable_title_layout, this)
        val customAttributesStyle = context.obtainStyledAttributes(
            attributes,
            R.styleable.WorkoutExpandableTitleLayout,
            0,
            0
        )
        checkBox = findViewById(R.id.checkBoxExerciseDone)

        customAttributesStyle.recycle()
    }

    fun setExerciseAttributes(exerciseAttributes: WorkoutExerciseDraft?) {
        val customAttributesStyle = context.obtainStyledAttributes(
            attributes,
            R.styleable.WorkoutExpandableTitleLayout,
            0,
            0
        )
        val exerciseName = findViewById<TextView>(R.id.textViewExerciseName)
        val pause = findViewById<TextView>(R.id.textViewPauseValue)
        val pauseUnit = findViewById<TextView>(R.id.textViewPauseUnitValue)
        val reps = findViewById<TextView>(R.id.textViewRepsValue)
        val series = findViewById<TextView>(R.id.textViewSeriesValue)
        val rpe = findViewById<TextView>(R.id.textViewRpeValue)
        val pace = findViewById<TextView>(R.id.textViewPaceValue)
        try {
            if (exerciseAttributes != null) {
            exerciseName.text = exerciseAttributes.exerciseName
            pause.text = exerciseAttributes.pause
            pauseUnit.text = exerciseAttributes.pauseUnit.toString()
            reps.text = exerciseAttributes.reps
            series.text = exerciseAttributes.series
            rpe.text = exerciseAttributes.rpe
            pace.text = exerciseAttributes.pace
            checkBox.isChecked = exerciseAttributes.isChecked
                workoutExerciseDraft =
                    WorkoutExerciseDraft(
                        exerciseAttributes.exerciseName,
                        exerciseAttributes.pause,
                        exerciseAttributes.pauseUnit,
                        exerciseAttributes.reps,
                        exerciseAttributes.series,
                        exerciseAttributes.rpe,
                        exerciseAttributes.pace,
                        null,
                        exerciseAttributes.isChecked
                    )
            }
        } finally {
            customAttributesStyle.recycle()
        }
    }

    fun getWorkoutExerciseDraft(): WorkoutExerciseDraft? {
        return this.workoutExerciseDraft
    }

    fun getExerciseCheckBox(): CheckBox{
        return this.checkBox
    }
}