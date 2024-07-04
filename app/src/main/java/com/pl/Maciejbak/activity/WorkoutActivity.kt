package com.pl.Maciejbak.activity

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.preference.PreferenceManager
import com.pl.Maciejbak.R
import com.pl.Maciejbak.adapter.WorkoutExpandableListAdapter
import com.pl.Maciejbak.databinding.ActivityWorkoutBinding
import com.pl.Maciejbak.exception.ValidationException
import com.pl.Maciejbak.fragment.HomeFragment
import com.pl.Maciejbak.fragment.StartWorkoutMenuFragment
import com.pl.Maciejbak.model.CustomPairDeserializer
import com.pl.Maciejbak.model.WorkoutSessionSetDeserializer
import com.pl.Maciejbak.model.workout.CustomDate
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutHints
import com.pl.Maciejbak.model.workout.WorkoutSessionSet
import com.pl.Maciejbak.persistence.ExercisesDataBaseHelper
import com.pl.Maciejbak.persistence.PlansDataBaseHelper
import com.pl.Maciejbak.persistence.WorkoutHistoryDatabaseHelper
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class WorkoutActivity : WorkoutBaseActivity() {

    private lateinit var binding: ActivityWorkoutBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var workoutExpandableListAdapter: WorkoutExpandableListAdapter
    private val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(this, null)
    private var workout: MutableList<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>> =
        ArrayList()
    private var workoutHints: MutableList<WorkoutHints> = ArrayList()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            workoutExpandableListAdapter.saveToFile()
            isTerminated = false
            val resultIntent = Intent()
            val currentWorkoutName = binding.textViewCurrentWorkout.text
            resultIntent.putExtra(HomeFragment.ROUTINE_NAME, currentWorkoutName)
            val prefs = getSharedPreferences("TerminatePreferences", Context.MODE_PRIVATE)
            prefs.edit().putString("ROUTINE_NAME", currentWorkoutName.toString())
                .apply()
            setResult(RESULT_CANCELED, resultIntent)
            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        loadTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


        expandableListView = binding.expandableListViewWorkout
        workoutExpandableListAdapter = WorkoutExpandableListAdapter(this, workout, workoutHints, expandableListView)
        expandableListView.setAdapter(workoutExpandableListAdapter)



        binding.buttonSaveWorkout.setOnClickListener {
            val customDate = CustomDate()
            val date = customDate.getDate()
            saveWorkoutToHistory(date)
        }

        binding.buttonCancelWorkout.setOnClickListener {
            showCancelDialog()
        }

        binding.buttonTimer.apply {
            setTimerButtonBackground()
            setOnClickListener {
               openTimerActivity()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        binding.goBackButton.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }

    }


    override fun onStop() {
        super.onStop()
        val prefs = getSharedPreferences("TerminatePreferences", Context.MODE_PRIVATE)
        if (isCorrectlyClosed) {
            prefs.edit().clear().apply()
        } else if (isTerminated) {
            prefs.edit().putString("ROUTINE_NAME", binding.textViewCurrentWorkout.text.toString())
                .apply()
            workoutExpandableListAdapter.saveToFile()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun loadRoutine(planId: Int?) {
        workout.clear()
        val exercisesDataBase = ExercisesDataBaseHelper(this, null)
        val routineName = intent.getStringExtra(TrainingPlanActivity.ROUTINE_NAME)
        val planName = this.planName
        if (routineName != null && planId != null && planName != null) {
            val savedRoutine = exercisesDataBase.getRoutine(routineName, planId.toString())
            val savedNotes = workoutHistoryDatabase.getLastTrainingNotes(planName, routineName)
            savedRoutine.forEachIndexed { index, savedExercise ->
                val exercise = WorkoutExerciseDraft(
                    savedExercise.name,
                    savedExercise.pause,
                    savedExercise.pauseUnit,
                    savedExercise.reps,
                    savedExercise.series,
                    savedExercise.intensity,
                    savedExercise.intensityIndex,
                    savedExercise.pace,
                    "",
                    isChecked = false,
                )
                val seriesList = List(savedExercise.series!!.toInt()) {
                    WorkoutSeriesDraft(
                        "",
                        "",
                        savedExercise.loadUnit,
                        isChecked = false
                    )
                }
                workout.add(workout.size, Pair(exercise, seriesList))

                if(savedNotes.isNotEmpty()){
                    val note = savedNotes[index]
                    val workoutHints = WorkoutHints(savedExercise.reps, savedExercise.load, note)
                    this.workoutHints.add(workoutHints)
                }else{
                    val workoutHints = WorkoutHints(savedExercise.reps, savedExercise.load, "Note")
                    this.workoutHints.add(workoutHints)
                }


            }
        }
        val isUnsaved = intent.getBooleanExtra(HomeFragment.IS_UNSAVED, false)
        if (isUnsaved) {
            restoreFromFile()
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
                    isCorrectlyClosed = true
                    isTerminated = false
                    finish()
                } catch (exception: ValidationException) {
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun restoreFromFile() {
        try {
            val file = File(applicationContext.filesDir, "workout_session.json")
            val jsonContent = file.readText()

            val gson = GsonBuilder()
                .registerTypeAdapter(WorkoutSessionSet::class.java, WorkoutSessionSetDeserializer())
                .registerTypeAdapter(
                    object : TypeToken<List<Pair<Int, List<WorkoutSessionSet>>>>() {}.type,
                    CustomPairDeserializer()
                )
                .create()

            val type = object : TypeToken<List<Pair<Int, List<WorkoutSessionSet>>>>() {}.type
            val workoutSession: List<Pair<Int, List<WorkoutSessionSet>>> =
                gson.fromJson(jsonContent, type)

            workoutSession.forEach { pair ->
                val workoutSessionSets = pair.second
                for (workoutSessionSet in workoutSessionSets) {
                    val groupPosition = workoutSessionSet.groupId
                    val childPosition = workoutSessionSet.childId
                    workout[groupPosition].first.note = workoutSessionSet.note
                    workout[groupPosition].second[childPosition].actualReps =
                        workoutSessionSet.actualReps
                    workout[groupPosition].second[childPosition].load = workoutSessionSet.load

                    workout[groupPosition].second[childPosition].isChecked =
                        workoutSessionSet.isChecked
                }
                workout[pair.first].first.isChecked = workoutSessionSets.all { it.isChecked }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

}