package com.example.gymapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.TrainingPlanRecyclerViewAdapter
import com.example.gymapp.databinding.ActivityTrainingPlanBinding
import com.example.gymapp.model.trainingPlans.TrainingPlanElement

class TrainingPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingPlanBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlanRecyclerViewAdapter: TrainingPlanRecyclerViewAdapter

    private val routines: MutableList<TrainingPlanElement> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.RecyclerViewTrainingPlan
        val routine = TrainingPlanElement("This plan doesn't contain any routines yet")
        routines.add(routine)
        trainingPlanRecyclerViewAdapter = TrainingPlanRecyclerViewAdapter(routines)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = trainingPlanRecyclerViewAdapter


        binding.buttonAddRoutine.setOnClickListener()
        {
            val explicitIntent = Intent(applicationContext, CreateRoutineActivity::class.java)
            startActivity(explicitIntent)
        }
    }
}