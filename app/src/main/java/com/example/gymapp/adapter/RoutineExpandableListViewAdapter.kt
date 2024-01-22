package com.example.gymapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.EditText
import com.example.gymapp.R
import com.example.gymapp.layout.RoutineExpandableLayout
import com.example.gymapp.layout.RoutineExpandableTitleLayout
import com.example.gymapp.model.routine.Exercise
import com.example.gymapp.model.routine.ExerciseDraft


class RoutineExpandableListAdapter(
    private val context: Context,
    private val exercises: MutableList<ExerciseDraft>
    ) : BaseExpandableListAdapter(), View.OnDragListener {


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
            view = inflater.inflate(R.layout.routine_expandable_layout_helper, null)
        }
        val exercise = getChild(listPosition, expandedListPosition) as ExerciseDraft
        val routineExpandableLayout = view as RoutineExpandableLayout?
        routineExpandableLayout?.setAdapter(this)
        routineExpandableLayout?.setExerciseTextChangedListener(object :
            RoutineExpandableLayout.ExerciseTextChangedListener {
            override fun onExerciseNameChanged(name: String?) {
                val routineExpandableTitleLayout =
                    getGroupView(listPosition, false, null, parent) as RoutineExpandableTitleLayout
                routineExpandableTitleLayout.setExerciseNameText(name)
            }

        })
        routineExpandableLayout?.setExercise(exercise)

        return view
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return 1
    }

    fun getRoutine(): ArrayList<Exercise> {
        val routine = ArrayList<Exercise>()
        for (i: Int in 0 until groupCount) {
            val routineExpandableLayout =
                getChildView(i, 0, true, null, null) as RoutineExpandableLayout?
            val exercise = routineExpandableLayout?.getExerciseDraft()?.toExercise()
            if (exercise != null) {
                routine.add(exercise)
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
            view = inflater.inflate(R.layout.routine_expandable_title_layout_helper, null)
        }
        val exercise = getGroup(listPosition) as ExerciseDraft
        val routineExpandableTitleLayout = view as RoutineExpandableTitleLayout?
        routineExpandableTitleLayout?.setExerciseNameText(exercise.name)

       /* view?.setOnDragListener(this)
        view?.tag = listPosition
        view?.getDragButton()?.setOnLongClickListener {
            startDrag(view)
            true
        }*/


        return view
    }


    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

    private fun reorderGroupElements(oldPosition: Int, newPosition: Int) {
        if (oldPosition in 0 until exercises.size && newPosition in 0 until exercises.size) {
            // Remove the item from the old position
            val exercise = exercises.removeAt(oldPosition)

            // Add the item to the new position
            exercises.add(newPosition, exercise)

            // Notify the adapter about the change
            notifyDataSetChanged()
        }
    }

    override fun onDrag(view: View?, dragEvent: DragEvent?): Boolean {
        when (dragEvent?.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                // Handle drag started
                return true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                // Handle drag entered
                return true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                // Handle drag location
                return true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                // Handle drag exited
                return true
            }
            DragEvent.ACTION_DROP -> {
                // Handle the drop
                val draggedView = dragEvent.localState as View
                val sourcePosition = draggedView.tag as Int
                val targetPosition = view?.tag as Int

                // Perform the reorder based on source and target positions
                reorderGroupElements(sourcePosition, targetPosition)

                return true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                // Handle drag ended
                return true
            }
            else -> return false
        }
    }

    private fun startDrag(view: View) {
        val dragData = View.DragShadowBuilder(view)
        view.startDragAndDrop(null, dragData, view, 0)
    }

}

