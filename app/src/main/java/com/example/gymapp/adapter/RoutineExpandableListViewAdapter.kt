package com.example.gymapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Toast
import com.example.gymapp.R
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.layout.RoutineExpandableLayout
import com.example.gymapp.layout.RoutineExpandableTitleLayout
import com.example.gymapp.model.routine.Exercise
import com.example.gymapp.model.routine.ExerciseDraft


class RoutineExpandableListAdapter(
    private val context: Context,
    private val exercises: List<ExerciseDraft>,

    ): BaseExpandableListAdapter(){

    //var exercise: ExerciseDraft? = null

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return exercises[listPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var view = convertView
        if(view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.routine_expandable_layout_helper, null)
        }
        val exercise = getChild(listPosition, expandedListPosition) as ExerciseDraft
        val routineExpandableLayout = view as RoutineExpandableLayout?
        routineExpandableLayout?.setExercise(exercise)
        return view
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return 1
    }

    fun getRoutine() : ArrayList<Exercise>
    {
        val routine = ArrayList<Exercise>()
        for (i: Int in 0 until groupCount)
        {
            val routineExpandableLayout = getChildView(i, 0, true, null, null) as RoutineExpandableLayout?
            try {
                val exercise = routineExpandableLayout?.getExerciseDraft()?.toExercise()
                if (exercise != null) {
                    routine.add(exercise)
                }
            } catch (exception: ValidationException) {
                Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
            }
        }
        return routine
    }

    override fun getGroup(listPosition: Int): Any {
        return exercises[listPosition]
    }

    override fun getGroupCount(): Int {
        return exercises.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getGroupView(listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if(convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.routine_expandable_title_layout_helper, null)
        }
        val exercise = getGroup(listPosition) as ExerciseDraft
        val routineExpandableTitleLayout = view as RoutineExpandableTitleLayout?
        routineExpandableTitleLayout?.setExerciseNameText(exercise.name)
       /* val routineExpandableLayout = getChildView(listPosition, 0, true, null, null) as RoutineExpandableLayout?
        routineExpandableLayout?.exerciseEditText?.addTextChangedListener(object: TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                routineExpandableTitleLayout?.setExerciseNameText(routineExpandableLayout.exerciseEditText.text.toString())
                notifyDataSetChanged()
            }
        })*/
        routineExpandableTitleLayout?.invalidate()
        return view
    }


    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}

