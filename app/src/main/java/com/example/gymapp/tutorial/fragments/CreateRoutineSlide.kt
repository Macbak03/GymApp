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
import com.example.gymapp.databinding.FragmentCreateRoutineSlideBinding
import com.example.gymapp.fragment.SplashFragment

class CreateRoutineSlide : SplashFragment() {
    private var _binding: FragmentCreateRoutineSlideBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRoutineSlideBinding.inflate(layoutInflater, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.tutorialViewPager)

        binding.nextRoutineTutorial.setOnClickListener {
            viewPager?.currentItem = 2
        }

        binding.skipRoutineTutorial.setOnClickListener {
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