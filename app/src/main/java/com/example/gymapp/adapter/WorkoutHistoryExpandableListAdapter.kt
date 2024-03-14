package com.example.gymapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.example.gymapp.R
import com.example.gymapp.layout.WorkoutExpandableTitleLayout
import com.example.gymapp.layout.WorkoutHistoryExpandableLayout
import com.example.gymapp.model.workout.WorkoutExerciseDraft
import com.example.gymapp.model.workout.WorkoutSeriesDraft

class WorkoutHistoryExpandableListAdapter (
    private val context: Context,
    private val workout: List<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>>

) : BaseExpandableListAdapter() {

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return workout[listPosition].second[expandedListPosition]
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
            view = inflater.inflate(R.layout.workout_history_expandable_layout_helper, null)
        }
        val series = getChild(listPosition, expandedListPosition) as WorkoutSeriesDraft
        val workoutExerciseDraft = getGroup(listPosition) as WorkoutExerciseDraft
        val workoutHistoryExpandableLayout = view as WorkoutHistoryExpandableLayout?
        workoutHistoryExpandableLayout?.setSeries(series, workoutExerciseDraft, expandedListPosition + 1)
        val noteEditText = workoutHistoryExpandableLayout?.getNoteTextView()
        if (!isLastChild || noteEditText?.text.isNullOrEmpty()) {
            noteEditText?.visibility = View.GONE
        } else {
            noteEditText?.visibility = View.VISIBLE
        }


        return view
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return workout[listPosition].first.series?.toInt() ?: 0
    }

    override fun getGroup(listPosition: Int): Any {
        return workout[listPosition].first
    }

    override fun getGroupCount(): Int {
        return workout.size
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
        val exercise = getGroup(listPosition) as WorkoutExerciseDraft
        val workoutExpandableTitleLayout = view as WorkoutExpandableTitleLayout?
        workoutExpandableTitleLayout?.setExerciseAttributes(exercise)
        workoutExpandableTitleLayout?.getExerciseCheckBox()?.visibility = View.GONE
        return view
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

}