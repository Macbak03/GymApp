package com.example.gymapp

import android.os.Bundle
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.example.gymapp.databinding.ActivityCreateRoutineBinding
import com.example.gymapp.model.ExactReps
import com.example.gymapp.model.Pace
import com.example.gymapp.model.Routine
import com.example.gymapp.model.Rpe
import com.example.gymapp.model.Weight
import com.example.gymapp.model.WeightUnit
import kotlin.time.Duration.Companion.minutes


class CreateRoutineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRoutineBinding
    private lateinit var expandableListView: ExpandableListView
    private lateinit var routineExpandableListAdapter: RoutineExpandableListAdapter
    private val routines: MutableList<Routine> = ArrayList()
    private var exerciseCount: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expandableListView = binding.ExpandableListViewRoutineItems
        val routine2 = Routine(
            "exercise$exerciseCount", 2.minutes, Weight(100f, WeightUnit.kg),
            10, ExactReps(3), Rpe(8), Pace(2, 0, 2, 1)
        )
        exerciseCount++
        val routine1 = Routine(
            "exercise$exerciseCount", 4.minutes, Weight(70f, WeightUnit.kg),
            5, ExactReps(8), Rpe(9), Pace(3, 1, 1, 1)
        )
        exerciseCount++
        routines.add(routine2)
        routines.add(routine1)
        routineExpandableListAdapter = RoutineExpandableListAdapter(this, routines)
        expandableListView.setAdapter(routineExpandableListAdapter)
        /*expandableListView.setOnGroupClickListener { parent, _, groupPosition, _ ->
            setScrollListViewHeightBasedOnChildren(parent, groupPosition)
            false
        }*/
        addExercise()
        removeExercise()
        saveRoutineIntoDB()

    }

    private fun addExercise() {
        binding.buttonAddExercise.setOnClickListener()
        {
            val routine = Routine("exercise$exerciseCount", null, null, null, null, null, null)
            routines.add(routine)
            exerciseCount++
            routineExpandableListAdapter.notifyDataSetChanged()
        }
    }

    private fun removeExercise() {
        binding.buttonDeleteExercise.setOnClickListener()
        {
            if (routines.isNotEmpty()) {
                routines.removeAt(routines.lastIndex)
                exerciseCount--
                routineExpandableListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun saveRoutineIntoDB()
    {
        binding.buttonSaveRoutine.setOnClickListener()
        {
            val dataBase = DataBaseHelper(this, null)
            val routineName = binding.editTextRoutineName.text.toString()
            for(i in 0..routineExpandableListAdapter.groupCount)
            {
                val routineElements = routineExpandableListAdapter.routineElements
                dataBase.addRoutineToDB(routineElements, routineName)
            }
        }
    }
    /*private fun setScrollListViewHeightBasedOnChildren(
         expandableListView: ExpandableListView,
         group: Int
     ) {
         val listAdapter = expandableListView.expandableListAdapter as ExpandableListAdapter
         var totalHeight = 0
         val desiredWidth = MeasureSpec.makeMeasureSpec(
             expandableListView.width,
             MeasureSpec.EXACTLY
         )
         for (i in 0 until listAdapter.groupCount) {
             val groupItem = listAdapter.getGroupView(i, false, null, expandableListView)
             groupItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
             totalHeight += groupItem.measuredHeight
             if (expandableListView.isGroupExpanded(i) && i != group || !expandableListView.isGroupExpanded(i) && i == group) {
                 for (j in 0 until listAdapter.getChildrenCount(i)) {
                     val listItem = listAdapter.getChildView(
                         i, j, false, null,
                         expandableListView
                     )
                     listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
                     totalHeight += listItem.measuredHeight
                 }
             }
         }
         val params = expandableListView.layoutParams
         var height = totalHeight + expandableListView.dividerHeight * (listAdapter.groupCount - 1)
         if (height < 10) height = 200
         params.height = height
         expandableListView.layoutParams = params
         expandableListView.requestLayout()
     }*/
}