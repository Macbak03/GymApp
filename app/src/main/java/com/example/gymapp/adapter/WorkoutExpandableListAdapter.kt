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
import com.example.gymapp.model.workout.WorkoutSeries
import com.example.gymapp.model.workout.WorkoutExerciseAttributes
import com.example.gymapp.model.workout.WorkoutExercise

class WorkoutExpandableListAdapter(
    private val context: Context,
    private val exercises: List<WorkoutExerciseAttributes>,
    private val series: List<WorkoutSeries>

) : BaseExpandableListAdapter() {

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return series[listPosition]
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
        val series = getChild(listPosition, expandedListPosition) as WorkoutSeries
        val workoutExpandableLayout = view as WorkoutExpandableLayout?
        workoutExpandableLayout?.setSeries(series, expandedListPosition + 1)
        val noteEditText =workoutExpandableLayout?.getNoteEditText()
        if(!isLastChild)
        {
            noteEditText?.visibility = View.GONE
        }
        else
        {
            noteEditText?.visibility = View.VISIBLE
        }
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
        val exercise = getGroup(listPosition) as WorkoutExerciseAttributes
        val workoutExpandableTitleLayout = view as WorkoutExpandableTitleLayout?
        workoutExpandableTitleLayout?.setExerciseAttributes(exercise)
        return view
    }

    fun getWorkout(): ArrayList<WorkoutExercise> {
        val workout = ArrayList<WorkoutExercise>()
        for (i: Int in 0 until groupCount) {
            val workoutExpandableTitleLayout =
                getGroupView(i, true, null, null) as WorkoutExpandableTitleLayout?
            val groupElement = workoutExpandableTitleLayout?.getGroupElement()
            for (j: Int in 0 until getChildrenCount(i)) {
                val workoutExpandableLayout =
                    getChildView(i, j, false, null, null) as WorkoutExpandableLayout?
                val childElement = workoutExpandableLayout?.getChildElement()
                if(groupElement != null && childElement != null)
                {
                    val exerciseDraft = ExerciseDraft(
                        groupElement.exerciseName,
                        groupElement.pause,
                        groupElement.pauseUnit,
                        childElement.load,
                        childElement.loadUnit,
                        groupElement.series,
                        groupElement.reps,
                        groupElement.rpe,
                        groupElement.pace,
                        true
                    )
                    val actualReps = childElement.actualReps?.toFloat()
                    val exercise = exerciseDraft.toExercise()
                    if(actualReps != null)
                    {
                        workout.add(WorkoutExercise(exercise, actualReps,i + 1, j + 1, childElement.note))
                    }
                }
            }
        }
        return workout
    }


    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

}