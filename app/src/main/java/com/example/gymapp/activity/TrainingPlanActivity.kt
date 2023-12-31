package com.example.gymapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.TrainingPlanRecyclerViewAdapter
import com.example.gymapp.databinding.ActivityTrainingPlanBinding
import com.example.gymapp.fragment.TrainingPlansFragment
import com.example.gymapp.model.trainingPlans.TrainingPlanElement
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper

class TrainingPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingPlanBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlanRecyclerViewAdapter: TrainingPlanRecyclerViewAdapter

    private var planName: String? = null
    private var routines: MutableList<TrainingPlanElement> = ArrayList()
    private val routinesDataBase = RoutinesDataBaseHelper(this, null)
    private val defaultElement = "Create Routine"

    companion object {
        const val PLAN_NAME = "com.example.gymapp.planname"
        const val ROUTINE_NAME = "com.example.gymapp.routinename"
    }

    @SuppressLint("NotifyDataSetChanged")
    private val startCreateRoutineActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                routines.clear()
                setRecyclerViewContent()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TrainingPlansFragment.NEXT_SCREEN)) {
            binding.textViewTrainingPlanName.text =
                intent.getStringExtra(TrainingPlansFragment.NEXT_SCREEN)

        }
        planName = binding.textViewTrainingPlanName.text.toString()


        recyclerView = binding.RecyclerViewTrainingPlan
        trainingPlanRecyclerViewAdapter = TrainingPlanRecyclerViewAdapter(routines)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = trainingPlanRecyclerViewAdapter

        setRecyclerViewContent()

        binding.buttonAddRoutine.setOnClickListener()
        {
            val explicitIntent = Intent(applicationContext, CreateRoutineActivity::class.java)
            explicitIntent.putExtra(PLAN_NAME, planName)
            startCreateRoutineActivityForResult.launch(explicitIntent)
        }

        trainingPlanRecyclerViewAdapter.setOnClickListener(object :
            TrainingPlanRecyclerViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: TrainingPlanElement) {
                val explicitIntent = Intent(applicationContext, CreateRoutineActivity::class.java)
                if (routines[0].routineName == defaultElement) {
                    explicitIntent.putExtra(PLAN_NAME, planName)
                    startCreateRoutineActivityForResult.launch(explicitIntent)
                } else {
                    explicitIntent.putExtra(ROUTINE_NAME, model.routineName)
                    explicitIntent.putExtra(PLAN_NAME, planName)
                    startCreateRoutineActivityForResult.launch(explicitIntent)
                }
            }
        })

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setRecyclerViewContent() {
        val plansDataBase = PlansDataBaseHelper(this, null)
        val planName = this.planName
        if (planName != null) {
            val planId = plansDataBase.getPlanId(planName)
            if (planId != null) {
                if (!routinesDataBase.isPlanNotEmpty(planId.toString())) {
                    routines.add(TrainingPlanElement(defaultElement))
                }
                val routinesInPlan = routinesDataBase.getRoutinesInPlan(planId)
                for (routine in routinesInPlan) {
                    routines.add(routine)
                }
                trainingPlanRecyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }
}