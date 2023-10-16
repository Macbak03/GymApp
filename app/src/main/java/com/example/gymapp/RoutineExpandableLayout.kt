package com.example.gymapp

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout

class RoutineExpandableLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {

    init {
        inflate(context, R.layout.routine_expandable_layout, this)
        val customAttributesStyle = context.obtainStyledAttributes(attributes, R.styleable.RoutineExpandableLayout, 0, 0)

        setRoutine(customAttributesStyle.getString(R.styleable.RoutineExpandableLayout_editTextLoadText))
        customAttributesStyle.recycle()
    }

    fun setRoutine(text: String?)
    {
        val customAttributesStyle = context.obtainStyledAttributes(attributes, R.styleable.RoutineExpandableLayout, 0, 0)

        val loadEditText = findViewById<EditText>(R.id.editTextLoad)

        try {
            loadEditText.setText(text)
        } finally {
            customAttributesStyle.recycle()
        }
    }
}