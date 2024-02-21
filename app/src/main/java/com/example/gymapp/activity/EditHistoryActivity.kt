package com.example.gymapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Toast
import com.example.gymapp.adapter.EditWorkoutHistoryExpandableListAdapter
import com.example.gymapp.databinding.ActivityEditHistoryBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.fragment.TrainingHistoryFragment
import com.example.gymapp.model.workout.WorkoutExerciseDraft
import com.example.gymapp.model.workout.WorkoutSeriesDraft
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import com.example.gymapp.persistence.WorkoutSeriesDataBaseHelper

class EditHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditHistoryBinding
    private lateinit var expandableListView: ExpandableListView
    private lateinit var editWorkoutHistoryExpandableListAdapter: EditWorkoutHistoryExpandableListAdapter

    private val workout: MutableList<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>> =
        ArrayList()

    private val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(this, null)
    private val workoutSeriesDatabase = WorkoutSeriesDataBaseHelper(this, null)

    private var routineName: String? = null
    private var planName: String? = null
    private var rawDate: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TrainingHistoryFragment.ROUTINE_NAME) && intent.hasExtra(
                TrainingHistoryFragment.FORMATTED_DATE
            )
            && intent.hasExtra(TrainingHistoryFragment.PLAN_NAME) && intent.hasExtra(
                TrainingHistoryFragment.RAW_DATE
            )
        ) {
            routineName = intent.getStringExtra(TrainingHistoryFragment.ROUTINE_NAME)
            planName = intent.getStringExtra(TrainingHistoryFragment.PLAN_NAME)
            rawDate = intent.getStringExtra(TrainingHistoryFragment.RAW_DATE)
            binding.textViewEditHistoryRoutineName.text = routineName
            binding.textViewEditHistoryDate.text =
                intent.getStringExtra(TrainingHistoryFragment.FORMATTED_DATE)

        }

        expandableListView = binding.expandableListViewEditHistory
        editWorkoutHistoryExpandableListAdapter =
            EditWorkoutHistoryExpandableListAdapter(this, workout)
        expandableListView.setAdapter(editWorkoutHistoryExpandableListAdapter)

        loadWorkoutHistoryDetails()

        binding.buttonCancelWorkout.setOnClickListener {
            finish()
        }

        binding.buttonSaveWorkout.setOnClickListener {
            editHistoryDetails()
        }

    }

    private fun loadWorkoutHistoryDetails() {
        val rawDate = this.rawDate
        val routineName = this.routineName
        val planName = this.planName
        if (rawDate != null && routineName != null && planName != null) {
            val workoutExercises =
                workoutHistoryDatabase.getWorkoutExercises(rawDate, routineName, planName)
            for (workoutExercise in workoutExercises) {
                val exerciseId = workoutExercise.exerciseName?.let {
                    workoutHistoryDatabase.getExerciseID(rawDate, it)
                }
                if (exerciseId != null) {
                    val workoutSeries = workoutSeriesDatabase.getSeries(exerciseId)
                    workout.add(Pair(workoutExercise, workoutSeries))
                    editWorkoutHistoryExpandableListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun editHistoryDetails() {
        val exerciseIds = workoutHistoryDatabase.getExerciseIdsByDate(rawDate)
        var success = true
        for (groupPosition in 0 until editWorkoutHistoryExpandableListAdapter.groupCount) {
            try {
                val workoutSeries =
                    editWorkoutHistoryExpandableListAdapter.getWorkoutSeries(groupPosition)
                for (ser in workoutSeries) {
                    workoutSeriesDatabase.updateSeriesValues(
                        exerciseIds[groupPosition],
                        ser.seriesCount, ser.actualReps, ser.load.weight
                    )
                }
            } catch (exception: ValidationException) {
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                success = false
            }

            val note = editWorkoutHistoryExpandableListAdapter.getNoteFromEditText(groupPosition)
            workoutHistoryDatabase.updateNotes(rawDate, exerciseIds[groupPosition], note)
        }
        if (success) {
            Toast.makeText(this, "Workout Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
