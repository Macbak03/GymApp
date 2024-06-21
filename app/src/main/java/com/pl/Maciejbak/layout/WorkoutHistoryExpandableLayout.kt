package com.pl.Maciejbak.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import com.pl.Maciejbak.R
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft

class WorkoutHistoryExpandableLayout (
    private val context: Context,
    private val attributes: AttributeSet,
): LinearLayout(context, attributes) {

    private var workoutSeriesDraft: WorkoutSeriesDraft? = null
    private var workoutExerciseDraft: WorkoutExerciseDraft? = null
    private val note: TextView

    init {
        inflate(context, R.layout.workout_history_expandable_layout, this)
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutHistoryExpandableLayout, 0, 0)
        note = findViewById(R.id.textViewHistoryDetailNote)
        customAttributesStyle.recycle()
    }

    fun setSeries(seriesDraft: WorkoutSeriesDraft?, exerciseDraft: WorkoutExerciseDraft?, count: Int)
    {
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutHistoryExpandableLayout, 0, 0)
        val weightUnitText = findViewById<TextView>(R.id.textViewHistoryDetailWeightUnitValue)
        val seriesCount = findViewById<TextView>(R.id.textViewHistoryDetailSeriesCount)
        val reps = findViewById<TextView>(R.id.textViewHistoryDetailReps)
        val weightValue = findViewById<TextView>(R.id.textViewHistoryDetailWeight)

        workoutSeriesDraft = seriesDraft
        workoutExerciseDraft = exerciseDraft
        val seriesCountFormat = getString(context, R.string.series_count_format)
        val formattedCount = String.format(seriesCountFormat, count)
        try {
            seriesCount.text = formattedCount
            weightUnitText.text = seriesDraft?.loadUnit.toString()
            reps.text = seriesDraft?.actualReps
            weightValue.text = seriesDraft?.load
            note.text = exerciseDraft?.note
        }finally {
            customAttributesStyle.recycle()
        }
    }

    fun getNoteTextView():TextView{
        return this.note
    }

}