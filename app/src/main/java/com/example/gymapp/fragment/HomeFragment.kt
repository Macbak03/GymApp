package com.example.gymapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.FragmentTransaction
import com.example.gymapp.R
import com.example.gymapp.activity.TrainingPlanActivity
import com.example.gymapp.databinding.FragmentHomeBinding
import com.example.gymapp.model.trainingPlans.TrainingPlan
import com.example.gymapp.model.workout.CustomDate
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var plansDataBase: PlansDataBaseHelper
    private var trainingPlansNames: MutableList<TrainingPlan> = ArrayList()
    private lateinit var spinner: Spinner
    private val SPINNER_PREF_KEY = "selectedSpinnerItem"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner = binding.spinnerTrainingPlans
        plansDataBase = PlansDataBaseHelper(requireContext(), null)
        val trainingPlanNamesString = plansDataBase.getColumn(
            PlansDataBaseHelper.TABLE_NAME,
            PlansDataBaseHelper.PLAN_NAME_COLUMN,
            PlansDataBaseHelper.PLAN_ID_COLUMN
        )
        trainingPlansNames = plansDataBase.convertList(trainingPlanNamesString) { TrainingPlan(it) }
        if (!plansDataBase.isTableNotEmpty()) {
            val noneTrainingPlanFound = "Go to training plans section to create your first plan"
            binding.textViewCurrentTrainingPlan.text = noneTrainingPlanFound
            spinner.visibility = View.GONE
        } else {
            spinner.visibility = View.VISIBLE
        }
        initSpinner()
        val routinesDataBase = RoutinesDataBaseHelper(requireContext(), null)
        binding.buttonStartWorkout.setOnClickListener {
            val planName = spinner.selectedItem.toString()
            val planId = plansDataBase.getPlanId(planName)
            if (!plansDataBase.isTableNotEmpty()) {
                openTrainingPlansFragment()
            } else if (spinner.selectedItem != null && planId != null && !routinesDataBase.isPlanNotEmpty(
                    planId.toString()
                )
            ) {
                openRoutinesActivity(planName)
            } else {
                openStartWorkoutMenuFragment()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setLastTraining()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openStartWorkoutMenuFragment() {
        val selectedSpinnerItem = spinner.selectedItem as TrainingPlan?
        if (selectedSpinnerItem != null) {
            val bundle = Bundle()
            bundle.putString("SELECTED_ITEM_KEY", selectedSpinnerItem.name)

            val startWorkoutMenuFragment = StartWorkoutMenuFragment()
            startWorkoutMenuFragment.arguments = bundle

            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.startWorkoutMenu, startWorkoutMenuFragment, "WorkoutMenu")
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun initSpinner() {
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_header, trainingPlansNames)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.adapter = adapter

        val savedSelection = loadSpinnerSelection()
        if (savedSelection.isNotEmpty()) {
            val selectionIndex = trainingPlansNames.indexOfFirst { it.name == savedSelection }
            if (selectionIndex != AdapterView.INVALID_POSITION) {
                spinner.setSelection(selectionIndex)
            }
        }

        with(spinner)
        {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item = parent?.getItemAtPosition(position) as TrainingPlan?
                    if (item != null) {
                        saveSpinnerSelection(item.toString())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun saveSpinnerSelection(selectedItem: String) {
        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(SPINNER_PREF_KEY, selectedItem)
        editor.apply()
    }

    private fun loadSpinnerSelection(): String {
        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(SPINNER_PREF_KEY, "") ?: ""
    }

    private fun openTrainingPlansFragment() {
        val fragmentTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.fragmentContainer,
            TrainingPlansFragment(),
            "TrainingPlansFragment"
        )
        fragmentTransaction.commit()
    }

    private fun openRoutinesActivity(planName: String) {
        val explicitIntent = Intent(context, TrainingPlanActivity::class.java)
        explicitIntent.putExtra(TrainingPlansFragment.NEXT_SCREEN, planName)
        startActivity(explicitIntent)
    }

    private fun setLastTraining() {
        val historyDataBase = WorkoutHistoryDatabaseHelper(requireContext(), null)
        val customDate = CustomDate()
        if (!historyDataBase.isTableNotEmpty()) {
            binding.linearLayoutLastWorkout.visibility = View.GONE
        } else {
            binding.linearLayoutLastWorkout.visibility = View.VISIBLE
            val rawDate = historyDataBase.getLastWorkout()[1]
            if(rawDate != null)
            {
                val date = customDate.getFormattedDate(rawDate)
                binding.textViewLastTrainingPlanName.text = historyDataBase.getLastWorkout()[0]
                binding.textViewLastTrainingDate.text = date
                binding.textViewLastTrainingRoutineName.text = historyDataBase.getLastWorkout()[2]
            }
        }
    }

}