package com.example.gymapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.activity.TrainingPlanActivity
import com.example.gymapp.adapter.TrainingPlansRecyclerViewAdapter
import com.example.gymapp.animation.Animations
import com.example.gymapp.databinding.FragmentTrainingPlansBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.model.trainingPlans.TrainingPlan
import com.example.gymapp.persistence.PlansDataBaseHelper

class TrainingPlansFragment : Fragment() {
    private var _binding: FragmentTrainingPlansBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlansRecyclerViewAdapter: TrainingPlansRecyclerViewAdapter

    private lateinit var slideUpAnimation: Animation
    private lateinit var slideDownAnimation: Animation
    private val animations: Animations = Animations()

    private lateinit var plansDataBase: PlansDataBaseHelper
    private var trainingPlansNames: MutableList<TrainingPlan> = ArrayList()
    private val defaultElement = "Create training plan"

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (trainingPlansRecyclerViewAdapter.isLongClickActivated) {
                trainingPlansRecyclerViewAdapter.resetLongClickState()
                hideToolbar()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    companion object {
        const val NEXT_SCREEN = "trainingPlanScreen"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingPlansBinding.inflate(layoutInflater, container, false)
        slideUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.toolbar_slide_up)
        slideDownAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.toolbar_slide_down)
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
            trainingPlansNames.add((TrainingPlan(defaultElement)))
        }

        trainingPlansRecyclerViewAdapter = TrainingPlansRecyclerViewAdapter(
            trainingPlansNames,
            binding.toolbarDeletePlan.buttonDeleteElements,
            context,
            plansDataBase
        )
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = trainingPlansRecyclerViewAdapter

        binding.buttonCreateTrainingPlan.setOnClickListener()
        {
            showEditTextDialog()
        }

        trainingPlansRecyclerViewAdapter.setOnClickListener(object :
            TrainingPlansRecyclerViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: TrainingPlan) {
                if (trainingPlansNames[0].name == defaultElement) {
                    showEditTextDialog()
                } else {
                    val explicitIntent = Intent(context, TrainingPlanActivity::class.java)
                    explicitIntent.putExtra(NEXT_SCREEN, model.name)
                    startActivity(explicitIntent)
                }
            }
        })

        trainingPlansRecyclerViewAdapter.setOnLongClickListener(object :
            TrainingPlansRecyclerViewAdapter.OnLongClickListener {
            override fun onLongClick(position: Int, model: TrainingPlan) {
                showToolbar()
            }
        })

        binding.toolbarDeletePlan.buttonBackFromDeleteMode.setOnClickListener {
            trainingPlansRecyclerViewAdapter.resetLongClickState()
            hideToolbar()
        }

        binding.toolbarDeletePlan.buttonDeleteElements.setOnClickListener {
            showDeleteDialog()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("InflateParams", "NotifyDataSetChanged")
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
                    if (editText.text.toString() == defaultElement) {
                        throw ValidationException("Invalid plan name")
                    }
                    if (trainingPlansNames[0].name == defaultElement) {
                        trainingPlansNames.clear()
                    }
                    plansDataBase.addPLan(editText.text.toString())
                    trainingPlansNames.add(TrainingPlan(editText.text.toString()))
                    trainingPlansRecyclerViewAdapter.notifyDataSetChanged()
                } catch (exception: ValidationException) {
                    Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                }
            }
            this?.setNegativeButton("Cancel") { _, _ -> }
            this?.setView(dialogLayout)
            this?.show()
        }
    }

    private fun showDeleteDialog() {
        val builder = context?.let { AlertDialog.Builder(it) }
        with(builder) {
            this?.setTitle("Are you sure you want to delete?")
            this?.setPositiveButton("Yes") { _, _ ->
                trainingPlansRecyclerViewAdapter.deletePlansFromRecyclerView()
                val deletedPlans = trainingPlansRecyclerViewAdapter.getDeletedPlans()
                plansDataBase.deletePlans(deletedPlans)
            }
            this?.setNegativeButton("Cancel") { _, _ -> }
            this?.show()
        }
    }




    private fun showToolbar() {
        val toolbar = binding.toolbarDeletePlan.toolbar

        slideUpAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(anim: Animation?) {
                animations.translateY(
                    recyclerView.translationY,
                    toolbar.translationY + toolbar.height.toFloat(),
                    recyclerView
                )
            }

            override fun onAnimationEnd(anim: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        toolbar.visibility = View.VISIBLE
        toolbar.startAnimation(slideUpAnimation)

    }

    private fun hideToolbar() {
        val toolbar = binding.toolbarDeletePlan.toolbar

        slideDownAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(anim: Animation?) {
                animations.translateY(recyclerView.translationY, 0f, recyclerView)
            }

            override fun onAnimationEnd(animation: Animation?) {
                toolbar.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        toolbar.startAnimation(slideDownAnimation)
    }
}