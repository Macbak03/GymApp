package com.lifthub.lifthubandroid.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.lifthub.lifthubandroid.R
import com.lifthub.lifthubandroid.layout.WorkoutExpandableLayout
import com.lifthub.lifthubandroid.layout.WorkoutExpandableTitleLayout
import com.lifthub.lifthubandroid.model.workout.WorkoutExerciseDraft
import com.lifthub.lifthubandroid.model.workout.WorkoutSeries
import com.lifthub.lifthubandroid.model.workout.WorkoutSeriesDraft

class EditWorkoutHistoryExpandableListAdapter(
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
            view = inflater.inflate(R.layout.workout_expandable_layout_helper, null)
        }
        val series = getChild(listPosition, expandedListPosition) as WorkoutSeriesDraft
        val workoutExerciseDraft = getGroup(listPosition) as WorkoutExerciseDraft
        val workoutExpandableLayout = view as WorkoutExpandableLayout?
        workoutExpandableLayout?.setSeries(series, workoutExerciseDraft, expandedListPosition + 1)
        val noteEditText = workoutExpandableLayout?.getNoteEditText()
        if (!isLastChild) {
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
        return view
    }

    fun getWorkoutSeries(exerciseIndex: Int): ArrayList<WorkoutSeries> {
        val series = ArrayList<WorkoutSeries>()
        series.clear()
        for (i: Int in 0 until getChildrenCount(exerciseIndex)) {
            val workoutExpandableLayout =
                getChildView(exerciseIndex, i, false, null, null) as WorkoutExpandableLayout?
            val workoutSeries =
                workoutExpandableLayout?.getWorkoutSeriesDraft()?.toWorkoutSeries(i + 1)
            if (workoutSeries != null) {
                series.add(workoutSeries)
            }
        }
        return series
    }

    fun getNoteFromEditText(listPosition: Int): String {
        var note = ""
        val workoutExpandableLayout = getChildView(
            listPosition,
            getChildrenCount(listPosition) -1,
            true,
            null,
            null
        ) as WorkoutExpandableLayout?
        if(workoutExpandableLayout != null){
            note = workoutExpandableLayout.getNoteEditText().text.toString()
        }
        return note
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}