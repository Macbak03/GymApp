package com.example.gymapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.gymapp.R
import com.example.gymapp.adapter.WorkoutExpandableListAdapter
import com.example.gymapp.databinding.ActivityWorkoutBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.fragment.HomeFragment
import com.example.gymapp.fragment.StartWorkoutMenuFragment
import com.example.gymapp.model.CustomPairDeserializer
import com.example.gymapp.model.WorkoutSessionSetDeserializer
import com.example.gymapp.model.workout.CustomDate
import com.example.gymapp.model.workout.WorkoutSeriesDraft
import com.example.gymapp.model.workout.WorkoutExerciseDraft
import com.example.gymapp.model.workout.WorkoutSessionSet
import com.example.gymapp.persistence.ExercisesDataBaseHelper
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter

class WorkoutActivity : BaseActivity() {

    private lateinit var binding: ActivityWorkoutBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var workoutExpandableListAdapter: WorkoutExpandableListAdapter
    private val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(this, null)
    private var workout: MutableList<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>> =
        ArrayList()
    private var routineName: String? = null
    private var planName: String? = null

    private var isCorrectlyClosed = false
    private var isTerminated = true

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            /* val workRequest = OneTimeWorkRequestBuilder<SaveSeriesWorker>().build()
             WorkManager.getInstance(this@WorkoutActivity).enqueue(workRequest)*/
            //saveSeries()
            //save()
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
        workoutExpandableListAdapter = WorkoutExpandableListAdapter(this, workout)
        expandableListView.setAdapter(workoutExpandableListAdapter)


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
        val prefs = getSharedPreferences("TerminatePreferences", Context.MODE_PRIVATE)
        if (isCorrectlyClosed) {
            prefs.edit().clear().apply()
        } else if (isTerminated) {
            prefs.edit().putString("ROUTINE_NAME", binding.textViewCurrentWorkout.text.toString())
                .apply()
            /*            val workRequest = OneTimeWorkRequestBuilder<SaveSeriesWorker>().build()
                        WorkManager.getInstance(this).enqueue(workRequest)*/
            //saveSeries()
            //save()
            workoutExpandableListAdapter.saveToFile()
        }
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
                workout.add(workout.size, Pair(exercise, seriesList))
            }
        }
        val isUnsaved = intent.getBooleanExtra(HomeFragment.IS_UNSAVED, false)
        if (isUnsaved) {
            restore()
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

    private fun save() {
        val workoutSession = workoutExpandableListAdapter.getWorkoutSession()
        if (workoutSession.isNotEmpty()) {
            val gson = Gson()
            val jsonData = gson.toJson(workoutSession)

            try {
                val file = File(applicationContext.filesDir, "workout_session.json")
                val writer = FileWriter(file, false)
                writer.write(jsonData)
                writer.close()
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }


    private fun restore() {
        try {
            val file = File(applicationContext.filesDir, "workout_session.json")
            val jsonContent = file.readText()

            val gson = GsonBuilder()
                .registerTypeAdapter(WorkoutSessionSet::class.java, WorkoutSessionSetDeserializer())
                .registerTypeAdapter(object : TypeToken<List<Pair<Int, List<WorkoutSessionSet>>>>() {}.type, CustomPairDeserializer())
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
                }
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

    inner class SaveSeriesWorker(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {
        override fun doWork(): Result {
            //saveSeries()
            return Result.success()
        }
    }

}