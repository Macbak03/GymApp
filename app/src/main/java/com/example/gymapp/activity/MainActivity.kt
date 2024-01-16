package com.example.gymapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.gymapp.databinding.ActivityMainBinding
import com.example.gymapp.R
import com.example.gymapp.fragment.HomeFragment
import com.example.gymapp.fragment.SettingsFragment
import com.example.gymapp.fragment.TrainingHistoryFragment
import com.example.gymapp.fragment.TrainingPlansFragment
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.ExercisesDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import com.example.gymapp.persistence.WorkoutSeriesDataBaseHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager

    private val exercisesDataBase = ExercisesDataBaseHelper(this, null)
    private val routinesDataBase = RoutinesDataBaseHelper(this, null)
    private val planDataBase = PlansDataBaseHelper(this, null)
    private val workoutHistoryDataBase = WorkoutHistoryDatabaseHelper(this, null)
    private val workoutSeriesDataBase = WorkoutSeriesDataBaseHelper(this, null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentManager = supportFragmentManager
        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.buttonHome -> openFragment(HomeFragment(), "HomeFragment")
                R.id.buttonTrainingPlans -> openFragment(TrainingPlansFragment(), "TrainingPlansFragment")
                R.id.buttonTrainingHistory -> openFragment(TrainingHistoryFragment(), "TrainingHistoryFragment")
                R.id.buttonSettings -> openFragment(SettingsFragment(), "SettingsFragment")
            }
            true
        }

        openFragment(HomeFragment(), "HomeFragment")
        planDataBase.onCreate(planDataBase.readableDatabase)
        routinesDataBase.onCreate(routinesDataBase.readableDatabase)
        exercisesDataBase.onCreate(exercisesDataBase.readableDatabase)
        workoutHistoryDataBase.onCreate(workoutHistoryDataBase.readableDatabase)
        workoutSeriesDataBase.onCreate(workoutSeriesDataBase.readableDatabase)
    }


    private fun openFragment(fragment: Fragment, tag: String?)
    {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, tag)
        fragmentTransaction.commit()
    }
}

