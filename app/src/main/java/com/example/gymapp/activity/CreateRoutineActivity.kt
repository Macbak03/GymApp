package com.example.gymapp.activity

import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.gymapp.persistence.RoutineDataBaseHelper
import com.example.gymapp.adapter.RoutineExpandableListAdapter
import com.example.gymapp.databinding.ActivityCreateRoutineBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.model.routine.ExactReps
import com.example.gymapp.model.routine.ExerciseDraft
import com.example.gymapp.model.routine.RangeReps
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

        if (intent.hasExtra(TrainingPlanActivity.ROUTINE_NAME)) {
            loadRoutine()
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

    private fun loadRoutine() {
        val routineName = intent.getStringExtra(TrainingPlanActivity.ROUTINE_NAME)
        if (routineName != null) {
            binding.editTextRoutineName.setText(routineName)

            val cursor = dataBase.getRoutine(routineName)
            cursor.moveToFirst()

            val exerciseName =
                cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.EXERCISE_NAME_COLUMN))

            var pauseInt =
                cursor.getInt(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.PAUSE_COLUMN))
            val pauseUnit: TimeUnit
            if((pauseInt % 60) == 0)
            {
                pauseInt /= 60
                pauseUnit = TimeUnit.min
            }
            else
            {
                pauseUnit = TimeUnit.s
            }
            val pause = pauseInt.toString()

            val loadValue =
                cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.LOAD_VALUE_COLUMN))

            val loadUnit =
                cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.LOAD_UNIT_COLUMN))

            val repsRangeFrom =
                cursor.getInt(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.REPS_RANGE_FROM_COLUMN))
            val repsRangeTo =
                cursor.getInt(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.REPS_RANGE_TO_COLUMN))
            val reps: String = if (repsRangeFrom == repsRangeTo) {
                ExactReps(repsRangeFrom).toString()
            } else {
                RangeReps(repsRangeFrom, repsRangeTo).toString()
            }

            val series =
                cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.SERIES_COLUMN))

            val rpeRangeFrom =
                cursor.getInt(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.RPE_RANGE_FROM_COLUMN))
            val rpeRangeTo =
                cursor.getInt(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.RPE_RANGE_TO_COLUMN))
            val rpe: String = if (rpeRangeFrom == rpeRangeTo) {
                ExactReps(rpeRangeFrom).toString()
            } else {
                RangeReps(rpeRangeFrom, rpeRangeTo).toString()
            }

            val pace =
                cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.PACE_COLUMN))

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
                    cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.EXERCISE_NAME_COLUMN))

                var nextPauseInt =
                    cursor.getInt(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.PAUSE_COLUMN))
                val nextPauseUnit: TimeUnit
                if((nextPauseInt % 60) == 0)
                {
                    nextPauseInt /= 60
                    nextPauseUnit = TimeUnit.min
                }
                else
                {
                    nextPauseUnit = TimeUnit.s
                }
                val nextPause = nextPauseInt.toString()

                val nextLoadValue =
                    cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.LOAD_VALUE_COLUMN))
                val nextLoadUnit =
                    cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.LOAD_UNIT_COLUMN))
                val nextRepsRangeFrom =
                    cursor.getInt(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.REPS_RANGE_FROM_COLUMN))
                val nextRepsRangeTo =
                    cursor.getInt(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.REPS_RANGE_TO_COLUMN))
                val nextReps: String = if (nextRepsRangeFrom == nextRepsRangeTo) {
                    ExactReps(nextRepsRangeFrom).toString()
                } else {
                    RangeReps(nextRepsRangeFrom, nextRepsRangeTo).toString()
                }
                val nextSeries =
                    cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.SERIES_COLUMN))
                val nextRpeRangeFrom =
                    cursor.getInt(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.RPE_RANGE_FROM_COLUMN))
                val nextRpeRangeTo =
                    cursor.getInt(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.RPE_RANGE_TO_COLUMN))
                val nextRpe: String = if (nextRpeRangeFrom == nextRpeRangeTo) {
                    ExactReps(nextRpeRangeFrom).toString()
                } else {
                    RangeReps(nextRpeRangeFrom, nextRpeRangeTo).toString()
                }
                val nextPace =
                    cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.PACE_COLUMN))

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
        val routineName = binding.editTextRoutineName.text.toString()
        if (routineName.isBlank()) {
            throw ValidationException("routine name cannot be empty")
        }
        val planDataBase = PlanDataBaseHelper(this, null)
        val id = planDataBase.getValue(
            PlanDataBaseHelper.TABLE_NAME,
            PlanDataBaseHelper.PLAN_ID_COLUMN,
            PlanDataBaseHelper.PLAN_NAME_COLUMN,
            planName

        )?.toInt()
        if (id != null) {
            val editRoutineName = intent.getStringExtra(TrainingPlanActivity.ROUTINE_NAME)
            if(editRoutineName != null)
            {
                dataBase.deleteRoutine(id, editRoutineName)
            }
            var exerciseCount =1
                for (exercise in routineExpandableListAdapter.getRoutine()) {
                    dataBase.addExercise(exercise, routineName, id, exerciseCount)
                    exerciseCount ++
            }
        }
        if(routineExpandableListAdapter.groupCount == 0)
        {
            throw ValidationException("You must add at least one exercise to the routine")
        }
        Toast.makeText(this, "Routine $routineName saved", Toast.LENGTH_LONG).show()
        goBackToTrainingPlanActivity()
    }


    //TODO add exercise order

}