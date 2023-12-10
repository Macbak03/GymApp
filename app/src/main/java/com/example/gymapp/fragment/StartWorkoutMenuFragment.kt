package com.example.gymapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.adapter.StartWorkoutMenuRecycleViewAdapter
import com.example.gymapp.adapter.TrainingPlanRecyclerViewAdapter
import com.example.gymapp.databinding.FragmentStartWorkoutMenuBinding
import com.example.gymapp.model.trainingPlans.TrainingPlanElement
import com.example.gymapp.persistence.RoutinesDataBaseHelper

class StartWorkoutMenuFragment : Fragment() {
    private var _binding: FragmentStartWorkoutMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var startWorkoutMenuRecyclerViewAdapter: StartWorkoutMenuRecycleViewAdapter
    private var routines: MutableList<TrainingPlanElement> = ArrayList()
    private lateinit var routinesDataBase: RoutinesDataBaseHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartWorkoutMenuBinding.inflate(layoutInflater, container, false)

        routinesDataBase = RoutinesDataBaseHelper(requireContext(), null)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}