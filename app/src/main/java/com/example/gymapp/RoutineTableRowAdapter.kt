package com.example.gymapp

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


    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return routines[listPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

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
            view = inflater.inflate(R.layout.routine_expandable_layout, null)
        }
        val routine = getChild(listPosition, expandedListPosition) as Routine

        val routineExpandableLayout = view?.findViewById<RoutineExpandableLayout>(R.id.expandableLayout)
        routineExpandableLayout?.setLoadText(routine.load.toString())
        return view
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return 1
    }

    override fun getGroup(listPosition: Int): Any {
        return routines[listPosition]
    }

    override fun getGroupCount(): Int {
        return routines.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if(convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.routine_parent_element_layout, null)
        }
        val routine = getGroup(listPosition) as Routine
        val routineExpandableTitleLayout = view?.findViewById<RoutineExpandableTitleLayout>(R.id.expandableLayoutTitle)
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

