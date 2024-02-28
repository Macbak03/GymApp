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
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.activity.TrainingPlanActivity
import com.example.gymapp.adapter.TrainingPlansRecyclerViewAdapter
import com.example.gymapp.animation.FragmentAnimator
import com.example.gymapp.databinding.FragmentTrainingPlansBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.model.trainingPlans.TrainingPlan
import com.example.gymapp.persistence.PlansDataBaseHelper
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class TrainingPlansFragment : Fragment(), FragmentAnimator {
    private var _binding: FragmentTrainingPlansBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlansRecyclerViewAdapter: TrainingPlansRecyclerViewAdapter

    private lateinit var plansDataBase: PlansDataBaseHelper
    private var trainingPlans: MutableList<TrainingPlan> = ArrayList()


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

    companion object {
        const val NEXT_SCREEN = "trainingPlanScreen"
        const val DEFAULT_ELEMENT = "Create training plan"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingPlansBinding.inflate(layoutInflater, container, false)
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
            trainingPlans.add((TrainingPlan(DEFAULT_ELEMENT)))
        }

        trainingPlansRecyclerViewAdapter = TrainingPlansRecyclerViewAdapter(
            trainingPlans,
            this
        )
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = trainingPlansRecyclerViewAdapter

        val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.add_button_animation)

        binding.buttonCreateTrainingPlan.setOnClickListener()
        {
            it.startAnimation(scaleAnimation)
            showCreatePlanDialog()
        }

        trainingPlansRecyclerViewAdapter.setOnClickListener(object :
            TrainingPlansRecyclerViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: TrainingPlan) {
                if (trainingPlans[0].name == DEFAULT_ELEMENT) {
                    showCreatePlanDialog()
                } else {
                    val explicitIntent = Intent(context, TrainingPlanActivity::class.java)
                    explicitIntent.putExtra(NEXT_SCREEN, model.name)
                    startActivity(explicitIntent)
                }
            }
        })

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("InflateParams", "NotifyDataSetChanged")
    private fun showCreatePlanDialog() {
        val builder = context?.let { AlertDialog.Builder(it, R.style.YourAlertDialogTheme) }
        val dialogLayout = layoutInflater.inflate(R.layout.edit_text_dialog_layout, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editTextName)
        editText.hint = "Plan name"

        with(builder) {
            this?.setTitle("Enter training plan name")
            this?.setPositiveButton("Add") { _, _ ->
                try {
                    if (editText.text.isBlank()) {
                        throw ValidationException("Training plan name cannot be empty")
                    }
                    if (plansDataBase.doesPlanNameExist(editText.text.toString())) {
                        throw ValidationException("There is already a plan with this name")
                    }
                    if (editText.text.toString() == DEFAULT_ELEMENT) {
                        throw ValidationException("Invalid plan name")
                    }
                    if (trainingPlans.isNotEmpty()) {
                        if (trainingPlans[0].name == DEFAULT_ELEMENT) {
                            trainingPlans.clear()
                        }
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

    @SuppressLint("InflateParams")
    private fun showDeleteDialog(position: Int) {
        val builder = context?.let { AlertDialog.Builder(it, R.style.YourAlertDialogTheme) }
        val dialogLayout = layoutInflater.inflate(R.layout.text_view_dialog_layout, null)
        val textView = dialogLayout.findViewById<TextView>(R.id.textViewDialog)
        val message = "${trainingPlans[position].name}?"
        textView.text = message
        with(builder) {
            this?.setTitle("Are you sure you want to delete")
            this?.setPositiveButton("Yes") { _, _ ->
                trainingPlansRecyclerViewAdapter.deleteSinglePlan(position)
                plansDataBase.deletePlans(trainingPlansRecyclerViewAdapter.getDeletedPlans())
            }
            this?.setNegativeButton("Cancel") { _, _ ->
                trainingPlansRecyclerViewAdapter.notifyItemChanged(position)
            }
            this?.setView(dialogLayout)
            this?.show()
        }
    }

    override fun triggerAnimation() {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
        requireView().startAnimation(slideIn)
    }
}