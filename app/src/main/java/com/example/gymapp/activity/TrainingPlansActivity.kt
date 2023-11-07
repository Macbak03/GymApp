package com.example.gymapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.TrainingPlansRecyclerViewAdapter
import com.example.gymapp.databinding.ActivityTrainingPlansBinding
import com.example.gymapp.model.trainingPlans.TrainingPlanName

class TrainingPlansActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingPlansBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlansRecyclerViewAdapter: TrainingPlansRecyclerViewAdapter

    private val trainingPlansNames: MutableList<TrainingPlanName> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingPlansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerViewTrainingPlans
        val name = TrainingPlanName("chuj")
        trainingPlansNames.add(name)
        trainingPlansRecyclerViewAdapter = TrainingPlansRecyclerViewAdapter(trainingPlansNames)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = trainingPlansRecyclerViewAdapter
    }

}