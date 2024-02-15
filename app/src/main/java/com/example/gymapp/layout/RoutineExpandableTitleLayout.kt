package com.example.gymapp.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.example.gymapp.R

class RoutineExpandableTitleLayout(
    context: Context,
    attributes: AttributeSet
) : LinearLayout(context, attributes) {

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
}