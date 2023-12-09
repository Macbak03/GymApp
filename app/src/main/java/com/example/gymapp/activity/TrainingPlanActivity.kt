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
import com.example.gymapp.model.trainingPlans.TrainingPlanElement
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper

class TrainingPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingPlanBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlanRecyclerViewAdapter: TrainingPlanRecyclerViewAdapter
    private lateinit var planName: String

    private var routines: MutableList<TrainingPlanElement> = ArrayList()
    private val routinesDataBase = RoutinesDataBaseHelper(this, null)

    companion object {
        const val PLAN_NAME = "com.example.gymapp.planname"
        const val ROUTINE_NAME = "com.example.gymapp.routinename"
    }

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

        if (intent.hasExtra(TrainingPlansActivity.NEXT_SCREEN)) {
            binding.textViewTrainingPlanName.text =
                intent.getStringExtra(TrainingPlansActivity.NEXT_SCREEN)

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
                explicitIntent.putExtra(ROUTINE_NAME, model.routineName)
                explicitIntent.putExtra(PLAN_NAME, planName)
                startCreateRoutineActivityForResult.launch(explicitIntent)
            }
        })

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setRecyclerViewContent() {
        val plansDataBase = PlansDataBaseHelper(this, null)
        val planId = plansDataBase.getValue(
            PlansDataBaseHelper.TABLE_NAME,
            PlansDataBaseHelper.PLAN_ID_COLUMN,
            PlansDataBaseHelper.PLAN_NAME_COLUMN,
            planName
        )?.toInt()
        if (planId != null) {
            if(!routinesDataBase.isPlanEmpty(planId.toString()))
            {
                routines.add(TrainingPlanElement("You don't have any routines yet"))
            }
            val cursor = routinesDataBase.getRoutinesInPlan(planId)
            if (cursor.moveToFirst()) {
                routines.add(
                    TrainingPlanElement(cursor.getString(cursor.getColumnIndexOrThrow(RoutinesDataBaseHelper.ROUTINE_NAME_COLUMN))))
                while (cursor.moveToNext()) {
                    routines.add(TrainingPlanElement(cursor.getString(cursor.getColumnIndexOrThrow(RoutinesDataBaseHelper.ROUTINE_NAME_COLUMN))))

                }
            }
            trainingPlanRecyclerViewAdapter.notifyDataSetChanged()
        }
    }
}