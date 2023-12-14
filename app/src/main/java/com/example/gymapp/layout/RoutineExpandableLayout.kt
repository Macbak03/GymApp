package com.example.gymapp.layout

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import com.example.gymapp.R
import com.example.gymapp.model.routine.ExerciseDraft
import com.example.gymapp.model.routine.TimeUnit
import com.example.gymapp.model.routine.WeightUnit


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

    private var exerciseTextChangedListener: ExerciseTextChangedListener? = null

    private val timeUnits = arrayOf(TimeUnit.min, TimeUnit.s)
    private val weightUnits = arrayOf(WeightUnit.kg, WeightUnit.lbs)


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

        exerciseEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.name = exerciseEditText.text.toString()
                exercise?.wasModified = true

                exerciseTextChangedListener?.onExerciseNameChanged(exercise?.name ?: "")
            }
        })
        pauseEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.pause = pauseEditText.text.toString()
                exercise?.wasModified = true
            }
        })
        loadEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.load = loadEditText.text.toString()
                exercise?.wasModified = true
            }
        })
        repsEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.reps = repsEditText.text.toString()
                exercise?.wasModified = true
            }
        })
        seriesEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.series = seriesEditText.text.toString()
                exercise?.wasModified = true
            }
        })
        rpeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.rpe = rpeEditText.text.toString()
                exercise?.wasModified = true
            }
        })
        paceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exercise?.pace = paceEditText.text.toString()
                exercise?.wasModified = true
            }
        })

        initTimeUnitSpinner()
        initWeightUnitSpinner()
        customAttributesStyle.recycle()

    }

    fun requestFocusOnEditText() {
        exerciseEditText.requestFocus()
    }

    fun setExerciseTextChangedListener(listener: ExerciseTextChangedListener) {
        exerciseTextChangedListener = listener
    }

    fun setExercise(exercise: ExerciseDraft?) {
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.RoutineExpandableLayout, 0, 0)

        this.exercise = exercise

        try {
            setExerciseText(exercise)
        } finally {
            customAttributesStyle.recycle()
        }
    }

    private fun setExerciseText(exercise: ExerciseDraft?) {
        exerciseEditText.setText(exercise?.name)
        pauseEditText.setText(exercise?.pause.toString())
        if (exercise != null) {
            setTimeUnitSpinner(exercise.pauseUnit)
        }
        loadEditText.setText(exercise?.load.toString())
        if (exercise != null) {
            setWeightUnitSpinner(exercise.loadUnit)
        }
        seriesEditText.setText(exercise?.series.toString())
        repsEditText.setText(exercise?.reps.toString())
        rpeEditText.setText(exercise?.rpe.toString())
        paceEditText.setText(exercise?.pace.toString())
    }

    fun getExerciseDraft(): ExerciseDraft? {
        return this.exercise
    }

    private fun initTimeUnitSpinner() {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, timeUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pauseSpinner.adapter = adapter
        with(pauseSpinner)
        {
            setSelection(0, false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val item = parent?.getItemAtPosition(position) as TimeUnit?
                    if (item != null)
                    {
                        exercise?.pauseUnit = item
                    }
                    //item?.let { exercise?.pauseUnit = it } - to to samo co 180-183 linijka
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setTimeUnitSpinner(timeUnit: TimeUnit)
    {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, timeUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pauseSpinner.adapter = adapter

        val selectionIndex = timeUnits.indexOf(timeUnit)
        pauseSpinner.setSelection(selectionIndex)
    }

    private fun initWeightUnitSpinner() {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, weightUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loadSpinner.adapter = adapter
        with(loadSpinner)
        {
            setSelection(0, false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val item = parent?.getItemAtPosition(position) as WeightUnit?
                    if (item != null)
                    {
                        exercise?.loadUnit = item
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setWeightUnitSpinner(weightUnit: WeightUnit)
    {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, weightUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loadSpinner.adapter = adapter

        val selectionIndex = weightUnits.indexOf(weightUnit)
        loadSpinner.setSelection(selectionIndex)
    }

    interface ExerciseTextChangedListener {
        fun onExerciseNameChanged(name: String?)
    }

}