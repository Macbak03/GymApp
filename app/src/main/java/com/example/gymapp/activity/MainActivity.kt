package com.example.gymapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.gymapp.databinding.ActivityMainBinding
import com.example.gymapp.R
import com.example.gymapp.persistence.PlanDataBaseHelper
import com.example.gymapp.persistence.RoutineDataBaseHelper

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    private val routineDataBase = RoutineDataBaseHelper(this, null)
    private val planDataBase = PlanDataBaseHelper(this, null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        routineDataBase.onCreate(routineDataBase.readableDatabase)
        planDataBase.onCreate(planDataBase.readableDatabase)
        planDataBase.setForeignKeys("ON")

        binding.buttonTrainingPlans.setOnClickListener{
            val explicitIntent = Intent(applicationContext, TrainingPlansActivity::class.java)
            startActivity(explicitIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}

