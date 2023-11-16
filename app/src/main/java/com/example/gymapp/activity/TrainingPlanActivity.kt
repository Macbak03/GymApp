package com.example.gymapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.TrainingPlanRecyclerViewAdapter
import com.example.gymapp.databinding.ActivityTrainingPlanBinding
import com.example.gymapp.model.trainingPlans.TrainingPlanElement
import com.example.gymapp.persistence.PlanDataBaseHelper
import com.example.gymapp.persistence.RoutineDataBaseHelper

class TrainingPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingPlanBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlanRecyclerViewAdapter: TrainingPlanRecyclerViewAdapter
    private lateinit var planName: String

    private var routines: MutableList<TrainingPlanElement> = ArrayList()
    private val routineDataBase = RoutineDataBaseHelper(this, null)

    companion object {
        const val NEXT_SCREEN = "createRoutineScreen"
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
        trainingPlanRecyclerViewAdapter = TrainingPlanRecyclerViewAdapter(routines)
        recyclerView = binding.RecyclerViewTrainingPlan
        setRecyclerViewContent()
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = trainingPlanRecyclerViewAdapter

        binding.buttonAddRoutine.setOnClickListener()
        {
            val explicitIntent = Intent(applicationContext, CreateRoutineActivity::class.java)
            explicitIntent.putExtra(NEXT_SCREEN, planName)
            startCreateRoutineActivityForResult.launch(explicitIntent)
        }

    }


    private fun setRecyclerViewContent() {
        val planDataBase = PlanDataBaseHelper(this, null)
        val id = planDataBase.getValue(
            PlanDataBaseHelper.TABLE_NAME,
            PlanDataBaseHelper.PLAN_ID_COLUMN,
            PlanDataBaseHelper.PLAN_NAME_COLUMN,
            planName

        )?.toInt()
        if (id != null && routineDataBase.doesIdExist(id)) {
            val cursor = routineDataBase.getFromTable(
                RoutineDataBaseHelper.TABLE_NAME, RoutineDataBaseHelper.ROUTINE_NAME_COLUMN,
                RoutineDataBaseHelper.PLAN_ID_COLUMN,
                id.toString()
            )
            cursor.moveToFirst()
            routines.add(TrainingPlanElement(cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.ROUTINE_NAME_COLUMN))))
            while (cursor.moveToNext())
            {
                routines.add(TrainingPlanElement(cursor.getString(cursor.getColumnIndexOrThrow(RoutineDataBaseHelper.ROUTINE_NAME_COLUMN))))
            }
            trainingPlanRecyclerViewAdapter.notifyItemInserted(trainingPlanRecyclerViewAdapter.itemCount)
        }


    }
}