package com.example.gymapp.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.example.gymapp.R

class WorkoutExpandableLayout(
    private val context: Context,
    private val attributes: AttributeSet
): LinearLayout(context, attributes) {
    init {
        inflate(context, R.layout.workout_expandable_layout, this)
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutExpandableLayout, 0, 0)
        customAttributesStyle.recycle()
    }

    fun setWeightUnitText(weightUnitValue: String?, count: Int)
    {
        val customAttributesStyle =
            context.obtainStyledAttributes(attributes, R.styleable.WorkoutExpandableLayout, 0, 0)

        val weightUnitText = findViewById<TextView>(R.id.textViewWeightUnitValue)
        val seriesCount = findViewById<TextView>(R.id.textViewSeriesCount)
        try {
            weightUnitText.text = weightUnitValue
            seriesCount.text = count.toString()
        }finally {
            customAttributesStyle.recycle()
        }
    }

}