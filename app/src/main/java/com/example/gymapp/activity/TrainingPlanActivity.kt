package com.example.gymapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.adapter.TrainingPlanRecyclerViewAdapter
import com.example.gymapp.databinding.ActivityTrainingPlanBinding
import com.example.gymapp.fragment.TrainingPlansFragment
import com.example.gymapp.model.trainingPlans.TrainingPlanElement
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class TrainingPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingPlanBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlanRecyclerViewAdapter: TrainingPlanRecyclerViewAdapter

    private var planName: String? = null
    private var routines: MutableList<TrainingPlanElement> = ArrayList()
    private val routinesDataBase = RoutinesDataBaseHelper(this, null)
    private val plansDataBase = PlansDataBaseHelper(this, null)
    private val defaultElement = "Create Routine"

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            if (direction == ItemTouchHelper.RIGHT) {
                showDeleteDialog(position)
            }
        }

        /*Copyright {2024} {Maciej Bąk}

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addBackgroundColor(
                    ContextCompat.getColor(
                        this@TrainingPlanActivity,
                        R.color.red
                    )
                )
                .addActionIcon(R.drawable.baseline_delete_24)
                .setActionIconTint(0xFFFFFFFF.toInt())
                .addCornerRadius(TypedValue.COMPLEX_UNIT_DIP, 3)
                .addPadding(TypedValue.COMPLEX_UNIT_DIP, 6f, 14f, 6f)
                .addSwipeRightLabel("Delete")
                .setSwipeRightLabelColor(0xFFFFFFFF.toInt())
                .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }


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
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setRecyclerViewContent() {
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


    private fun showDeleteDialog(position: Int) {
        val builder = this.let { AlertDialog.Builder(it) }
        with(builder) {
            this.setTitle("Are you sure you want to delete ${routines[position].routineName}?")
            this.setPositiveButton("Yes") { _, _ ->
                val planId = plansDataBase.getPlanId(planName)
                if(planId != null)
                {
                    trainingPlanRecyclerViewAdapter.deleteSingleRoutine(position)
                    routinesDataBase.deleteRoutines(planId, trainingPlanRecyclerViewAdapter.getDeletedRoutines())
                }
            }
            this.setNegativeButton("Cancel") { _, _ ->
                trainingPlanRecyclerViewAdapter.notifyItemChanged(position)
            }
            this.show()
        }
    }

}