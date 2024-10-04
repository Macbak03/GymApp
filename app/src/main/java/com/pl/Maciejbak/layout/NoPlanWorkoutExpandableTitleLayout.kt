package com.pl.Maciejbak.layout

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.pl.Maciejbak.R
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft

class NoPlanWorkoutExpandableTitleLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {
    private var workoutExerciseDraft: WorkoutExerciseDraft? = null
    private val addExerciseButton: FrameLayout
    private val exerciseNameEditText: EditText
    private val removeExerciseButton: FrameLayout

    private lateinit var textWatcher: TextWatcher

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
        removeExerciseButton = findViewById(R.id.buttonRemoveExercise)

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                workoutExerciseDraft?.exerciseName = s.toString()
            }
        }

        exerciseNameEditText.addTextChangedListener(textWatcher)

        customAttributesStyle.recycle()
    }

    fun setExerciseName(exerciseAttributes: WorkoutExerciseDraft?) {
        val customAttributesStyle = context.obtainStyledAttributes(
            attributes,
            R.styleable.NoPlanWorkoutExpandableTitleLayout,
            0,
            0
        )
        try {
            if (exerciseAttributes != null) {
                exerciseNameEditText.removeTextChangedListener(textWatcher)

                exerciseNameEditText.setText(exerciseAttributes.exerciseName)
                workoutExerciseDraft = exerciseAttributes

                exerciseNameEditText.addTextChangedListener(textWatcher)
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

    fun getAddExerciseButton(): FrameLayout {
        return this.addExerciseButton
    }

    fun getRemoveExerciseButton(): FrameLayout {
        return this.removeExerciseButton
    }
}