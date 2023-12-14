package com.example.gymapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.example.gymapp.R
import com.example.gymapp.layout.WorkoutExpandableLayout
import com.example.gymapp.layout.WorkoutExpandableTitleLayout
import com.example.gymapp.model.routine.ExerciseDraft

class WorkoutExpandableListAdapter(
    private val context: Context,
    private val exercises: List<ExerciseDraft>

) : BaseExpandableListAdapter() {

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
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.workout_expandable_layout_helper, null)
        }
        val exercise = getChild(listPosition, expandedListPosition) as ExerciseDraft
        val workoutExpandableLayout = view as WorkoutExpandableLayout?
        workoutExpandableLayout?.setWeightUnitText(exercise.loadUnit.toString(), expandedListPosition + 1)
        return view
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return exercises[listPosition].series?.toInt() ?: 0
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
    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var view = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.workout_expandable_title_layout_helper, null)
        }
        val exercise = getGroup(listPosition) as ExerciseDraft
        val workoutExpandableTitleLayout = view as WorkoutExpandableTitleLayout?
        workoutExpandableTitleLayout?.setExerciseAttributes(exercise)
        return view
    }


    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

}