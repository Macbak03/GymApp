package com.example.gymapp.activity

import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.persistence.ExercisesDataBaseHelper
import com.example.gymapp.adapter.RoutineRecyclerViewAdapter
import com.example.gymapp.databinding.ActivityCreateRoutineBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.model.routine.Exercise
import com.example.gymapp.model.routine.ExerciseDraft
import com.example.gymapp.model.routine.TimeUnit
import com.example.gymapp.model.routine.WeightUnit
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.viewModel.RoutineRecyclerViewViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.Collections


class CreateRoutineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRoutineBinding

    private lateinit var viewModel: RoutineRecyclerViewViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var routineRecyclerViewAdapter: RoutineRecyclerViewAdapter

    private val exercisesDataBase = ExercisesDataBaseHelper(this, null)
    private val plansDataBase = PlansDataBaseHelper(this, null)
    private var exercises: MutableList<ExerciseDraft> = ArrayList()
    private var exerciseCount: Int = 0

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT ) {
        override fun onMove(
            recyclerView: RecyclerView,
            source: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val sourcePosition = source.absoluteAdapterPosition
            val targetPosition = target.absoluteAdapterPosition

            Collections.swap(exercises, sourcePosition, targetPosition)

            routineRecyclerViewAdapter.notifyItemMoved(sourcePosition, targetPosition)

            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        override fun getDragDirs(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val routineViewHolder = recyclerView.findViewHolderForAdapterPosition(viewHolder.absoluteAdapterPosition) as RoutineRecyclerViewAdapter.RoutineViewHolder?
            if (routineViewHolder != null) {
                routineRecyclerViewAdapter.hideDetails(routineViewHolder)
            }
            return super.getDragDirs(recyclerView, viewHolder)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            if (direction == ItemTouchHelper.RIGHT) {
                exercises.removeAt(position)
                exerciseCount--
                routineRecyclerViewAdapter.notifyItemRemoved(position)
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
                        this@CreateRoutineActivity,
                        R.color.red
                    )
                )
                .addActionIcon(R.drawable.baseline_delete_24)
                .setActionIconTint(0xFFFFFFFF.toInt())
                .addPadding(TypedValue.COMPLEX_UNIT_DIP, 6f, 14f, 6f)
                .addSwipeRightLabel("Delete")
                .setSwipeRightLabelColor(0xFFFFFFFF.toInt())
                .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

            goBackToTrainingPlanActivity()

            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var planName: String? = null
        if (intent.hasExtra(TrainingPlanActivity.PLAN_NAME)) {
            planName = intent.getStringExtra(TrainingPlanActivity.PLAN_NAME)
        }

        if(planName != null)
        {
            val planId = plansDataBase.getPlanId(planName)
            if (intent.hasExtra(TrainingPlanActivity.ROUTINE_NAME)) {
                loadRoutine(planId)
            }
        }

        viewModel = ViewModelProvider(this)[RoutineRecyclerViewViewModel::class.java]


        recyclerView = binding.recyclerViewRoutineItems
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        routineRecyclerViewAdapter = RoutineRecyclerViewAdapter(exercises, itemTouchHelper, this, layoutInflater, viewModel)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = routineRecyclerViewAdapter

        binding.buttonAddExercise.setOnClickListener {
            addExercise()
        }
        binding.buttonSaveRoutine.setOnClickListener()
        {
            if (planName != null) {
                try {
                    saveRoutineIntoDB(planName)
                } catch (exception: ValidationException) {
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)){ view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottom)
            insets
        }
    }

    private fun loadRoutine(planId: Int?) {
        val routineName = intent.getStringExtra(TrainingPlanActivity.ROUTINE_NAME)
        if (routineName != null && planId != null) {
            binding.editTextRoutineName.setText(routineName)
            exercises = exercisesDataBase.getRoutine(routineName, planId.toString())
            exerciseCount = exercises.size
        }

    }

    private fun addExercise() {
        val adapter = routineRecyclerViewAdapter
        ++exerciseCount
        val exercise = ExerciseDraft(
            exerciseCount.toLong(),
            "",
            "",
            TimeUnit.min,
            "",
            WeightUnit.kg,
            "",
            "",
            "",
            "",
            true
        )
        exercises.add(exercise)
        adapter.notifyItemInserted(adapter.itemCount + 1)
    }

    private fun goBackToTrainingPlanActivity() {
        setResult(RESULT_OK)
        finish()
    }

    private fun getRoutine(): ArrayList<Exercise>{
        val adapter = routineRecyclerViewAdapter
        val routine = ArrayList<Exercise>()
        for(i in 0 until adapter.itemCount)
        {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? RoutineRecyclerViewAdapter.RoutineViewHolder
            viewHolder?.let {
                val exerciseDraft = it.exerciseDetails.getExerciseDraft()
                val exerciseName =it.exerciseName.text.toString()
                exerciseDraft?.name = exerciseName
                val exercise = exerciseDraft?.toExercise()
                if (exercise != null) {
                    routine.add(exercise)
                }
            }
        }
        return routine
    }

    private fun saveRoutineIntoDB(planName: String) {
        val adapter = routineRecyclerViewAdapter
        if (adapter.itemCount == 0) {
            throw ValidationException("You must add at least one exercise to the routine")
        }
        val routineName = binding.editTextRoutineName.text.toString()
        if (routineName.isBlank()) {
            throw ValidationException("routine name cannot be empty")
        }
        val planId = plansDataBase.getPlanId(planName)
        if (planId != null) {
            try {
                val routine = getRoutine()
                val originalRoutineName = intent.getStringExtra(TrainingPlanActivity.ROUTINE_NAME)
                exercisesDataBase.addRoutine(routine, routineName, planId, originalRoutineName)
                Toast.makeText(this, "Routine $routineName saved", Toast.LENGTH_LONG).show()
                goBackToTrainingPlanActivity()
            } catch (exception: ValidationException) {
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}