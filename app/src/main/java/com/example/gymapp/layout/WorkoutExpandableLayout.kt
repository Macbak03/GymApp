package com.example.gymapp.layout

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.gymapp.R
import com.example.gymapp.model.workout.WorkoutExerciseDraft
import com.example.gymapp.model.workout.WorkoutHint
import com.example.gymapp.model.workout.WorkoutSeriesDraft

class WorkoutExpandableLayout(
    private val context: Context,
    private val attributes: AttributeSet
): LinearLayout(context, attributes) {

    private val repsEditText: EditText
    private val weightEditText: EditText
    private val noteEditText: EditText
    private var workoutSeriesDraft: WorkoutSeriesDraft? = null
    private var workoutExerciseDraft: WorkoutExerciseDraft? = null

    private val checkBox: CheckBox

    init {
        inflate(context, R.layout.workout_expandable_layout, this)
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutExpandableLayout, 0, 0)


        repsEditText = findViewById(R.id.editTextWorkoutReps)
        weightEditText = findViewById(R.id.editTextWorkoutWeight)
        noteEditText = findViewById(R.id.editTextNote)
        checkBox = findViewById(R.id.checkBoxSetDone)

        repsEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                workoutSeriesDraft?.actualReps = s.toString()
                workoutSeriesDraft?.isRepsEmpty = s.isNullOrBlank()
            }
        })
        weightEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                workoutSeriesDraft?.load = s.toString()
                workoutSeriesDraft?.isWeightEmpty = s.isNullOrBlank()
            }
        })
        noteEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                workoutExerciseDraft?.note = s.toString()
                workoutExerciseDraft?.isNoteEmpty = s.isNullOrBlank()
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
            if(seriesDraft != null && exerciseDraft != null) {
                seriesCount.text = formattedCount
                weightUnitText.text = seriesDraft.loadUnit.toString()
                if(!seriesDraft.isRepsEmpty) {repsEditText.setText(seriesDraft.actualReps)}
                if(!seriesDraft.isWeightEmpty) {weightEditText.setText(seriesDraft.load)}
                if(!exerciseDraft.isNoteEmpty) {noteEditText.setText(exerciseDraft.note)}
                checkBox.isChecked = seriesDraft.isChecked
            }
        }finally {
            customAttributesStyle.recycle()
        }
    }

    fun setHints(workoutHint: WorkoutHint?){
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutExpandableLayout, 0, 0)
        try {
            if(workoutHint != null) {
                repsEditText.hint = workoutHint.repsHint
                weightEditText.hint = workoutHint.weightHint
                noteEditText.hint = workoutHint.noteHint
            }
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

    fun getSetCheckBox(): CheckBox{
        return this.checkBox
    }
}