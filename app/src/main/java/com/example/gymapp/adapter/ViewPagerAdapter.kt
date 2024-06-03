package com.example.gymapp.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gymapp.activity.MainActivity
import com.example.gymapp.fragment.ChartsFragment
import com.example.gymapp.fragment.HomeFragment
import com.example.gymapp.fragment.SettingsFragment
import com.example.gymapp.fragment.TrainingHistoryFragment
import com.example.gymapp.fragment.TrainingPlansFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragments = SparseArray<Fragment>()
    override fun getItemCount(): Int {
        return MainActivity.NUM_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            CHARTS_FRAGMENT -> ChartsFragment()
            TRAINING_PLANS_FRAGMENT -> TrainingPlansFragment()
            HOME_FRAGMENT -> HomeFragment()
            TRAINING_HISTORY_FRAGMENT -> TrainingHistoryFragment()
            SETTINGS_FRAGMENT -> SettingsFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
        fragments.put(position, fragment)
        return fragment
    }


    companion object{
        const val CHARTS_FRAGMENT = 0
        const val TRAINING_PLANS_FRAGMENT = 1
        const val HOME_FRAGMENT = 2
        const val TRAINING_HISTORY_FRAGMENT = 3
        const val SETTINGS_FRAGMENT = 4
    }
}