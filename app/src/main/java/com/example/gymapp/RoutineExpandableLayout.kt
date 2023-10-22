package com.example.gymapp

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import com.example.gymapp.model.ExerciseDraft
import com.example.gymapp.model.TimeUnit
import com.example.gymapp.model.WeightUnit

class RoutineExpandableLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {

    private val exerciseEditText: EditText
    private val pauseEditText: EditText
    private val pauseSpinner: Spinner
    private val loadEditText: EditText
    private val loadSpinner: Spinner
    private val repsEditText: EditText
    private val seriesEditText: EditText
    private val rpeEditText: EditText
    private val paceEditText: EditText

    private var exercise: ExerciseDraft? = null

    init {
        inflate(context, R.layout.routine_expandable_layout, this)
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.RoutineExpandableLayout, 0, 0)

        exerciseEditText = findViewById(R.id.editTextExercise)
        pauseEditText = findViewById(R.id.editTextPause)
        pauseSpinner = findViewById(R.id.spinnerPause)
        loadEditText = findViewById(R.id.editTextLoad)
        loadSpinner = findViewById(R.id.spinnerLoad)
        repsEditText = findViewById(R.id.editTextReps)
        seriesEditText = findViewById(R.id.editTextSeries)
        rpeEditText = findViewById(R.id.editTextRpe)
        paceEditText = findViewById(R.id.editTextPace)

        exerciseEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.name = exerciseEditText.text.toString()
                exercise?.wasModified = true
            }

        })

        pauseEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.load = pauseEditText.text.toString().toFloat()
                exercise?.wasModified = true
            }

        })
        loadEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.load = loadEditText.text.toString().toFloat()
                exercise?.wasModified = true
            }

        })
        repsEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.load = repsEditText.text.toString().toFloat()
                exercise?.wasModified = true
            }

        })
        seriesEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.load = seriesEditText.text.toString().toFloat()
                exercise?.wasModified = true
            }

        })
        rpeEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.load = rpeEditText.text.toString().toFloat()
                exercise?.wasModified = true
            }

        })
        paceEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.load = paceEditText.text.toString().toFloat()
                exercise?.wasModified = true
            }

        })


        customAttributesStyle.recycle()
    }

    fun setExercise(exercise: ExerciseDraft?) {
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.RoutineExpandableLayout, 0, 0)

        this.exercise = exercise

        try {
            exerciseSetText(exercise)
        } finally {
            customAttributesStyle.recycle()
        }
    }


    private fun exerciseSetText(exercise: ExerciseDraft?) {
        exerciseEditText.setText(exercise?.name)
        pauseEditText.setText(exercise?.pause.toString())
        loadEditText.setText(exercise?.load.toString())
        repsEditText.setText(exercise?.reps.toString())
        seriesEditText.setText(exercise?.series.toString())
        rpeEditText.setText(exercise?.rpe.toString())
        paceEditText.setText(exercise?.pace.toString())
    }

    fun getExerciseDraft(): ExerciseDraft? {
        return this.exercise
        /*return ExerciseDraft(
            exerciseEditText.text.toString(),
            pauseEditText.text.toString().toFloat(),
            TimeUnit.min,
            loadEditText.text.toString().toFloat(),
            WeightUnit.kg,
            repsEditText.text.toString().toInt(),
            seriesEditText.text.toString(),
            rpeEditText.text.toString(),
            paceEditText.text.toString(),
            false
        )*/
    }
}