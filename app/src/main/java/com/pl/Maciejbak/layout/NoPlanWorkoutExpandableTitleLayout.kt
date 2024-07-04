package com.pl.Maciejbak.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.pl.Maciejbak.R
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft

class NoPlanWorkoutExpandableTitleLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {
    private var workoutExerciseDraft: WorkoutExerciseDraft? = null
    private val addExerciseButton: FrameLayout
    private val exerciseNameEditText: EditText

    init {
        inflate(context, R.layout.no_plan_workout_expandable_title_layout, this)
        val customAttributesStyle = context.obtainStyledAttributes(
            attributes,
            R.styleable.NoPlanWorkoutExpandableTitleLayout,
            0,
            0
        )

        addExerciseButton = findViewById(R.id.buttonAddExercise)
        exerciseNameEditText = findViewById(R.id.editTextExerciseName)

        customAttributesStyle.recycle()
    }

    fun setExerciseAttributes(exerciseAttributes: WorkoutExerciseDraft?) {
        val customAttributesStyle = context.obtainStyledAttributes(
            attributes,
            R.styleable.NoPlanWorkoutExpandableTitleLayout,
            0,
            0
        )
        try {
            if (exerciseAttributes != null) {
                exerciseNameEditText.setText(exerciseAttributes.exerciseName)
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

    fun getExerciseNameEditText(): EditText {
        return this.exerciseNameEditText
    }
}