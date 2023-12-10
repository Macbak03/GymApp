package com.example.gymapp.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager

    private val exercisesDataBase = ExercisesDataBaseHelper(this, null)
    private val routinesDataBase = RoutinesDataBaseHelper(this, null)
    private val planDataBase = PlansDataBaseHelper(this, null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentManager = supportFragmentManager
        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.buttonHome -> openFragment(HomeFragment())
                R.id.buttonTrainingPlans -> openFragment(TrainingPlansFragment())
                R.id.buttonTrainingHistory -> openFragment(TrainingHistoryFragment())
                R.id.buttonSettings -> openFragment(SettingsFragment())
            }
            true
        }

        openFragment(HomeFragment())
        planDataBase.onCreate(planDataBase.readableDatabase)
        routinesDataBase.onCreate(routinesDataBase.readableDatabase)
        exercisesDataBase.onCreate(exercisesDataBase.readableDatabase)
        routinesDataBase.setForeignKeys("ON")
        
    }


    private fun openFragment(fragment: Fragment)
    {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}

