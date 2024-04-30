package com.example.gymapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.activity.HistoryDetailsActivity
import com.example.gymapp.adapter.WorkoutHistoryRecyclerViewAdapter
import com.example.gymapp.animation.FragmentAnimator
import com.example.gymapp.databinding.FragmentTrainingHistoryBinding
import com.example.gymapp.model.workout.CustomDate
import com.example.gymapp.model.workoutHistory.WorkoutHistoryElement
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import java.util.Locale

class TrainingHistoryFragment : Fragment(), FragmentAnimator {
    private var _binding: FragmentTrainingHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutHistoryRecyclerViewAdapter: WorkoutHistoryRecyclerViewAdapter
    private var workoutHistory: MutableList<WorkoutHistoryElement> = ArrayList()
    private lateinit var searchView: SearchView
    private var searchList: MutableList<WorkoutHistoryElement> = ArrayList()


    companion object {
        const val PLAN_NAME = "com.example.gymapp.planname"
        const val ROUTINE_NAME = "com.example.gymapp.routinename"
        const val FORMATTED_DATE = "com.example.gymapp.formatteddate"
        const val RAW_DATE = "com.example.gymapp.rawdate"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingHistoryBinding.inflate(layoutInflater, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = binding.searchViewHistory

        setRecyclerViewContent()

        recyclerView = binding.recyclerViewWorkoutHistory
        workoutHistoryRecyclerViewAdapter = WorkoutHistoryRecyclerViewAdapter(searchList, this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = workoutHistoryRecyclerViewAdapter




        workoutHistoryRecyclerViewAdapter.setOnClickListener(object :
            WorkoutHistoryRecyclerViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: WorkoutHistoryElement) {
                val explicitIntent = Intent(context, HistoryDetailsActivity::class.java)
                explicitIntent.putExtra(PLAN_NAME, model.planName)
                explicitIntent.putExtra(ROUTINE_NAME, model.routineName)
                explicitIntent.putExtra(FORMATTED_DATE, model.formattedDate)
                explicitIntent.putExtra(RAW_DATE, model.rawDate)
                startActivity(explicitIntent)
            }
        })

        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    workoutHistory.forEach {
                        if (it.planName.lowercase(Locale.getDefault())
                                .contains(searchText) || it.routineName.lowercase(Locale.getDefault())
                                .contains(searchText) || it.formattedDate.lowercase(Locale.getDefault())
                                .contains(searchText)
                        ) {
                            searchList.add(it)
                        }
                    }
                    workoutHistoryRecyclerViewAdapter.notifyDataSetChanged()
                } else {
                    searchList.clear()
                    searchList.addAll(workoutHistory)
                    workoutHistoryRecyclerViewAdapter.notifyDataSetChanged()
                }
                return false
            }

        })
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setRecyclerViewContent()
        workoutHistoryRecyclerViewAdapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecyclerViewContent() {
        val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(requireContext(), null)
        workoutHistory = workoutHistoryDatabase.getHistory()
        searchList.clear()
        for(workoutHistoryElement in workoutHistory)
        {
            searchList.add(workoutHistoryElement)
        }
    }

    override fun triggerAnimation() {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
        requireView().startAnimation(slideIn)
    }

}