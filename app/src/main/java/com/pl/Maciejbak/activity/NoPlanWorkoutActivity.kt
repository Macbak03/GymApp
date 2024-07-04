package com.pl.Maciejbak.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ExpandableListView
import androidx.activity.OnBackPressedCallback
import androidx.preference.PreferenceManager
import com.pl.Maciejbak.adapter.NoPlanWorkoutExpandableListAdapter
import com.pl.Maciejbak.databinding.ActivityNoPlanWorkoutBinding
import com.pl.Maciejbak.fragment.HomeFragment
import com.pl.Maciejbak.model.routine.IntensityIndex
import com.pl.Maciejbak.model.routine.TimeUnit
import com.pl.Maciejbak.model.routine.WeightUnit
import com.pl.Maciejbak.model.workout.CustomDate
import com.pl.Maciejbak.model.workout.NoPlanWorkoutExercise
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft

class NoPlanWorkoutActivity : WorkoutBaseActivity() {
    private lateinit var binding: ActivityNoPlanWorkoutBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var noPlanWorkoutExpandableListAdapter: NoPlanWorkoutExpandableListAdapter
    private var workout: MutableList<Pair<WorkoutExerciseDraft, MutableList<WorkoutSeriesDraft>>> =
        ArrayList()

    private var weightUnit = WeightUnit.kg

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //noPlanWorkoutExpandableListAdapter.saveToFile()
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

        expandableListView = binding.expandableListViewWorkout
        noPlanWorkoutExpandableListAdapter =
            NoPlanWorkoutExpandableListAdapter(this, workout, weightUnit)
        expandableListView.setAdapter(noPlanWorkoutExpandableListAdapter)

        /*        binding.buttonSaveWorkout.setOnClickListener {
                    val customDate = CustomDate()
                    val date = customDate.getDate()
                    saveWorkoutToHistory(date)
                }*/

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

    private fun loadWorkout() {
        workout.clear()
        val isUnsaved = intent.getBooleanExtra(HomeFragment.IS_UNSAVED, false)
        if (isUnsaved) {
            restoreFromFile()
        } else {
            val exercise = WorkoutExerciseDraft(
                "",
                null,
                TimeUnit.s,
                null,
                null,
                null,
                IntensityIndex.RPE,
                null,
                "",
                false
            )
            val seriesList = ArrayList<WorkoutSeriesDraft>()
            seriesList.add(WorkoutSeriesDraft("", "", weightUnit, false))
            workout.add(Pair(exercise, seriesList))
        }
    }

    private fun restoreFromFile() {

    }

    private fun loadUnitSettings() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        when (sharedPreferences.getString("unit", "")) {
            "kg" -> weightUnit = WeightUnit.kg
            "lbs" -> weightUnit = WeightUnit.lbs
        }

    }
}