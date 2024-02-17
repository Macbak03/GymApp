package com.example.gymapp.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.gymapp.databinding.ActivityMainBinding
import com.example.gymapp.R
import com.example.gymapp.adapter.ViewPagerAdapter
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

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNavigationBar.menu.getItem(position).isChecked = true
            }
        })

        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.buttonHome ->  viewPager.currentItem = 0
                R.id.buttonTrainingPlans -> viewPager.currentItem = 1
                R.id.buttonTrainingHistory -> viewPager.currentItem = 2

                R.id.buttonSettings -> viewPager.currentItem = 3
            }
            true
        }
        planDataBase.onCreate(planDataBase.readableDatabase)
        routinesDataBase.onCreate(routinesDataBase.readableDatabase)
        exercisesDataBase.onCreate(exercisesDataBase.readableDatabase)
        workoutHistoryDataBase.onCreate(workoutHistoryDataBase.readableDatabase)
        workoutSeriesDataBase.onCreate(workoutSeriesDataBase.readableDatabase)
    }

    companion object {
        const val NUM_PAGES = 4
    }
}

