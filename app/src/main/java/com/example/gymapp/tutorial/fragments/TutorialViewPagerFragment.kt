package com.example.gymapp.tutorial.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gymapp.R
import com.example.gymapp.activity.MainActivity
import com.example.gymapp.adapter.ViewPagerAdapter
import com.example.gymapp.databinding.FragmentHomeBinding
import com.example.gymapp.databinding.FragmentTutorialViewPagerBinding
import com.example.gymapp.tutorial.adapter.TutorialViewPagerAdapter

class TutorialViewPagerFragment : Fragment() {

    private var _binding: FragmentTutorialViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorialViewPagerBinding.inflate(layoutInflater, container, false)

        val fragmentList = arrayListOf(
            CreatePlanSlide(),
            CreateRoutineSlide(),
            StartWorkoutSlide()
        )

        val adapter = TutorialViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.tutorialViewPager.adapter = adapter

        binding.indicator.setViewPager(binding.tutorialViewPager)

        adapter.registerAdapterDataObserver(binding.indicator.adapterDataObserver)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PACKAGE_NAME = "com.example.gymapp"
    }
}