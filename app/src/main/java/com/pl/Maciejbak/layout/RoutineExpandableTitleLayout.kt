package com.pl.Maciejbak.layout

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.pl.Maciejbak.R

class RoutineExpandableTitleLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {

    private var exerciseName: String? = null

    private val exerciseNameEditText: EditText


    init {
        inflate(context, R.layout.routine_expandable_title_layout, this)

        exerciseNameEditText = findViewById(R.id.editTextExerciseName)
        val moveButton = findViewById<FrameLayout>(R.id.imageButtonMove)

        exerciseNameEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                exerciseName = exerciseNameEditText.text.toString()
                moveButton.contentDescription = "move $exerciseName"
            }

        })


        val customAttributesStyle = context.obtainStyledAttributes(
            attributes,
            R.styleable.RoutineExpandableTitleLayout,
            0,
            0
        )
        customAttributesStyle.recycle()
    }

    fun setExerciseName(exerciseName: String?){
        val customAttributesStyle = context.obtainStyledAttributes(
            attributes,
            R.styleable.RoutineExpandableTitleLayout,
            0,
            0
        )

        this.exerciseName = exerciseName

        try {
            exerciseNameEditText.setText(this.exerciseName)
        } finally {
            customAttributesStyle.recycle()
        }
    }

}