package com.example.gymapp.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.gymapp.R
import com.example.gymapp.databinding.FragmentHomeBinding
import com.example.gymapp.model.trainingPlans.TrainingPlan
import com.example.gymapp.persistence.PlansDataBaseHelper


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataBase: PlansDataBaseHelper
    private var trainingPlansNames: MutableList<TrainingPlan> = ArrayList()
    private lateinit var spinner: Spinner
    private val SPINNER_PREF_KEY = "selectedSpinnerItem"
    private var selectedItem: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        spinner = binding.spinnerTrainingPlans
        dataBase = PlansDataBaseHelper(requireContext(), null)
        val trainingPlanNamesString = dataBase.getColumn(
            PlansDataBaseHelper.TABLE_NAME,
            PlansDataBaseHelper.PLAN_NAME_COLUMN,
            PlansDataBaseHelper.PLAN_ID_COLUMN
        )
        trainingPlansNames = dataBase.convertList(trainingPlanNamesString) { TrainingPlan(it) }
        if (!dataBase.isTableNotEmpty()) {
            val noneTrainingPlanFound = "Go to training plans section to create your first plan"
            binding.textViewCurrentTrainingPlan.text = noneTrainingPlanFound
            spinner.isEnabled = false
            spinner.visibility = View.GONE
        }
        initSpinner()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            //setSelection(0, false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val item = parent?.getItemAtPosition(position) as TrainingPlan?
                    if (item != null)
                    {
                        //this@HomeFragment.selectedItem = item.toString()
                        saveSpinnerSelection(item.toString())
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun saveSpinnerSelection(selectedItem: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(SPINNER_PREF_KEY, selectedItem)
        editor.apply()
    }

    private fun loadSpinnerSelection(): String {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(SPINNER_PREF_KEY, "") ?: ""
    }

}