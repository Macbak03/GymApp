package com.example.gymapp.tutorial.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.gymapp.R
import com.example.gymapp.activity.MainActivity
import com.example.gymapp.databinding.FragmentCreatePlanSlideBinding
import com.example.gymapp.fragment.SplashFragment

class CreatePlanSlide : SplashFragment() {
    private var _binding: FragmentCreatePlanSlideBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlanSlideBinding.inflate(layoutInflater, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.tutorialViewPager)

        binding.nextPlanTutorial.setOnClickListener {
            viewPager?.currentItem = 1
        }

        binding.skipPlanTutorial.setOnClickListener {
            setTutorialFinished()
            openMainActivity()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}