package com.example.gymapp

import android.os.Bundle
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.example.gymapp.databinding.ActivityCreateRoutineBinding
import com.example.gymapp.model.Exercise
import com.example.gymapp.model.ExerciseDraft
import com.example.gymapp.model.TimeUnit
import com.example.gymapp.model.WeightUnit


class CreateRoutineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRoutineBinding
    private lateinit var expandableListView: ExpandableListView
    private lateinit var routineExpandableListAdapter: RoutineExpandableListAdapter
    private val exercises: MutableList<ExerciseDraft> = ArrayList()
    private var exerciseCount: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expandableListView = binding.ExpandableListViewRoutineItems
        val exercise2 = ExerciseDraft(
            "exercise$exerciseCount", null,
            TimeUnit.min, null, WeightUnit.kg, null, null,null, null, true)
        exerciseCount++
        val exercise1 = ExerciseDraft(
            "exercise$exerciseCount", null,
            TimeUnit.min, null, WeightUnit.kg, null, null,null, null, true)
        exerciseCount++
        exercises.add(exercise2)
        exercises.add(exercise1)
        routineExpandableListAdapter = RoutineExpandableListAdapter(this, exercises)
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
            val exercise = ExerciseDraft("exercise$exerciseCount", null, TimeUnit.min, null, WeightUnit.kg, null, null, null, null, true)
            exercises.add(exercise)
            exerciseCount++
            routineExpandableListAdapter.notifyDataSetChanged()
        }
    }

    private fun removeExercise() {
        binding.buttonDeleteExercise.setOnClickListener()
        {
            if (exercises.isNotEmpty()) {
                exercises.removeAt(exercises.lastIndex)
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
                val exerciseDraft = routineExpandableListAdapter.exercise
                //dataBase.addExercise(exercise, routineName)
            }
        }
    }

    //TODO add exercise order

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