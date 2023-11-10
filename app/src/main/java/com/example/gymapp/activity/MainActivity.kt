package com.example.gymapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.gymapp.databinding.ActivityMainBinding


class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonTrainingPlans.setOnClickListener{
            val explicitIntent = Intent(applicationContext, TrainingPlansActivity::class.java)
            startActivity(explicitIntent)
        }
    }
}

