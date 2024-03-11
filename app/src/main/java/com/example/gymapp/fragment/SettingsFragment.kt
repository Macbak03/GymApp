package com.example.gymapp.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.gymapp.R
import com.example.gymapp.animation.FragmentAnimator

class SettingsFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listPreference : ListPreference? = findPreference("theme")


        listPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                loadTheme()
                true
            }
    }

    private fun loadTheme(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())

        when (sharedPreferences.getString("theme", "")) {
            "Dark" -> requireActivity().setTheme(R.style.Theme_Dark)
            "DarkBlue" -> requireActivity().setTheme(R.style.Theme_DarkBlue)
            else -> requireActivity().setTheme(R.style.Theme_Dark)
        }
        requireActivity().recreate()
    }

}