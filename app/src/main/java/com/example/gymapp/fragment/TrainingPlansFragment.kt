package com.example.gymapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
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
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class TrainingPlansFragment : Fragment() {
    private var _binding: FragmentTrainingPlansBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlansRecyclerViewAdapter: TrainingPlansRecyclerViewAdapter

    private lateinit var slideUpAnimation: Animation
    private lateinit var slideDownAnimation: Animation
    private val animations: Animations = Animations()

    private lateinit var plansDataBase: PlansDataBaseHelper
    private var trainingPlans: MutableList<TrainingPlan> = ArrayList()
    private val defaultElement = "Create training plan"

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return trainingPlansRecyclerViewAdapter.isLongClickActivated
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            if (direction == ItemTouchHelper.RIGHT) {
                showSingleDeleteDialog(position)
            }
        }

        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (trainingPlansRecyclerViewAdapter.isLongClickActivated) {
                return 0
            }
            return super.getSwipeDirs(recyclerView, viewHolder)
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
                        requireContext(),
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
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
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
        trainingPlans = plansDataBase.convertList(trainingPlanNamesString) { TrainingPlan(it) }
        if (!plansDataBase.isTableNotEmpty()) {
            trainingPlans.add((TrainingPlan(defaultElement)))
        }

        trainingPlansRecyclerViewAdapter = TrainingPlansRecyclerViewAdapter(
            trainingPlans,
            binding.toolbarDeletePlan.buttonDeleteElements
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
                if (trainingPlans[0].name == defaultElement) {
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

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
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
                    for (item in trainingPlans) {
                        if (editText.text.toString() == item.toString()) {
                            throw ValidationException("There is already a plan with this name")
                        }
                    }
                    if (editText.text.toString() == defaultElement) {
                        throw ValidationException("Invalid plan name")
                    }
                    if (trainingPlans[0].name == defaultElement) {
                        trainingPlans.clear()
                    }
                    plansDataBase.addPLan(editText.text.toString())
                    trainingPlans.add(TrainingPlan(editText.text.toString()))
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

    private fun showSingleDeleteDialog(position: Int) {
        val builder = context?.let { AlertDialog.Builder(it) }
        with(builder) {
            this?.setTitle("Are you sure you want to delete ${trainingPlans[position].name}?")
            this?.setPositiveButton("Yes") { _, _ ->
                trainingPlansRecyclerViewAdapter.deleteSinglePlan(position)
                plansDataBase.deletePlans(trainingPlansRecyclerViewAdapter.getDeletedPlans())
            }
            this?.setNegativeButton("Cancel") { _, _ ->
                trainingPlansRecyclerViewAdapter.notifyItemChanged(position)
            }
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
                    recyclerView,
                    300
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
                animations.translateY(recyclerView.translationY, 0f, recyclerView, 300)
            }

            override fun onAnimationEnd(animation: Animation?) {
                toolbar.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        toolbar.startAnimation(slideDownAnimation)
    }
}