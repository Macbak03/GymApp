package com.example.gymapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.TrainingPlansRecyclerViewAdapter
import com.example.gymapp.databinding.ActivityTrainingPlansBinding
import com.example.gymapp.model.trainingPlans.TrainingPlan

class TrainingPlansActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingPlansBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlansRecyclerViewAdapter: TrainingPlansRecyclerViewAdapter

    private val trainingPlansNames: MutableList<TrainingPlan> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingPlansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerViewTrainingPlans
        val name = TrainingPlan("You don't have any training plans yet")
        trainingPlansNames.add(name)
        trainingPlansRecyclerViewAdapter = TrainingPlansRecyclerViewAdapter(trainingPlansNames)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = trainingPlansRecyclerViewAdapter

        binding.buttonCreateTrainingPlan.setOnClickListener()
        {
            val explicitIntent = Intent(applicationContext, TrainingPlanActivity::class.java)
            startActivity(explicitIntent)
        }
    }

}