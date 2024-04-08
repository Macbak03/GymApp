package com.example.gymapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.example.gymapp.databinding.ActivityMainBinding
import com.example.gymapp.R
import com.example.gymapp.adapter.ViewPagerAdapter
import com.example.gymapp.animation.FragmentAnimator
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.ExercisesDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import com.example.gymapp.persistence.WorkoutSeriesDataBaseHelper


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager

    private val exercisesDataBase = ExercisesDataBaseHelper(this, null)
    private val routinesDataBase = RoutinesDataBaseHelper(this, null)
    private val planDataBase = PlansDataBaseHelper(this, null)
    private val workoutHistoryDataBase = WorkoutHistoryDatabaseHelper(this, null)
    private val workoutSeriesDataBase = WorkoutSeriesDataBaseHelper(this, null)
    override fun onCreate(savedInstanceState: Bundle?) {
        loadTheme()
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
            val targetItem = when (item.itemId) {
                R.id.buttonCharts -> ViewPagerAdapter.CHARTS_FRAGMENT
                R.id.buttonTrainingPlans -> ViewPagerAdapter.TRAINING_PLANS_FRAGMENT
                R.id.buttonHome ->  ViewPagerAdapter.HOME_FRAGMENT
                R.id.buttonTrainingHistory -> ViewPagerAdapter.TRAINING_HISTORY_FRAGMENT
                R.id.buttonSettings -> ViewPagerAdapter.SETTINGS_FRAGMENT
                else -> return@setOnItemSelectedListener false
            }
            viewPager.setCurrentItem(targetItem, false)
            true
        }

        viewPager.setCurrentItem(2, false)
        planDataBase.onCreate(planDataBase.readableDatabase)
        routinesDataBase.onCreate(routinesDataBase.readableDatabase)
        exercisesDataBase.onCreate(exercisesDataBase.readableDatabase)
        workoutHistoryDataBase.onCreate(workoutHistoryDataBase.readableDatabase)
        workoutSeriesDataBase.onCreate(workoutSeriesDataBase.readableDatabase)
        workoutHistoryDataBase.onUpgrade(workoutHistoryDataBase.writableDatabase, workoutHistoryDataBase.writableDatabase.version, 38)
        exercisesDataBase.onUpgrade(exercisesDataBase.writableDatabase, exercisesDataBase.writableDatabase.version, 38)
    }

    override fun onResume() {
        loadTheme()
        super.onResume()
    }

    companion object {
        const val NUM_PAGES = 5
    }
}

