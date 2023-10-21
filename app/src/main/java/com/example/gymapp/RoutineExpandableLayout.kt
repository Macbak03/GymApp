package com.example.gymapp

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.forEach
import com.example.gymapp.model.Routine

class RoutineExpandableLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {

    init {
        inflate(context, R.layout.routine_expandable_layout, this)
        val customAttributesStyle = context.obtainStyledAttributes(attributes, R.styleable.RoutineExpandableLayout, 0, 0)

        customAttributesStyle.recycle()
    }

    fun setRoutine(routine: Routine?)
    {
        val customAttributesStyle = context.obtainStyledAttributes(attributes, R.styleable.RoutineExpandableLayout, 0, 0)


        try {
            routineSetText(routine)
        } finally {
            customAttributesStyle.recycle()
        }
    }

    private fun findLayoutElements() : ArrayList<EditText>
    {
        val routineEditTexts = ArrayList<EditText>()
        val exerciseEditText = findViewById<EditText>(R.id.editTextExercise)
        routineEditTexts.add(exerciseEditText)

        val pauseEditText = findViewById<EditText>(R.id.editTextPause)
        routineEditTexts.add(pauseEditText)

        val loadEditText = findViewById<EditText>(R.id.editTextLoad)
        routineEditTexts.add(loadEditText)

        val repsEditText = findViewById<EditText>(R.id.editTextReps)
        routineEditTexts.add(repsEditText)

        val seriesEditText = findViewById<EditText>(R.id.editTextSeries)
        routineEditTexts.add(seriesEditText)

        val rpeEditText = findViewById<EditText>(R.id.editTextRpe)
        routineEditTexts.add(rpeEditText)

        val paceEditText = findViewById<EditText>(R.id.editTextPace)
        routineEditTexts.add(paceEditText)

        return routineEditTexts
    }

    private fun routineSetText(routine: Routine?)
    {
        val layoutElements = findLayoutElements()
        layoutElements[0].setText(routine?.exerciseName)
        layoutElements[1].setText(routine?.pause.toString())
        layoutElements[2].setText(routine?.load.toString())
        layoutElements[3].setText(routine?.reps.toString())
        layoutElements[4].setText(routine?.series.toString())
        layoutElements[5].setText(routine?.rpe.toString())
        layoutElements[6].setText(routine?.pace.toString())
    }

    fun getRoutineText() : ArrayList<String>
    {
        val layoutElements = findLayoutElements()
        val routineTexts = ArrayList<String>()
        val exerciseName = layoutElements[0].text.toString()
        routineTexts.add(exerciseName)
        val pause = layoutElements[1].text.toString()
        routineTexts.add(pause)
        val load = layoutElements[2].text.toString()
        routineTexts.add(load)
        val reps = layoutElements[3].text.toString()
        routineTexts.add(reps)
        val series = layoutElements[4].text.toString()
        routineTexts.add(series)
        val rpe = layoutElements[5].text.toString()
        routineTexts.add(rpe)
        val pace = layoutElements[6].text.toString()
        routineTexts.add(pace)
        return routineTexts
    }
}