package com.example.gymapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import com.example.gymapp.adapter.WorkoutExpandableListAdapter
import com.example.gymapp.databinding.ActivityWorkoutBinding
import com.example.gymapp.fragment.StartWorkoutMenuFragment
import com.example.gymapp.model.routine.ExerciseDraft

class WorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var workoutExpandableListAdapter: WorkoutExpandableListAdapter

    private val exercises: MutableList<ExerciseDraft> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(StartWorkoutMenuFragment.ROUTINE_NAME))
        {
            binding.textViewCurrentWorkout.text = intent.getStringExtra(StartWorkoutMenuFragment.ROUTINE_NAME)
        }

        expandableListView = binding.expandableListViewWorkout
        workoutExpandableListAdapter = WorkoutExpandableListAdapter(this, exercises)
        expandableListView.setAdapter(workoutExpandableListAdapter)
    }
}