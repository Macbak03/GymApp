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
        layoutElements[1].setText(routine?.load.toString())
        layoutElements[2].setText(routine?.reps.toString())
        layoutElements[3].setText(routine?.series.toString())
        layoutElements[4].setText(routine?.rpe.toString())
        layoutElements[5].setText(routine?.pace.toString())
    }
}