package com.example.gymapp.fragment

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.preference.PreferenceFragmentCompat
import com.example.gymapp.R
import com.example.gymapp.animation.FragmentAnimator

class SettingsFragment : PreferenceFragmentCompat(), FragmentAnimator {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
    override fun triggerAnimation() {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
        requireView().startAnimation(slideIn)
    }
}