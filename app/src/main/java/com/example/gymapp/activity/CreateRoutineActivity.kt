package com.example.gymapp.activity

import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.gymapp.persistence.DataBaseHelper
import com.example.gymapp.adapter.RoutineExpandableListAdapter
import com.example.gymapp.databinding.ActivityCreateRoutineBinding
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
            "exercise$exerciseCount", "",
            TimeUnit.min, "", WeightUnit.kg, "", "","", "", true)
        exerciseCount++
        exercises.add(exercise2)
        routineExpandableListAdapter = RoutineExpandableListAdapter(this, exercises)
        expandableListView.setAdapter(routineExpandableListAdapter)
        addExercise()
        removeExercise()
        saveRoutineIntoDB()

    }

    private fun addExercise() {
        binding.buttonAddExercise.setOnClickListener()
        {
            val exercise = ExerciseDraft("exercise$exerciseCount", "", TimeUnit.min, "", WeightUnit.kg, "", "", "", "", true)
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
            for(exercise in routineExpandableListAdapter.getRoutine())
            {
                dataBase.addExercise(exercise, routineName)
            }
        }
    }


    //TODO add exercise order

}