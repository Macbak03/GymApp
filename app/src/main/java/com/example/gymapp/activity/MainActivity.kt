package com.example.gymapp.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.gymapp.databinding.ActivityMainBinding
import com.example.gymapp.R
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.ExercisesDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    private val exercisesDataBase = ExercisesDataBaseHelper(this, null)
    private val routinesDataBase = RoutinesDataBaseHelper(this, null)
    private val planDataBase = PlansDataBaseHelper(this, null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        planDataBase.onCreate(planDataBase.readableDatabase)
        routinesDataBase.onCreate(routinesDataBase.readableDatabase)
        exercisesDataBase.onCreate(exercisesDataBase.readableDatabase)
        routinesDataBase.setForeignKeys("ON")

        binding.buttonTrainingPlans.setOnClickListener{
            val explicitIntent = Intent(applicationContext, TrainingPlansActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,   // enter animation
                R.anim.slide_out_left    // exit animation
            )
            startActivity(explicitIntent, options.toBundle())
        }
    }
}

