package com.example.gymapp.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.example.gymapp.R

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
            customAttributesStyle.recycle()
        }

        fun setExerciseNameText(text: String?) {
            val customAttributesStyle = context.obtainStyledAttributes(
                attributes,
                R.styleable.RoutineExpandableTitleLayout,
                0,
                0
            )

            val exerciseTextView = findViewById<TextView>(R.id.textViewExercise)

            try {
                exerciseTextView.text = text
            } finally {
                customAttributesStyle.recycle()
            }
        }

    }