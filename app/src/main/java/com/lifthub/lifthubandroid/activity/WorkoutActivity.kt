package com.lifthub.lifthubandroid.activity

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
import com.lifthub.lifthubandroid.R
import com.lifthub.lifthubandroid.adapter.WorkoutExpandableListAdapter
import com.lifthub.lifthubandroid.databinding.ActivityWorkoutBinding
import com.lifthub.lifthubandroid.exception.ValidationException
import com.lifthub.lifthubandroid.fragment.HomeFragment
import com.lifthub.lifthubandroid.fragment.StartWorkoutMenuFragment
import com.lifthub.lifthubandroid.model.CustomPairDeserializer
import com.lifthub.lifthubandroid.model.WorkoutSessionSetDeserializer
import com.lifthub.lifthubandroid.model.workout.CustomDate
import com.lifthub.lifthubandroid.model.workout.WorkoutSeriesDraft
import com.lifthub.lifthubandroid.model.workout.WorkoutExerciseDraft
import com.lifthub.lifthubandroid.model.workout.WorkoutHints
import com.lifthub.lifthubandroid.model.workout.WorkoutSessionSet
import com.lifthub.lifthubandroid.persistence.ExercisesDataBaseHelper
import com.lifthub.lifthubandroid.persistence.PlansDataBaseHelper
import com.lifthub.lifthubandroid.persistence.WorkoutHistoryDatabaseHelper
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class WorkoutActivity : BaseActivity() {

    private lateinit var binding: ActivityWorkoutBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var workoutExpandableListAdapter: WorkoutExpandableListAdapter
    private val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(this, null)
    private var workout: MutableList<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>> =
        ArrayList()
    private var workoutHints: MutableList<WorkoutHints> = ArrayList()
    private var routineName: String? = null
    private var planName: String? = null

    private var isCorrectlyClosed = false
    private var isTerminated = true


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            workoutExpandableListAdapter.saveToFile()
            isTerminated = false
            val resultIntent = Intent()
            resultIntent.putExtra(HomeFragment.ROUTINE_NAME, binding.textViewCurrentWorkout.text)
            val prefs = getSharedPreferences("TerminatePreferences", Context.MODE_PRIVATE)
            prefs.edit().putString("ROUTINE_NAME", binding.textViewCurrentWorkout.text.toString())
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    showPermissionExplanation()
                    if (this@WorkoutActivity.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                        val explicitIntent = Intent(applicationContext, TimerActivity::class.java)
                        startActivity(explicitIntent)
                    }
                } else {
                    val explicitIntent = Intent(applicationContext, TimerActivity::class.java)
                    startActivity(explicitIntent)
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        binding.goBackButton.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottom)
            insets
        }
    }

    private fun View.setTimerButtonBackground(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        when (sharedPreferences.getString("theme", "")) {
            "Default" -> setBackgroundResource(R.drawable.clicked_default_button)
            "Dark" -> setBackgroundResource(R.drawable.dark_button_color)
            "DarkBlue" -> setBackgroundResource(R.drawable.button_color)
            else -> setBackgroundResource(R.drawable.clicked_default_button)
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

    private fun showPermissionExplanation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!this.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                android.app.AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This function requires the ability to schedule exact alarms to function properly. Please allow this permission in the settings.")
                    .setPositiveButton("Settings") { _, _ ->
                        val intent =
                            Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
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

    private fun showCancelDialog() {
        val builder = this.let { AlertDialog.Builder(it, R.style.YourAlertDialogTheme) }
        with(builder) {
            this.setTitle("Are you sure you want to cancel this training? It won't be saved.")
            this.setPositiveButton("Yes") { _, _ ->
                setResult(RESULT_OK)
                isCorrectlyClosed = true
                isTerminated = false
                finish()
            }
            this.setNegativeButton("No") { _, _ -> }
            this.show()
        }
    }

}