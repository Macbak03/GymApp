package com.example.gymapp.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.persistence.ExercisesDataBaseHelper
import com.example.gymapp.adapter.RoutineRecyclerViewAdapter
import com.example.gymapp.databinding.ActivityCreateRoutineBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.model.routine.Exercise
import com.example.gymapp.model.routine.ExerciseDraft
import com.example.gymapp.model.routine.TimeUnit
import com.example.gymapp.model.routine.WeightUnit
import com.example.gymapp.persistence.PlansDataBaseHelper
import java.util.Collections


class CreateRoutineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRoutineBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var routineRecyclerViewAdapter: RoutineRecyclerViewAdapter

    private val exercisesDataBase = ExercisesDataBaseHelper(this, null)
    private val plansDataBase = PlansDataBaseHelper(this, null)
    private var exercises: MutableList<ExerciseDraft> = ArrayList()
    private var exerciseCount: Int = 0

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0 ) {
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

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
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



        recyclerView = binding.recyclerViewRoutineItems
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        routineRecyclerViewAdapter = RoutineRecyclerViewAdapter(exercises, itemTouchHelper)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = routineRecyclerViewAdapter

        binding.buttonAddExercise.setOnClickListener {
            addExercise()
        }
        binding.buttonDeleteExercise.setOnClickListener {
            removeExercise()
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
            "exercise$exerciseCount",
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

    private fun removeExercise() {
        val adapter = routineRecyclerViewAdapter
        if (exercises.isNotEmpty()) {
            exercises.removeAt(exercises.lastIndex)
            exerciseCount--
            adapter.notifyItemRemoved(adapter.itemCount)
        }
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
                val exercise = it.exerciseDetails.getExerciseDraft()?.toExercise()
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