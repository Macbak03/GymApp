package com.example.gymapp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.gymapp.adapter.WorkoutExpandableListAdapter
import com.example.gymapp.databinding.ActivityWorkoutBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.fragment.HomeFragment
import com.example.gymapp.fragment.StartWorkoutMenuFragment
import com.example.gymapp.model.workout.CustomDate
import com.example.gymapp.model.workout.WorkoutSeriesDraft
import com.example.gymapp.model.workout.WorkoutExerciseDraft
import com.example.gymapp.persistence.ExercisesDataBaseHelper
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper

class WorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var workoutExpandableListAdapter: WorkoutExpandableListAdapter
    private val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(this, null)
    private var workout: MutableList<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>> =
        ArrayList()
    private var routineName: String? = null
    private var planName: String? = null

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            saveSeries()
            val resultIntent = Intent()
            resultIntent.putExtra(HomeFragment.ROUTINE_NAME, binding.textViewCurrentWorkout.text)
            setResult(RESULT_CANCELED, resultIntent)
            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expandableListView = binding.expandableListViewWorkout
        workoutExpandableListAdapter = WorkoutExpandableListAdapter(this, workout)
        expandableListView.setAdapter(workoutExpandableListAdapter)


        if (intent.hasExtra(StartWorkoutMenuFragment.ROUTINE_NAME) && intent.hasExtra(
                StartWorkoutMenuFragment.PLAN_NAME
            )
        ) {
            routineName = intent.getStringExtra(StartWorkoutMenuFragment.ROUTINE_NAME)
            binding.textViewCurrentWorkout.text = routineName
            planName = intent.getStringExtra(StartWorkoutMenuFragment.PLAN_NAME)
            val plansDataBase = PlansDataBaseHelper(this, null)
            if (planName != null) {
                val planId = plansDataBase.getPlanId(planName)
                loadRoutine(planId)
            }
        }
        binding.buttonSaveWorkout.setOnClickListener {
            val customDate = CustomDate()
            val date = customDate.getDate()
            saveWorkoutToHistory(date)
        }

        binding.buttonCancelWorkout.setOnClickListener {
            showCancelDialog()
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

       ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottom)
            insets
        }
    }

    override fun onStop() {
        super.onStop()
        saveSeries()
        val prefs = getSharedPreferences("TerminatePreferences", Context.MODE_PRIVATE)
        prefs.edit().putString("ROUTINE_NAME", binding.textViewCurrentWorkout.text.toString()).apply()
    }

    private fun loadRoutine(planId: Int?) {
        workout.clear()
        val exercisesDataBase = ExercisesDataBaseHelper(this, null)
        val routineName = intent.getStringExtra(TrainingPlanActivity.ROUTINE_NAME)
        if (routineName != null && planId != null) {
            val savedRoutine = exercisesDataBase.getRoutine(routineName, planId.toString())
            for (savedExercise in savedRoutine) {
                val exercise = WorkoutExerciseDraft(
                    savedExercise.name,
                    savedExercise.pause,
                    savedExercise.pauseUnit,
                    savedExercise.reps,
                    savedExercise.series,
                    savedExercise.rpe,
                    savedExercise.pace,
                    ""
                )
                val seriesList = List(savedExercise.series!!.toInt()) {
                    WorkoutSeriesDraft(
                        "",
                        savedExercise.load,
                        savedExercise.loadUnit,
                        false
                    )
                }
                workout.add(workoutExpandableListAdapter.groupCount, Pair(exercise, seriesList))
                workoutExpandableListAdapter.notifyDataSetChanged()
            }
        }
        val isUnsaved = intent.getBooleanExtra(HomeFragment.IS_UNSAVED, false)
        if(isUnsaved)
        {
            restoreSeries()
        }

    }

    private fun saveWorkoutToHistory(date: String) {
        if (routineName != null && planName != null) {
            val planName = this.planName
            val routineName = this.routineName
            if (planName != null && routineName != null) {
                try {
                    workoutHistoryDatabase.addExercises(
                        workoutExpandableListAdapter,
                        date,
                        planName,
                        routineName
                    )
                    Toast.makeText(this, "Workout Saved!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } catch (exception: ValidationException) {
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveSeries() {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        for (groupPosition in 0 until workoutExpandableListAdapter.groupCount) {
            val keyNote = "child_${groupPosition}_note"
            for (childPosition in 0 until workoutExpandableListAdapter.getChildrenCount(
                groupPosition
            )) {
                val keyReps = "child_${groupPosition}_${childPosition}_reps"
                val keyLoad = "child_${groupPosition}_${childPosition}_load"

                // Retrieve the values for each child
                val reps =
                    workoutExpandableListAdapter.getRepsFromEditText(groupPosition, childPosition)
                val load =
                    workoutExpandableListAdapter.getWeightFromEditText(groupPosition, childPosition)

                // Save the values to SharedPreferences
                editor.putString(keyReps, reps)
                editor.putString(keyLoad, load)
            }
            val note = workoutExpandableListAdapter.getNoteFromEditText(groupPosition)
            editor.putString(keyNote, note)
        }

        editor.apply()
    }

    private fun restoreSeries() {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        for (groupPosition in 0 until workoutExpandableListAdapter.groupCount) {
            val keyNote = "child_${groupPosition}_note"
            for (childPosition in 0 until workoutExpandableListAdapter.getChildrenCount(groupPosition)) {
                val keyReps = "child_${groupPosition}_${childPosition}_reps"
                val keyLoad = "child_${groupPosition}_${childPosition}_load"

                // Retrieve the saved values from SharedPreferences
                val reps = sharedPreferences.getString(keyReps, "")
                val load = sharedPreferences.getString(keyLoad, "")

                // Update the corresponding WorkoutSeriesDraft values
                workout[groupPosition].second[childPosition].actualReps = reps
                if(load != "")
                {
                    workout[groupPosition].second[childPosition].load = load
                }
            }
            val note = sharedPreferences.getString(keyNote, "")
            workout[groupPosition].first.note = note
            workoutExpandableListAdapter.notifyDataSetChanged()
        }
    }

    private fun showCancelDialog() {
        val builder = this.let { AlertDialog.Builder(it) }
        with(builder) {
            this.setTitle("Are you sure you want to cancel this training? It won't be saved.")
            this.setPositiveButton("Yes") { _, _ ->
                setResult(RESULT_OK)
                finish()
            }
            this.setNegativeButton("No") { _, _ -> }
            this.show()
        }
    }



}