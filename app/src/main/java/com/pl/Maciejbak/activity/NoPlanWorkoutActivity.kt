package com.pl.Maciejbak.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pl.Maciejbak.adapter.NoPlanWorkoutExpandableListAdapter
import com.pl.Maciejbak.databinding.ActivityNoPlanWorkoutBinding
import com.pl.Maciejbak.exception.ValidationException
import com.pl.Maciejbak.fragment.HomeFragment
import com.pl.Maciejbak.model.json.NoPlanWorkoutSessionSetListDeserializer
import com.pl.Maciejbak.model.json.WorkoutSessionSetDeserializer
import com.pl.Maciejbak.model.routine.IntensityIndex
import com.pl.Maciejbak.model.routine.TimeUnit
import com.pl.Maciejbak.model.routine.WeightUnit
import com.pl.Maciejbak.model.workout.CustomDate
import com.pl.Maciejbak.model.workout.NoPlanWorkoutSessionExercise
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft
import com.pl.Maciejbak.model.workout.WorkoutSessionSet
import com.pl.Maciejbak.persistence.WorkoutHistoryDatabaseHelper
import java.io.File

class NoPlanWorkoutActivity : WorkoutBaseActivity() {
    private lateinit var binding: ActivityNoPlanWorkoutBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var noPlanWorkoutExpandableListAdapter: NoPlanWorkoutExpandableListAdapter
    private var workout: MutableList<Pair<WorkoutExerciseDraft, MutableList<WorkoutSeriesDraft>>> =
        ArrayList()

    private var weightUnit = WeightUnit.kg
    private val defaultWorkoutValue = "0"
    private val defaultPaceValue = "0000"

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            noPlanWorkoutExpandableListAdapter.saveNoPlanSessionToFile()
            isTerminated = false
            val resultIntent = Intent()
            val currentWorkoutName = binding.editTextWorkoutName.text
            resultIntent.putExtra(HomeFragment.ROUTINE_NAME, currentWorkoutName.toString())
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
        loadUnitSettings()
        super.onCreate(savedInstanceState)
        binding = ActivityNoPlanWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadWorkout()

        planName = HomeFragment.NO_TRAINING_PLAN_OPTION

        expandableListView = binding.expandableListViewWorkout
        noPlanWorkoutExpandableListAdapter =
            NoPlanWorkoutExpandableListAdapter(this, workout, weightUnit)
        expandableListView.setAdapter(noPlanWorkoutExpandableListAdapter)

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

        binding.editTextWorkoutName.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                routineName = s.toString()
            }

        })
    }

    override fun onStop() {
        super.onStop()
        val prefs = getSharedPreferences("TerminatePreferences", Context.MODE_PRIVATE)
        if (isCorrectlyClosed) {
            prefs.edit().clear().apply()
        } else if (isTerminated) {
            prefs.edit().putString("ROUTINE_NAME", binding.editTextWorkoutName.text.toString())
                .apply()
            noPlanWorkoutExpandableListAdapter.saveNoPlanSessionToFile()
        }
    }

    private fun loadWorkout() {
        workout.clear()
        val exercise = WorkoutExerciseDraft(
            "",
            defaultWorkoutValue,
            TimeUnit.s,
            defaultWorkoutValue,
            defaultWorkoutValue,
            defaultWorkoutValue,
            IntensityIndex.RPE,
            defaultPaceValue,
            "",
            false
        )
        val seriesList = ArrayList<WorkoutSeriesDraft>()
        seriesList.add(WorkoutSeriesDraft("", "", weightUnit, false))
        workout.add(Pair(exercise, seriesList))

        val isUnsaved = intent.getBooleanExtra(HomeFragment.IS_UNSAVED, false)
        val isNewWorkoutWithoutCancel =
            intent.getBooleanExtra(HomeFragment.IS_NEW_WORKOUT_STARTED_WITHOUT_CANCEL, false)
        if (isUnsaved && !isNewWorkoutWithoutCancel) {
            workout.clear()
            restoreFromFile()
            val prefs = getSharedPreferences("TerminatePreferences", Context.MODE_PRIVATE)
            routineName = prefs.getString("ROUTINE_NAME", "")
            binding.editTextWorkoutName.setText(routineName)
        }
    }

    private fun restoreFromFile() {
        try {
            val file = File(applicationContext.filesDir, "workout_session.json")
            val jsonContent = file.readText()

            val gson = GsonBuilder()
                .registerTypeAdapter(WorkoutSessionSet::class.java, WorkoutSessionSetDeserializer())
                .registerTypeAdapter(
                    object : TypeToken<List<Pair<Int, NoPlanWorkoutSessionExercise>>>() {}.type,
                    NoPlanWorkoutSessionSetListDeserializer()
                )
                .create()

            val type = object : TypeToken<List<Pair<Int, NoPlanWorkoutSessionExercise>>>() {}.type
            val workoutSession: List<Pair<Int, NoPlanWorkoutSessionExercise>> =
                gson.fromJson(jsonContent, type)

            workoutSession.forEach { pair ->
                val workoutSessionSets = pair.second.workoutSessionExerciseSets
                val exerciseName = pair.second.exerciseName
                val workoutExerciseDraft = WorkoutExerciseDraft(
                    exerciseName,
                    defaultWorkoutValue,
                    TimeUnit.s,
                    defaultWorkoutValue,
                    defaultWorkoutValue,
                    defaultWorkoutValue,
                    IntensityIndex.RPE,
                    defaultPaceValue,
                    "",
                    false,
                )
                val workoutSeriesDraftList = ArrayList<WorkoutSeriesDraft>()
                for (workoutSessionSet in workoutSessionSets) {
                    val workoutSeriesDraft = WorkoutSeriesDraft(
                        workoutSessionSet.actualReps,
                        workoutSessionSet.load,
                        WeightUnit.kg,
                        workoutSessionSet.isChecked
                    )
                    workoutExerciseDraft.note = workoutSessionSet.note
                    workoutSeriesDraftList.add(workoutSeriesDraft)
                }
                workout.add(Pair(workoutExerciseDraft, workoutSeriesDraftList))
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun saveWorkoutToHistory(date: String) {
        if (routineName != null && planName != null) {
            val planName = this.planName
            val routineName = this.routineName
            if (planName != null && routineName != null) {
                try {
                    val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(this, null)
                    workoutHistoryDatabase.addNoPlanExercises(
                        noPlanWorkoutExpandableListAdapter,
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

    private fun loadUnitSettings() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        when (sharedPreferences.getString("unit", "")) {
            "kg" -> weightUnit = WeightUnit.kg
            "lbs" -> weightUnit = WeightUnit.lbs
        }
    }
}