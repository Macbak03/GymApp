package com.example.gymapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gymapp.activity.MainActivity
import com.example.gymapp.fragment.HomeFragment
import com.example.gymapp.fragment.SettingsFragment
import com.example.gymapp.fragment.TrainingHistoryFragment
import com.example.gymapp.fragment.TrainingPlansFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return MainActivity.NUM_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            HOME_FRAGMENT -> HomeFragment()
            TRAINING_PLANS_FRAGMENT -> TrainingPlansFragment()
            TRAINING_HISTORY_FRAGMENT -> TrainingHistoryFragment()
            SETTINGS_FRAGMENT -> SettingsFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    companion object{
        const val HOME_FRAGMENT = 0
        const val TRAINING_PLANS_FRAGMENT = 1
        const val TRAINING_HISTORY_FRAGMENT = 2
        const val SETTINGS_FRAGMENT = 3
    }
}