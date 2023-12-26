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
import com.example.gymapp.model.routine.Weight
import com.example.gymapp.model.workout.WorkoutSeriesDraft
import com.example.gymapp.model.workout.WorkoutExerciseDraft
import com.example.gymapp.model.workout.WorkoutExercise
import com.example.gymapp.model.workout.WorkoutSeries

class WorkoutExpandableListAdapter(
    private val context: Context,
    private val exercises: List<WorkoutExerciseDraft>,
    private val series: List<WorkoutSeriesDraft>

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
        val series = getChild(listPosition, expandedListPosition) as WorkoutSeriesDraft
        val workoutExpandableLayout = view as WorkoutExpandableLayout?
        workoutExpandableLayout?.setSeries(series, expandedListPosition + 1)
        val noteEditText = workoutExpandableLayout?.getNoteEditText()
        if (!isLastChild) {
            noteEditText?.visibility = View.GONE
        } else {
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
        val exercise = getGroup(listPosition) as WorkoutExerciseDraft
        val workoutExpandableTitleLayout = view as WorkoutExpandableTitleLayout?
        workoutExpandableTitleLayout?.setExerciseAttributes(exercise)
        return view
    }

    fun getWorkoutGroup(): ArrayList<WorkoutExercise> {
        val workout = ArrayList<WorkoutExercise>()
        for (i: Int in 0 until groupCount) {
            val workoutExpandableTitleLayout =
                getGroupView(i, true, null, null) as WorkoutExpandableTitleLayout?
            val workoutExerciseDraft = workoutExpandableTitleLayout?.getWorkoutExerciseDraft()
            val workoutExpandableLayout =
                getChildView(i, getChildrenCount(i), true, null, null) as WorkoutExpandableLayout?
            val workoutSeriesDraft = workoutExpandableLayout?.getWorkoutSeriesDraft()
            if (workoutExerciseDraft != null && workoutSeriesDraft != null) {
                workoutExpandableLayout.setWorkoutExerciseDraft(workoutExerciseDraft)
                val exerciseDraft = ExerciseDraft(
                    workoutExerciseDraft.exerciseName,
                    workoutExerciseDraft.pause,
                    workoutExerciseDraft.pauseUnit,
                    workoutSeriesDraft.load,
                    workoutSeriesDraft.loadUnit,
                    workoutExerciseDraft.series,
                    workoutExerciseDraft.reps,
                    workoutExerciseDraft.rpe,
                    workoutExerciseDraft.pace,
                    true
                )
                val exercise = exerciseDraft.toExercise()
                workout.add(WorkoutExercise(exercise, i + 1, workoutExerciseDraft.note))
            }
        }
        return workout
    }

    fun getWorkoutSeries(exerciseIndex: Int): ArrayList<WorkoutSeries> {
        val series = ArrayList<WorkoutSeries>()
        series.clear()
        for (i: Int in 0 until getChildrenCount(exerciseIndex)) {
            val workoutExpandableLayout =
                getChildView(exerciseIndex, i, false, null, null) as WorkoutExpandableLayout?
            val workoutSeriesDraft = workoutExpandableLayout?.getWorkoutSeriesDraft()
            if (workoutSeriesDraft != null) {
                val actualReps = workoutSeriesDraft.actualReps?.toFloat()
                val load = workoutSeriesDraft.load?.toFloat()
                if (actualReps != null && load != null) {
                    val weight = Weight(load, workoutSeriesDraft.loadUnit)
                    if (weight != null) {
                        series.add(WorkoutSeries(actualReps, i + 1, weight))
                    }
                }
            }
        }
        return series
    }


    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

}