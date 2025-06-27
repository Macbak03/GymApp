package com.pl.Maciejbak.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.pl.Maciejbak.R
import com.pl.Maciejbak.fragment.HomeFragment
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft

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

    fun setExerciseAttributes(exerciseAttributes: WorkoutExerciseDraft?, planName: String?) {
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
                if (planName == HomeFragment.NO_TRAINING_PLAN_OPTION){
                    val noPlanAttributeValue = "-"
                    pause.text = noPlanAttributeValue
                    pauseUnit.visibility = View.GONE
                    series.text = noPlanAttributeValue
                    intensity.text = noPlanAttributeValue
                    pace.text = noPlanAttributeValue
                }
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