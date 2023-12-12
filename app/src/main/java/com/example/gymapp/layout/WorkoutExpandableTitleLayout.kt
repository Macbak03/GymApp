package com.example.gymapp.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.example.gymapp.R

class WorkoutExpandableTitleLayout(
    private val context: Context,
    private val attributes: AttributeSet
): LinearLayout(context, attributes){
    init {
        inflate(context, R.layout.workout_expandable_title_layout, this)
        val customAttributesStyle = context.obtainStyledAttributes(
            attributes,
            R.styleable.WorkoutExpandableTitleLayout,
            0,
            0
        )
        customAttributesStyle.recycle()
    }

}