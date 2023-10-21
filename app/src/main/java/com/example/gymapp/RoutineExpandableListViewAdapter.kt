package com.example.gymapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.example.gymapp.model.Routine


class RoutineExpandableListAdapter(
    private val context: Context,
    private val routines: List<Routine>,

): BaseExpandableListAdapter(){

    var routineElements = ArrayList<String>()
    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return routines[listPosition]
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
        val routine = getChild(listPosition, expandedListPosition) as Routine
        val routineExpandableLayout = view as RoutineExpandableLayout?
        routineExpandableLayout?.setRoutine(routine)
        if (routineExpandableLayout != null) {
            routineElements = routineExpandableLayout.getRoutineText()
        }
        return view
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return 1
    }

    @SuppressLint("InflateParams")
/*    fun getRoutine() : ArrayList<String>
    {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.routine_expandable_layout_helper, null)
        val routineExpandableLayout = view as RoutineExpandableLayout
        return routineExpandableLayout.getRoutineText()
    }*/

    override fun getGroup(listPosition: Int): Any {
        return routines[listPosition]
    }

    override fun getGroupCount(): Int {
        return routines.size
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
        val routine = getGroup(listPosition) as Routine
        val routineExpandableTitleLayout = view as RoutineExpandableTitleLayout?
        routineExpandableTitleLayout?.setExerciseText(routine.exerciseName)
        return view
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}

