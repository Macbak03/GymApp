package com.example.gymapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.activity.TrainingPlanActivity
import com.example.gymapp.adapter.TrainingPlansRecyclerViewAdapter
import com.example.gymapp.databinding.FragmentTrainingPlansBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.model.trainingPlans.TrainingPlan
import com.example.gymapp.persistence.PlansDataBaseHelper

class TrainingPlansFragment : Fragment() {
    private var _binding: FragmentTrainingPlansBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlansRecyclerViewAdapter: TrainingPlansRecyclerViewAdapter

    private lateinit var plansDataBase: PlansDataBaseHelper
    private var trainingPlansNames: MutableList<TrainingPlan> = ArrayList()

    companion object {
        const val NEXT_SCREEN = "trainingPlanScreen"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingPlansBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plansDataBase = PlansDataBaseHelper(requireContext(), null)
        recyclerView = binding.recyclerViewTrainingPlans
        val trainingPlanNamesString = plansDataBase.getColumn(
            PlansDataBaseHelper.TABLE_NAME,
            PlansDataBaseHelper.PLAN_NAME_COLUMN,
            PlansDataBaseHelper.PLAN_ID_COLUMN
        )
        trainingPlansNames = plansDataBase.convertList(trainingPlanNamesString) { TrainingPlan(it) }
        if (!plansDataBase.isTableNotEmpty()) {
            trainingPlansNames.add((TrainingPlan("You don't have any training plans yet.")))
        }

        trainingPlansRecyclerViewAdapter = TrainingPlansRecyclerViewAdapter(trainingPlansNames)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = trainingPlansRecyclerViewAdapter

        binding.buttonCreateTrainingPlan.setOnClickListener()
        {
            showEditTextDialog()
        }

        trainingPlansRecyclerViewAdapter.setOnClickListener(object :
            TrainingPlansRecyclerViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: TrainingPlan) {
                val explicitIntent = Intent(context, TrainingPlanActivity::class.java)
                explicitIntent.putExtra(NEXT_SCREEN, model.name)
                startActivity(explicitIntent)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("InflateParams")
    private fun showEditTextDialog() {
        val builder = context?.let { AlertDialog.Builder(it) }
        val dialogLayout = layoutInflater.inflate(R.layout.enter_name_edit_text, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editTextName)
        editText.hint = "Plan name"

        with(builder) {
            this?.setTitle("Enter training plan name")
            this?.setPositiveButton("Add") { _, _ ->
                try {
                    if (editText.text.isBlank()) {
                        throw ValidationException("Training plan name cannot be empty")
                    }
                    for (item in trainingPlansNames) {
                        if (editText.text.toString() == item.toString()) {
                            throw ValidationException("There is already a plan with this name")
                        }
                    }
                    plansDataBase.addPLan(editText.text.toString())
                    trainingPlansNames.add(TrainingPlan(editText.text.toString()))
                    trainingPlansRecyclerViewAdapter.notifyItemInserted(
                        trainingPlansRecyclerViewAdapter.itemCount
                    )
                } catch (exception: ValidationException) {
                    Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                }
            }
            this?.setNegativeButton("Cancel") { _, _ -> }
            this?.setView(dialogLayout)
            this?.show()
        }
    }
}