package com.pl.Maciejbak.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ExpandableListView
import com.pl.Maciejbak.R
import com.pl.Maciejbak.layout.NoPlanWorkoutExpandableLayout
import com.pl.Maciejbak.layout.NoPlanWorkoutExpandableTitleLayout
import com.pl.Maciejbak.model.workout.NoPlanWorkoutSession
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft


class NoPlanWorkoutExpandableListAdapter(
    private val context: Context,
    private val workout: MutableList<Pair<WorkoutExerciseDraft, MutableList<WorkoutSeriesDraft>>>
) : WorkoutExpandableListAdapter(context, workout) {

    private val noPlanWorkoutSession =
        ArrayList<Pair<Int, List<NoPlanWorkoutSession>>>()

    init {
        //initWorkoutSession()
    }

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
            view = inflater.inflate(R.layout.no_plan_workout_expandable_layout_helper, null)
        }
        val series = getChild(listPosition, expandedListPosition) as WorkoutSeriesDraft
        val workoutExerciseDraft = getGroup(listPosition) as WorkoutExerciseDraft
        val noPlanWorkoutExpandableLayout = view as NoPlanWorkoutExpandableLayout?

        noPlanWorkoutExpandableLayout?.setSeries(
            series,
            workoutExerciseDraft,
            expandedListPosition + 1
        )

        val noteEditText = noPlanWorkoutExpandableLayout?.getNoteEditText()

        noteEditText?.visibility = if (isLastChild) View.VISIBLE else View.GONE


        val repsEditText = noPlanWorkoutExpandableLayout?.getRepsEditText()
        val weightEditText = noPlanWorkoutExpandableLayout?.getWeightEditText()

        return view
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return workout[listPosition].second.size
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
            view = inflater.inflate(R.layout.no_plan_workout_expandable_title_layout_helper, null)
        }
        val exercise = getGroup(listPosition) as WorkoutExerciseDraft
        val noPlanWorkoutExpandableTitleLayout = view as NoPlanWorkoutExpandableTitleLayout?
        noPlanWorkoutExpandableTitleLayout?.setExerciseAttributes(exercise)

        val exerciseNameEditText = noPlanWorkoutExpandableTitleLayout?.getExerciseNameEditText()


        view?.setOnClickListener {
            if (isExpanded) (parent as ExpandableListView).collapseGroup(listPosition)
            else (parent as ExpandableListView).expandGroup(listPosition, true)
        }

        return view
    }

    private fun updateSessionExerciseList(
        exerciseNameEditText: EditText,
        listPosition: Int,
    ) {

    }

    private fun updateSessionSetList(
        repsEditText: EditText,
        weightEditText: EditText,
        noteEditText: EditText,
        listPosition: Int,
        expandedListPosition: Int
    ) {

    }
    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}