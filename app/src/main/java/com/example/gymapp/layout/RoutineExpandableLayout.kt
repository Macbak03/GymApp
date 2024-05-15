package com.example.gymapp.layout


import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.example.gymapp.R
import com.example.gymapp.model.routine.ExerciseDraft
import com.example.gymapp.model.routine.TimeUnit
import com.example.gymapp.model.routine.WeightUnit



class RoutineExpandableLayout(
    private val context: Context,
    private val attributes: AttributeSet

) : LinearLayout(context, attributes) {

    private val pauseEditText: EditText
    private val pauseSpinner: Spinner
    private val loadEditText: EditText
    private val loadSpinner: Spinner
    private val repsEditText: EditText
    private val seriesEditText: EditText
    private val intensityEditText: EditText
    private val paceEditText: EditText

    private val intensityIndexTextView: TextView

    private val pauseDescription: FrameLayout
    private val loadDescription: FrameLayout
    private val repsDescription: FrameLayout
    private val seriesDescription: FrameLayout
    private val rpeDescription: FrameLayout
    private val paceDescription: FrameLayout


    private var exercise: ExerciseDraft? = null

    private val timeUnits = arrayOf(TimeUnit.min, TimeUnit.s)
    private val weightUnits = arrayOf(WeightUnit.kg, WeightUnit.lbs)


    init {
        inflate(context, R.layout.routine_expandable_layout, this)
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.RoutineExpandableLayout, 0, 0)

        pauseEditText = findViewById(R.id.editTextPause)
        pauseSpinner = findViewById(R.id.spinnerPause)
        loadEditText = findViewById(R.id.editTextLoad)
        loadSpinner = findViewById(R.id.spinnerLoad)
        repsEditText = findViewById(R.id.editTextReps)
        seriesEditText = findViewById(R.id.editTextSeries)
        intensityEditText = findViewById(R.id.editTextIntensity)
        paceEditText = findViewById(R.id.editTextPace)

        intensityIndexTextView = findViewById(R.id.textViewIntensity)

        pauseDescription = findViewById(R.id.imageViewPauseDescription)
        loadDescription = findViewById(R.id.imageViewLoadDescription)
        repsDescription = findViewById(R.id.imageViewRepsDescription)
        seriesDescription = findViewById(R.id.imageViewSeriesDescription)
        rpeDescription = findViewById(R.id.imageViewRpeDescription)
        paceDescription = findViewById(R.id.imageViewPaceDescription)

        initTimeUnitSpinner()
        initWeightUnitSpinner()
        customAttributesStyle.recycle()

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
        intensityEditText.setText(exercise?.intensity.toString())
        paceEditText.setText(exercise?.pace.toString())

        intensityIndexTextView.text = exercise?.intensityIndex.toString()
    }

    private fun initTimeUnitSpinner() {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, timeUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pauseSpinner.adapter = adapter
        with(pauseSpinner)
        {
            setSelection(0, false)
        }
    }

    private fun setTimeUnitSpinner(timeUnit: TimeUnit) {
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
        }
    }

    private fun setWeightUnitSpinner(weightUnit: WeightUnit) {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, weightUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loadSpinner.adapter = adapter

        val selectionIndex = weightUnits.indexOf(weightUnit)
        loadSpinner.setSelection(selectionIndex)
    }

    fun getPauseDescription(): FrameLayout {
        return this.pauseDescription
    }

    fun getLoadDescription(): FrameLayout {
        return this.loadDescription
    }

    fun getRepsDescription(): FrameLayout {
        return this.repsDescription
    }

    fun getSeriesDescription(): FrameLayout {
        return this.seriesDescription
    }

    fun getRpeDescription(): FrameLayout {
        return this.rpeDescription
    }

    fun getPaceDescription(): FrameLayout {
        return this.paceDescription
    }
}