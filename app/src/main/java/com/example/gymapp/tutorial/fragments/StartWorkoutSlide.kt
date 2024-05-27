package com.example.gymapp.tutorial.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.gymapp.R
import com.example.gymapp.activity.MainActivity
import com.example.gymapp.databinding.FragmentStartWorkoutSlideBinding
import com.example.gymapp.fragment.SplashFragment

class StartWorkoutSlide : SplashFragment() {
    private var _binding: FragmentStartWorkoutSlideBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartWorkoutSlideBinding.inflate(layoutInflater, container, false)

        binding.finish.setOnClickListener {
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