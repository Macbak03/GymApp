package com.example.gymapp.layout

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.gymapp.R
import com.example.gymapp.model.workout.ChildElement

class WorkoutExpandableLayout(
    private val context: Context,
    private val attributes: AttributeSet
): LinearLayout(context, attributes) {

    private val repsEditText: EditText
    private val weightEditText: EditText
    private val noteEditText: EditText
    private var workoutChildElement: ChildElement? = null

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
                workoutChildElement?.reps = s.toString()
                workoutChildElement?.wasModified = true
            }

        })
        weightEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                workoutChildElement?.load = s.toString()
                workoutChildElement?.wasModified = true
            }
        })
        noteEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                workoutChildElement?.note = s.toString()
                workoutChildElement?.wasModified = true
            }

        })
        customAttributesStyle.recycle()
    }

    fun setSeries(exercise: ChildElement?, count: Int)
    {
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutExpandableLayout, 0, 0)
        val weightUnitText = findViewById<TextView>(R.id.textViewWeightUnitValue)
        val seriesCount = findViewById<TextView>(R.id.textViewSeriesCount)
        workoutChildElement = exercise
        try {
            seriesCount.text = count.toString()
            weightUnitText.text = exercise?.loadUnit.toString()
            repsEditText.setText(exercise?.reps)
            weightEditText.setText(exercise?.load)
            noteEditText.setText(exercise?.note)
        }finally {
            customAttributesStyle.recycle()
        }
    }


    fun getChildElement(): ChildElement?{
        return this.workoutChildElement
    }

    fun getNoteEditText(): EditText{
        return this.noteEditText
    }

}