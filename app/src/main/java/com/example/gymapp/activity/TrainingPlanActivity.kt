package com.example.gymapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.adapter.TrainingPlanRecyclerViewAdapter
import com.example.gymapp.animation.Animations
import com.example.gymapp.databinding.ActivityTrainingPlanBinding
import com.example.gymapp.fragment.TrainingPlansFragment
import com.example.gymapp.model.trainingPlans.TrainingPlanElement
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper

class TrainingPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingPlanBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlanRecyclerViewAdapter: TrainingPlanRecyclerViewAdapter

    private lateinit var slideUpAnimation: Animation
    private lateinit var slideDownAnimation: Animation
    private val animations: Animations = Animations()

    private var planName: String? = null
    private var routines: MutableList<TrainingPlanElement> = ArrayList()
    private val routinesDataBase = RoutinesDataBaseHelper(this, null)
    private val defaultElement = "Create Routine"

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (trainingPlanRecyclerViewAdapter.isLongClickActivated) {
                trainingPlanRecyclerViewAdapter.resetLongClickState()
                hideToolbar()
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

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

        slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.toolbar_slide_up)
        slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.toolbar_slide_down)

        binding.toolbarDeleteRoutine.textViewToolbarText.setText(R.string.delete_routines_text)

        if (intent.hasExtra(TrainingPlansFragment.NEXT_SCREEN)) {
            binding.textViewTrainingPlanName.text =
                intent.getStringExtra(TrainingPlansFragment.NEXT_SCREEN)

        }
        planName = binding.textViewTrainingPlanName.text.toString()


        recyclerView = binding.RecyclerViewTrainingPlan
        trainingPlanRecyclerViewAdapter = TrainingPlanRecyclerViewAdapter(routines, binding.toolbarDeleteRoutine.buttonDeleteElements)
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

        trainingPlanRecyclerViewAdapter.setOnLongClickListener(object :
            TrainingPlanRecyclerViewAdapter.OnLongClickListener {
            override fun onLongClick(position: Int, model: TrainingPlanElement) {
                showToolbar()
            }
        })

        binding.toolbarDeleteRoutine.buttonBackFromDeleteMode.setOnClickListener {
            trainingPlanRecyclerViewAdapter.resetLongClickState()
            hideToolbar()
        }

        binding.toolbarDeleteRoutine.buttonDeleteElements.setOnClickListener {
            showDeleteDialog()
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

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

    private fun showDeleteDialog() {
        val builder = this.let { AlertDialog.Builder(it) }
        with(builder) {
            this.setTitle("Are you sure you want to delete?")
            this.setPositiveButton("OK") { _, _ ->
                val plansDataBase = PlansDataBaseHelper(context, null)
                val planName = this@TrainingPlanActivity.planName
                if(planName != null){
                    val planId = plansDataBase.getPlanId(planName)
                    if(planId != null){
                        trainingPlanRecyclerViewAdapter.deleteRoutinesFromRecyclerView()
                        val deletedRoutines = trainingPlanRecyclerViewAdapter.getDeletedRoutines()
                        routinesDataBase.deletePlans(planId, deletedRoutines)
                    }
                }
            }
            this.setNegativeButton("Cancel") { _, _ -> }
            this.show()
        }
    }

    private fun showToolbar() {
        val toolbar = binding.toolbarDeleteRoutine.toolbar

        slideUpAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(anim: Animation?) {
                hidePlanName()
            }

            override fun onAnimationEnd(anim: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        toolbar.visibility = View.VISIBLE
        toolbar.startAnimation(slideUpAnimation)

    }

    private fun hideToolbar() {
        val toolbar = binding.toolbarDeleteRoutine.toolbar

        slideDownAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(anim: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                toolbar.visibility = View.GONE
                showPlanName()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        toolbar.startAnimation(slideDownAnimation)
    }

    private fun showPlanName(){
        val planName = binding.textViewTrainingPlanName
        planName.visibility = View.VISIBLE
        planName.animate()
            .alpha(1f)
            .setDuration(200)
            .withEndAction(null)
            .start()
    }
    private fun hidePlanName(){
        val planName = binding.textViewTrainingPlanName
        planName.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                planName.visibility = View.INVISIBLE
                planName.clearAnimation()
            }
            .start()
    }
}