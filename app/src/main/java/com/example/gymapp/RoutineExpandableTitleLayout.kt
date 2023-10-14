package com.example.gymapp

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout

class RoutineExpandableTitleLayout (
    private val context: Context,
    private val attributes: AttributeSet
    ) : LinearLayout(context, attributes)
    {

        init {
            LinearLayout.inflate(context, R.layout.routine_parent_element_layout, this)
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

            val exerciseEditText = findViewById<EditText>(R.id.editTextExercise)

            try {
                exerciseEditText.setText(text)
            } finally {
                customAttributesStyle.recycle()
            }
        }
    }