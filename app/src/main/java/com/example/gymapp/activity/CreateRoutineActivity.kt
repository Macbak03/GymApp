package com.example.gymapp.activity

import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymapp.persistence.RoutineDataBaseHelper
import com.example.gymapp.adapter.RoutineExpandableListAdapter
import com.example.gymapp.databinding.ActivityCreateRoutineBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.model.routine.ExerciseDraft
import com.example.gymapp.model.routine.TimeUnit
import com.example.gymapp.model.routine.WeightUnit
import com.example.gymapp.persistence.PlanDataBaseHelper


class CreateRoutineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRoutineBinding
    private lateinit var expandableListView: ExpandableListView
    private lateinit var routineExpandableListAdapter: RoutineExpandableListAdapter

    private val dataBase = RoutineDataBaseHelper(this, null)
    private val exercises: MutableList<ExerciseDraft> = ArrayList()
    private var exerciseCount: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expandableListView = binding.ExpandableListViewRoutineItems
        val exercise2 = ExerciseDraft(
            "exercise$exerciseCount", "",
            TimeUnit.min, "", WeightUnit.kg, "", "", "", "", true
        )
        exerciseCount++
        exercises.add(exercise2)
        routineExpandableListAdapter = RoutineExpandableListAdapter(this, exercises)
        expandableListView.setAdapter(routineExpandableListAdapter)
        var planName: String? = null
        if (intent.hasExtra(TrainingPlanActivity.NEXT_SCREEN)) {
            planName = intent.getStringExtra(TrainingPlanActivity.NEXT_SCREEN)
        }

        binding.buttonAddExercise.setOnClickListener {
            addExercise()
        }
        binding.buttonDeleteExercise.setOnClickListener {
            removeExercise()
        }
        binding.buttonSaveRoutine.setOnClickListener()
        {
            if(planName != null)
            {
                try {
                    saveRoutineIntoDB(planName)
                } catch (exception: ValidationException) {
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.button.setOnClickListener() {
            loadDb()
        }
    }

    private fun addExercise() {
        val exercise = ExerciseDraft(
            "exercise$exerciseCount",
            "",
            TimeUnit.min,
            "",
            WeightUnit.kg,
            "",
            "",
            "",
            "",
            true
        )
        exercises.add(exercise)
        exerciseCount++
        routineExpandableListAdapter.notifyDataSetChanged()
    }

    private fun removeExercise() {
        if (exercises.isNotEmpty()) {
            exercises.removeAt(exercises.lastIndex)
            exerciseCount--
            routineExpandableListAdapter.notifyDataSetChanged()
        }
    }

    private fun saveRoutineIntoDB(planName: String) {
        val routineName = binding.editTextRoutineName.text.toString()
        if (routineName.isBlank()) {
            throw ValidationException("routine name cannot be empty")
        }
        val planDataBase = PlanDataBaseHelper(this, null)
        val id = planDataBase.getFromDb(
            PlanDataBaseHelper.TABLE_NAME,
            PlanDataBaseHelper.PLAN_ID_COLUMN,
            PlanDataBaseHelper.PLAN_NAME_COLUMN,
            planName

        )?.toInt()
        if (id != null) {
            for (exercise in routineExpandableListAdapter.getRoutine()) {
                dataBase.addExercise(exercise, routineName, id)
            }
        }
    }

    private fun loadDb() {
        val cursor = dataBase.getRoutineFromDB()
        cursor!!.moveToFirst()
        cursor.close()
    }

    //TODO add exercise order

}