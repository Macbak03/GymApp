package com.example.gymapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import com.example.gymapp.adapter.WorkoutHistoryExpandableListAdapter
import com.example.gymapp.databinding.ActivityHistoryDetailsBinding
import com.example.gymapp.fragment.TrainingHistoryFragment
import com.example.gymapp.model.workout.WorkoutExerciseDraft
import com.example.gymapp.model.workout.WorkoutSeriesDraft
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import com.example.gymapp.persistence.WorkoutSeriesDataBaseHelper

class HistoryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailsBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var workoutExpandableListAdapter: WorkoutHistoryExpandableListAdapter
    private val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(this, null)
    private val workoutSeriesDatabase = WorkoutSeriesDataBaseHelper(this, null)
    private val workout: MutableList<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>> = ArrayList()
    private var routineName: String? = null
    private var planName: String? = null
    private var rawDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(TrainingHistoryFragment.ROUTINE_NAME) && intent.hasExtra(TrainingHistoryFragment.FORMATTED_DATE)
            && intent.hasExtra(TrainingHistoryFragment.PLAN_NAME) && intent.hasExtra(TrainingHistoryFragment.RAW_DATE))
        {
            routineName = intent.getStringExtra(TrainingHistoryFragment.ROUTINE_NAME)
            planName = intent.getStringExtra(TrainingHistoryFragment.PLAN_NAME)
            rawDate = intent.getStringExtra(TrainingHistoryFragment.RAW_DATE)
            binding.textViewHistoryDetailRoutineName.text = routineName
            binding.textViewHistoryDetailDate.text = intent.getStringExtra(TrainingHistoryFragment.FORMATTED_DATE)

        }

        expandableListView = binding.expandableListViewHistoryDetails
        workoutExpandableListAdapter = WorkoutHistoryExpandableListAdapter(this, workout)
        expandableListView.setAdapter(workoutExpandableListAdapter)

        loadWorkoutHistoryDetails()
    }

    private fun loadWorkoutHistoryDetails()
    {
        val rawDate = this.rawDate
        val routineName = this.routineName
        val planName = this.planName
        if(rawDate != null && routineName != null && planName != null)
        {
            val workoutExercises = workoutHistoryDatabase.getWorkoutExercises(rawDate, routineName, planName)
            for (workoutExercise in workoutExercises)
            {
                val exerciseId = workoutExercise.exerciseName?.let {
                    workoutHistoryDatabase.getExerciseID(rawDate, it)
                }
                if(exerciseId != null)
                {
                    val workoutSeries = workoutSeriesDatabase.getSeries(exerciseId)
                    workout.add(Pair(workoutExercise, workoutSeries))
                    workoutExpandableListAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}