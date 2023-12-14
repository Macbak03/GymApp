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
import com.example.gymapp.model.routine.ExactReps
import com.example.gymapp.model.routine.ExerciseDraft
import com.example.gymapp.model.routine.RangeReps
import com.example.gymapp.model.routine.TimeUnit
import com.example.gymapp.model.routine.WeightUnit
import com.example.gymapp.persistence.PlansDataBaseHelper


class CreateRoutineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRoutineBinding
    private lateinit var expandableListView: ExpandableListView
    private lateinit var routineExpandableListAdapter: RoutineExpandableListAdapter

    private val exercisesDataBase = ExercisesDataBaseHelper(this, null)
    private val plansDataBase = PlansDataBaseHelper(this, null)
    private val exercises: MutableList<ExerciseDraft> = ArrayList()
    private var exerciseCount: Int = 1

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

        expandableListView = binding.ExpandableListViewRoutineItems
        routineExpandableListAdapter = RoutineExpandableListAdapter(this, exercises)
        expandableListView.setAdapter(routineExpandableListAdapter)
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

            val cursor = exercisesDataBase.getRoutine(routineName, planId.toString())
            cursor.moveToFirst()

            val exerciseName =
                cursor.getString(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.EXERCISE_NAME_COLUMN))

            var pauseInt =
                cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.PAUSE_COLUMN))
            val pauseUnit: TimeUnit
            if ((pauseInt % 60) == 0) {
                pauseInt /= 60
                pauseUnit = TimeUnit.min
            } else {
                pauseUnit = TimeUnit.s
            }
            val pause = pauseInt.toString()

            val loadValue =
                cursor.getString(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.LOAD_VALUE_COLUMN))

            val loadUnit =
                cursor.getString(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.LOAD_UNIT_COLUMN))

            val repsRangeFrom =
                cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.REPS_RANGE_FROM_COLUMN))
            val repsRangeTo =
                cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.REPS_RANGE_TO_COLUMN))
            val reps: String = if (repsRangeFrom == repsRangeTo) {
                ExactReps(repsRangeFrom).toString()
            } else {
                RangeReps(repsRangeFrom, repsRangeTo).toString()
            }

            val series =
                cursor.getString(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.SERIES_COLUMN))

            val rpeRangeFrom =
                cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.RPE_RANGE_FROM_COLUMN))
            val rpeRangeTo =
                cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.RPE_RANGE_TO_COLUMN))
            val rpe: String = if (rpeRangeFrom == rpeRangeTo) {
                ExactReps(rpeRangeFrom).toString()
            } else {
                RangeReps(rpeRangeFrom, rpeRangeTo).toString()
            }

            val pace =
                cursor.getString(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.PACE_COLUMN))

            val exercise = ExerciseDraft(
                exerciseName,
                pause,
                pauseUnit,
                loadValue,
                WeightUnit.valueOf(loadUnit),
                series,
                reps,
                rpe,
                pace,
                false
            )
            exercises.add(exercise)
            exerciseCount++
            routineExpandableListAdapter.notifyDataSetChanged()

            while (cursor.moveToNext()) {
                val nextExerciseName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.EXERCISE_NAME_COLUMN))

                var nextPauseInt =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.PAUSE_COLUMN))
                val nextPauseUnit: TimeUnit
                if ((nextPauseInt % 60) == 0) {
                    nextPauseInt /= 60
                    nextPauseUnit = TimeUnit.min
                } else {
                    nextPauseUnit = TimeUnit.s
                }
                val nextPause = nextPauseInt.toString()

                val nextLoadValue =
                    cursor.getString(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.LOAD_VALUE_COLUMN))
                val nextLoadUnit =
                    cursor.getString(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.LOAD_UNIT_COLUMN))
                val nextRepsRangeFrom =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.REPS_RANGE_FROM_COLUMN))
                val nextRepsRangeTo =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.REPS_RANGE_TO_COLUMN))
                val nextReps: String = if (nextRepsRangeFrom == nextRepsRangeTo) {
                    ExactReps(nextRepsRangeFrom).toString()
                } else {
                    RangeReps(nextRepsRangeFrom, nextRepsRangeTo).toString()
                }
                val nextSeries =
                    cursor.getString(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.SERIES_COLUMN))
                val nextRpeRangeFrom =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.RPE_RANGE_FROM_COLUMN))
                val nextRpeRangeTo =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.RPE_RANGE_TO_COLUMN))
                val nextRpe: String = if (nextRpeRangeFrom == nextRpeRangeTo) {
                    ExactReps(nextRpeRangeFrom).toString()
                } else {
                    RangeReps(nextRpeRangeFrom, nextRpeRangeTo).toString()
                }
                val nextPace =
                    cursor.getString(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.PACE_COLUMN))

                val nextExercise = ExerciseDraft(
                    nextExerciseName,
                    nextPause,
                    nextPauseUnit,
                    nextLoadValue,
                    WeightUnit.valueOf(nextLoadUnit),
                    nextSeries,
                    nextReps,
                    nextRpe,
                    nextPace,
                    false
                )
                exercises.add(nextExercise)
                exerciseCount++
                routineExpandableListAdapter.notifyDataSetChanged()
            }
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