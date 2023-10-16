package com.example.gymapp

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView

class RoutineExpandableTitleLayout (
    private val context: Context,
    private val attributes: AttributeSet
    ) : LinearLayout(context, attributes)
    {

        init {
            inflate(context, R.layout.routine_expandable_title_layout, this)
            val customAttributesStyle = context.obtainStyledAttributes(
                attributes,
                R.styleable.RoutineExpandableTitleLayout,
                0,
                0
            )

            setExerciseText(customAttributesStyle.getString(R.styleable.RoutineExpandableTitleLayout_editTextExerciseText))
            customAttributesStyle.recycle()
        }

        fun setExerciseText(text: String?) {
            val customAttributesStyle = context.obtainStyledAttributes(
                attributes,
                R.styleable.RoutineExpandableTitleLayout,
                0,
                0
            )

            val exerciseEditText = findViewById<TextView>(R.id.textViewExercise)

            try {
                exerciseEditText.text = text
            } finally {
                customAttributesStyle.recycle()
            }
        }
    }