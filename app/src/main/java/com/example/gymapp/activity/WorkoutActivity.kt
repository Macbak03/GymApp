package com.example.gymapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Toast
import com.example.gymapp.adapter.WorkoutExpandableListAdapter
import com.example.gymapp.databinding.ActivityWorkoutBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.fragment.StartWorkoutMenuFragment
import com.example.gymapp.model.routine.ExactReps
import com.example.gymapp.model.routine.RangeReps
import com.example.gymapp.model.routine.TimeUnit
import com.example.gymapp.model.routine.WeightUnit
import com.example.gymapp.model.workout.WorkoutSeries
import com.example.gymapp.model.workout.WorkoutAttributes
import com.example.gymapp.persistence.ExercisesDataBaseHelper
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class WorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var workoutExpandableListAdapter: WorkoutExpandableListAdapter
    private val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(this, null)
    private val exercises: MutableList<WorkoutAttributes> = ArrayList()
    private val series: MutableList<WorkoutSeries> = ArrayList()
    private var routineName: String? = null
    private var planName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        expandableListView = binding.expandableListViewWorkout
        workoutExpandableListAdapter = WorkoutExpandableListAdapter(this, exercises, series)
        expandableListView.setAdapter(workoutExpandableListAdapter)
        if (intent.hasExtra(StartWorkoutMenuFragment.ROUTINE_NAME) && intent.hasExtra(StartWorkoutMenuFragment.PLAN_NAME))
        {
            routineName = intent.getStringExtra(StartWorkoutMenuFragment.ROUTINE_NAME)
            binding.textViewCurrentWorkout.text = intent.getStringExtra(StartWorkoutMenuFragment.ROUTINE_NAME)
            planName = intent.getStringExtra(StartWorkoutMenuFragment.PLAN_NAME)
            val plansDataBase = PlansDataBaseHelper(this, null)
            if(planName != null)
            {
                val name = planName
                if(name != null)
                {
                    val planId = plansDataBase.getPlanId(name)
                    loadRoutine(planId)
                }
            }
        }
        binding.buttonSaveWorkout.setOnClickListener{
            val date = getDate()
            saveWorkoutToHistory(date)
        }
    }

    private fun loadRoutine(planId: Int?) {
        val exercisesDataBase = ExercisesDataBaseHelper(this, null)
        val routineName = intent.getStringExtra(TrainingPlanActivity.ROUTINE_NAME)
        if (routineName != null && planId != null) {

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

            val seriesAmount =
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

            val exercise = WorkoutAttributes(
                exerciseName,
                pause,
                pauseUnit,
                seriesAmount,
                reps,
                rpe,
                pace,
            )
            val ser = WorkoutSeries(
                "",
                "",
                WeightUnit.valueOf(loadUnit),
                "",
                false
            )
            exercises.add(exercise)
            series.add(ser)
            workoutExpandableListAdapter.notifyDataSetChanged()

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

                val nextExercise = WorkoutAttributes(
                    nextExerciseName,
                    nextPause,
                    nextPauseUnit,
                    nextReps,
                    nextSeries,
                    nextRpe,
                    nextPace,
                )
                val nextSer = WorkoutSeries(
                    "",
                    "",
                    WeightUnit.valueOf(nextLoadUnit),
                    "",
                    false
                )
                exercises.add(nextExercise)
                series.add(nextSer)
                workoutExpandableListAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun saveWorkoutToHistory(date: String){
        if(routineName != null && planName != null)
        {
            val planName = this.planName
            val routineName = this.routineName
            if (planName != null && routineName != null)
            {
                try {
                    workoutHistoryDatabase.addWorkout(workoutExpandableListAdapter.getWorkout(), date, planName, routineName)
                    Toast.makeText(this, "Workout Saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (exception: ValidationException)
                {
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getDate() : String
    {
        val date = Calendar.getInstance().time
        val timeZone = TimeZone.getDefault()
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(date)
    }
}