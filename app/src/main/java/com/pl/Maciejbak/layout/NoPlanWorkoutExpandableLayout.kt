package com.pl.Maciejbak.layout

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.pl.Maciejbak.R
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft

class NoPlanWorkoutExpandableLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {

    private val repsEditText: EditText
    private val weightEditText: EditText
    private val noteEditText: EditText
    private val addSetButton: FrameLayout
    private var workoutSeriesDraft: WorkoutSeriesDraft? = null
    private var workoutExerciseDraft: WorkoutExerciseDraft? = null


    init {
        inflate(context, R.layout.no_plan_workout_expandable_layout, this)
        val customAttributesStyle =
            context.obtainStyledAttributes(
                attributes,
                R.styleable.NoPlanWorkoutExpandableLayout,
                0,
                0
            )


        repsEditText = findViewById(R.id.editTextWorkoutReps)
        weightEditText = findViewById(R.id.editTextWorkoutWeight)
        noteEditText = findViewById(R.id.editTextNote)
        addSetButton = findViewById(R.id.buttonAddSet)


        repsEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                workoutSeriesDraft?.actualReps = s.toString()
            }
        })
        weightEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                workoutSeriesDraft?.load = s.toString()
            }
        })
        noteEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                workoutExerciseDraft?.note = s.toString()
            }
        })

        customAttributesStyle.recycle()
    }

    fun setSeries(
        seriesDraft: WorkoutSeriesDraft?,
        exerciseDraft: WorkoutExerciseDraft?,
        count: Int
    ) {
        val customAttributesStyle =
            context.obtainStyledAttributes(
                attributes,
                R.styleable.NoPlanWorkoutExpandableLayout,
                0,
                0
            )
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

    fun getAddSetButton(): FrameLayout {
        return this.addSetButton
    }


}