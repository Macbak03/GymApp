package com.pl.Maciejbak.layout

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.pl.Maciejbak.R

class RoutineExpandableTitleLayout(
    private val context: Context,
    private val attributes: AttributeSet
) : LinearLayout(context, attributes) {

    private var exerciseName: String? = null

    private val exerciseNameEditText: AutoCompleteTextView


    init {
        inflate(context, R.layout.routine_expandable_title_layout, this)

        val moveButton = findViewById<FrameLayout>(R.id.imageButtonMove)
        val exercises = context.resources.getStringArray(R.array.exercises_base)
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, exercises)
        exerciseNameEditText = findViewById<AutoCompleteTextView?>(R.id.editTextExerciseName).apply {
            setAdapter(adapter)
            threshold = 1

            setOnTouchListener { _, event ->
                performClick()
                showDropDown()
                false
            }
        }

        exerciseNameEditText.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isEmpty()){
                        postDelayed({
                            if (s.toString().isEmpty()){
                                exerciseNameEditText.showDropDown()
                            }
                        }, 100)
                    }
                    exerciseName = exerciseNameEditText.text.toString()
                    moveButton.contentDescription = "move $exerciseName"
                }
            })

        exerciseNameEditText.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.adapter.getItem(position).toString()
            exerciseName = selectedItem
        }


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