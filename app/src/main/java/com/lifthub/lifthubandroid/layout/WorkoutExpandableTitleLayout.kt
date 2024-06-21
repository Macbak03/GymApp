package com.lifthub.lifthubandroid.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.lifthub.lifthubandroid.R
import com.lifthub.lifthubandroid.model.workout.WorkoutExerciseDraft

class WorkoutExpandableTitleLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {
    private var workoutExerciseDraft: WorkoutExerciseDraft? = null

    init {
        inflate(context, R.layout.workout_expandable_title_layout, this)
        val customAttributesStyle = context.obtainStyledAttributes(
            attributes,
            R.styleable.WorkoutExpandableTitleLayout,
            0,
            0
        )

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
        val series = findViewById<TextView>(R.id.textViewSeriesValue)
        val intensity = findViewById<TextView>(R.id.textViewIntensityValue)
        val intensityIndex = findViewById<TextView>(R.id.textViewIntensityIndex)
        val pace = findViewById<TextView>(R.id.textViewPaceValue)
        try {
            if (exerciseAttributes != null) {
                exerciseName.text = exerciseAttributes.exerciseName
                pause.text = exerciseAttributes.pause
                pauseUnit.text = exerciseAttributes.pauseUnit.toString()
                series.text = exerciseAttributes.series
                intensity.text = exerciseAttributes.intensity
                pace.text = exerciseAttributes.pace
                intensityIndex.text = exerciseAttributes.intensityIndex.toString()
                workoutExerciseDraft =
                    WorkoutExerciseDraft(
                        exerciseAttributes.exerciseName,
                        exerciseAttributes.pause,
                        exerciseAttributes.pauseUnit,
                        exerciseAttributes.reps,
                        exerciseAttributes.series,
                        exerciseAttributes.intensity,
                        exerciseAttributes.intensityIndex,
                        exerciseAttributes.pace,
                        null,
                        exerciseAttributes.isChecked,
                    )
            }
        } finally {
            customAttributesStyle.recycle()
        }
    }

    fun getWorkoutExerciseDraft(): WorkoutExerciseDraft? {
        return this.workoutExerciseDraft
    }

}