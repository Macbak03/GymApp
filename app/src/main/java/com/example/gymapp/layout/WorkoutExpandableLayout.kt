package com.example.gymapp.layout

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.gymapp.R
import com.example.gymapp.model.workout.WorkoutExerciseDraft
import com.example.gymapp.model.workout.WorkoutSeriesDraft
import com.example.gymapp.model.workout.WorkoutSessionSet

class WorkoutExpandableLayout(
    private val context: Context,
    private val attributes: AttributeSet
): LinearLayout(context, attributes) {

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

        repsEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                workoutSeriesDraft?.actualReps = s.toString()
                workoutSeriesDraft?.wasModified = true
            }

        })
        weightEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                workoutSeriesDraft?.load = s.toString()
                workoutSeriesDraft?.wasModified = true
            }
        })
        noteEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                workoutExerciseDraft?.note = s.toString()
            }

        })
        customAttributesStyle.recycle()
    }

    fun setSeries(seriesDraft: WorkoutSeriesDraft?, exerciseDraft: WorkoutExerciseDraft?, count: Int)
    {
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutExpandableLayout, 0, 0)
        val weightUnitText = findViewById<TextView>(R.id.textViewWeightUnitValue)
        val seriesCount = findViewById<TextView>(R.id.textViewSeriesCount)
        workoutSeriesDraft = seriesDraft
        workoutExerciseDraft = exerciseDraft
        val seriesCountFormat = ContextCompat.getString(context, R.string.series_count_format)
        val formattedCount = String.format(seriesCountFormat, count)
        try {
            seriesCount.text = formattedCount
            weightUnitText.text = seriesDraft?.loadUnit.toString()
            repsEditText.setText(seriesDraft?.actualReps)
            weightEditText.setText(seriesDraft?.load)
            noteEditText.setText(exerciseDraft?.note)
        }finally {
            customAttributesStyle.recycle()
        }
    }


    fun getWorkoutSeriesDraft(): WorkoutSeriesDraft?{
        return this.workoutSeriesDraft
    }


    fun getNoteEditText(): EditText{
        return this.noteEditText
    }

    fun getNote():String?{
        return this.workoutExerciseDraft?.note
    }

    fun getRepsEditText(): EditText{
        return this.repsEditText
    }

    fun getWeightEditText(): EditText{
        return this.weightEditText
    }

}