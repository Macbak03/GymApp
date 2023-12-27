package com.example.gymapp.activity

import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.gymapp.persistence.ExercisesDataBaseHelper
import com.example.gymapp.adapter.RoutineExpandableListAdapter
import com.example.gymapp.databinding.ActivityCreateRoutineBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.model.routine.ExerciseDraft
import com.example.gymapp.model.routine.TimeUnit
import com.example.gymapp.model.routine.WeightUnit
import com.example.gymapp.persistence.PlansDataBaseHelper


class CreateRoutineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRoutineBinding
    private lateinit var expandableListView: ExpandableListView
    private lateinit var routineExpandableListAdapter: RoutineExpandableListAdapter

    private val exercisesDataBase = ExercisesDataBaseHelper(this, null)
    private val plansDataBase = PlansDataBaseHelper(this, null)
    private var exercises: MutableList<ExerciseDraft> = ArrayList()
    private var exerciseCount: Int = 0

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

            goBackToTrainingPlanActivity()

            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var planName: String? = null
        if (intent.hasExtra(TrainingPlanActivity.PLAN_NAME)) {
            planName = intent.getStringExtra(TrainingPlanActivity.PLAN_NAME)
        }

        if(planName != null)
        {
            val planId = plansDataBase.getPlanId(planName)
            if (intent.hasExtra(TrainingPlanActivity.ROUTINE_NAME)) {
                loadRoutine(planId)
            }
        }

        expandableListView = binding.ExpandableListViewRoutineItems
        routineExpandableListAdapter = RoutineExpandableListAdapter(this, exercises)
        expandableListView.setAdapter(routineExpandableListAdapter)

        binding.buttonAddExercise.setOnClickListener {
            addExercise()
        }
        binding.buttonDeleteExercise.setOnClickListener {
            removeExercise()
        }
        binding.buttonSaveRoutine.setOnClickListener()
        {
            if (planName != null) {
                try {
                    saveRoutineIntoDB(planName)
                } catch (exception: ValidationException) {
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun loadRoutine(planId: Int?) {
        val routineName = intent.getStringExtra(TrainingPlanActivity.ROUTINE_NAME)
        if (routineName != null && planId != null) {
            binding.editTextRoutineName.setText(routineName)
            exercises = exercisesDataBase.getRoutine(routineName, planId.toString())
            exerciseCount = exercises.size

        }

    }

    private fun addExercise() {
        ++exerciseCount
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
        routineExpandableListAdapter.notifyDataSetChanged()
    }

    private fun removeExercise() {
        if (exercises.isNotEmpty()) {
            exercises.removeAt(exercises.lastIndex)
            exerciseCount--
            routineExpandableListAdapter.notifyDataSetChanged()
        }
    }

    private fun goBackToTrainingPlanActivity() {
        setResult(RESULT_OK)
        finish()
    }

    private fun saveRoutineIntoDB(planName: String) {
        if (routineExpandableListAdapter.groupCount == 0) {
            throw ValidationException("You must add at least one exercise to the routine")
        }
        val routineName = binding.editTextRoutineName.text.toString()
        if (routineName.isBlank()) {
            throw ValidationException("routine name cannot be empty")
        }
        val planId = plansDataBase.getPlanId(planName)
        if (planId != null) {
            try {
                val routine = routineExpandableListAdapter.getRoutine()
                val originalRoutineName = intent.getStringExtra(TrainingPlanActivity.ROUTINE_NAME)
                exercisesDataBase.addRoutine(routine, routineName, planId, originalRoutineName)
                Toast.makeText(this, "Routine $routineName saved", Toast.LENGTH_LONG).show()
                goBackToTrainingPlanActivity()
            } catch (exception: ValidationException) {
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}