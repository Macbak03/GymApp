package com.example.gymapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.activity.CreateRoutineActivity
import com.example.gymapp.activity.TrainingPlanActivity
import com.example.gymapp.activity.WorkoutActivity
import com.example.gymapp.adapter.StartWorkoutMenuRecycleViewAdapter
import com.example.gymapp.adapter.TrainingPlanRecyclerViewAdapter
import com.example.gymapp.databinding.FragmentStartWorkoutMenuBinding
import com.example.gymapp.model.trainingPlans.TrainingPlanElement
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper

class StartWorkoutMenuFragment : Fragment() {
    private var _binding: FragmentStartWorkoutMenuBinding? = null
    private val binding get() = _binding!!
    private var homeFragment: HomeFragment? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var startWorkoutMenuRecyclerViewAdapter: StartWorkoutMenuRecycleViewAdapter
    private var routines: MutableList<TrainingPlanElement> = ArrayList()

    companion object {
        const val ROUTINE_NAME = "com.example.gymapp.routinename"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartWorkoutMenuBinding.inflate(layoutInflater, container, false)
        homeFragment = parentFragmentManager.findFragmentByTag("HomeFragment") as? HomeFragment
        homeFragment?.let {
            val spinner = it.view?.findViewById<Spinner>(R.id.spinnerTrainingPlans)
            val button = it.view?.findViewById<Button>(R.id.buttonStartWorkout)
            val textView = it.view?.findViewById<TextView>(R.id.textViewCurrentTrainingPlan)
            button?.visibility = View.GONE
            spinner?.isEnabled= false
            textView?.isEnabled= false
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.RecyclerViewStartWorkoutMenu
        val chosenTrainingPlan = arguments?.getString("SELECTED_ITEM_KEY")
        setRecyclerViewContent(chosenTrainingPlan)
        startWorkoutMenuRecyclerViewAdapter =  StartWorkoutMenuRecycleViewAdapter(routines)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = startWorkoutMenuRecyclerViewAdapter

        startWorkoutMenuRecyclerViewAdapter.setOnClickListener(object :
            StartWorkoutMenuRecycleViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: TrainingPlanElement) {
                val explicitIntent = Intent(context, WorkoutActivity::class.java)
                explicitIntent.putExtra(ROUTINE_NAME, model.routineName)
                startActivity(explicitIntent)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeFragment?.let {
            val spinner = it.view?.findViewById<Spinner>(R.id.spinnerTrainingPlans)
            val button = it.view?.findViewById<Button>(R.id.buttonStartWorkout)
            val textView = it.view?.findViewById<TextView>(R.id.textViewCurrentTrainingPlan)
            button?.visibility = View.VISIBLE
            spinner?.isEnabled= true
            textView?.isEnabled= true
        }
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecyclerViewContent(planName: String?) {
        val plansDataBase = PlansDataBaseHelper(requireContext(), null)
        val routinesDataBase = RoutinesDataBaseHelper(requireContext(), null)
        if(planName != null) {
            val planId = plansDataBase.getValue(
                PlansDataBaseHelper.TABLE_NAME,
                PlansDataBaseHelper.PLAN_ID_COLUMN,
                PlansDataBaseHelper.PLAN_NAME_COLUMN,
                planName
            )?.toInt()
            if (planId != null) {
                val cursor = routinesDataBase.getRoutinesInPlan(planId)
                if (cursor.moveToFirst()) {
                    routines.add(TrainingPlanElement(cursor.getString(cursor.getColumnIndexOrThrow(RoutinesDataBaseHelper.ROUTINE_NAME_COLUMN))))
                    while (cursor.moveToNext()) {
                        routines.add(TrainingPlanElement(cursor.getString(cursor.getColumnIndexOrThrow(RoutinesDataBaseHelper.ROUTINE_NAME_COLUMN))))

                    }
                }
            }
        }

    }

}