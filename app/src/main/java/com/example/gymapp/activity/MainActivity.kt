package com.example.gymapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.gymapp.databinding.ActivityMainBinding
import com.example.gymapp.R

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCreateRoutine.setOnClickListener{
            val explicitIntent = Intent(applicationContext, CreateRoutineActivity::class.java)
            startActivity(explicitIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}

