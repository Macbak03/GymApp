package com.example.gymapp.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.gymapp.R
import com.example.gymapp.adapter.TrainingPlansRecyclerViewAdapter
import com.example.gymapp.databinding.FragmentMoreBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TrainingPlansMoreFragment(private val trainingPlansRecyclerViewAdapter: TrainingPlansRecyclerViewAdapter) :
    BottomSheetDialogFragment() {
    private var _binding: FragmentMoreBinding? = null

    private val binding get() = _binding!!
    private lateinit var plansDataBase: PlansDataBaseHelper
    private var name: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener { it ->
            val dialog = it as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behaviour = BottomSheetBehavior.from(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        plansDataBase = PlansDataBaseHelper(requireContext(), null)

        binding.moreTextViewDate.visibility = View.GONE

        name = arguments?.getString(TrainingPlansRecyclerViewAdapter.SELECTED_ITEM_KEY)
        val position = arguments?.getInt(TrainingPlansRecyclerViewAdapter.POSITION)
        val editButtonText = arguments?.getString(TrainingPlansRecyclerViewAdapter.BUTTON_TEXT)

        binding.moreTextViewName.text = name
        binding.moreButtonEdit.text = editButtonText

        binding.moreButtonEdit.setOnClickListener {
            showEditDialog(name, position)
        }

        binding.moreButtonDelete.setOnClickListener {
            showDeleteDialog(name, position)
        }

        binding.moreButtonCancel.setOnClickListener {
            endFragment()
        }
    }

    private fun showDeleteDialog(name: String?, position: Int?) {
        if (position != null) {
            val builder = context?.let { AlertDialog.Builder(it) }
            with(builder) {
                this?.setTitle("Are you sure you want to delete $name?")
                this?.setPositiveButton("Yes") { _, _ ->
                    trainingPlansRecyclerViewAdapter.deleteSinglePlan(position)
                    plansDataBase.deletePlans(trainingPlansRecyclerViewAdapter.getDeletedPlans())
                    endFragment()
                }
                this?.setNegativeButton("Cancel") { _, _ ->
                }
                this?.show()
            }
        }
    }

    @SuppressLint("InflateParams", "NotifyDataSetChanged")

    private fun showEditDialog(name: String?, position: Int?) {
        val planId = plansDataBase.getPlanId(name)

        val builder = context?.let { AlertDialog.Builder(it) }
        val dialogLayout = layoutInflater.inflate(R.layout.enter_name_edit_text, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editTextName)
        editText.hint = "Plan name"
        editText.setText(name)
        with(builder) {
            this?.setTitle("Enter training plan name")
            this?.setPositiveButton("OK") { _, _ ->
                try {
                    if (editText.text.isBlank()) {
                        throw ValidationException("Training plan name cannot be empty")
                    }
                    if (plansDataBase.doesPlanNameExist(editText.text.toString())) {
                        throw ValidationException("There is already a plan with this name")
                    }
                    if (planId != null && position != null) {
                        val newPlanName = editText.text.toString()
                        plansDataBase.updatePlanName(planId, newPlanName)
                        trainingPlansRecyclerViewAdapter.getElement(position).name = newPlanName
                        trainingPlansRecyclerViewAdapter.notifyItemChanged(position)
                        binding.moreTextViewName.text = newPlanName
                        showUpdatePlanNameInHistoryQuery(name, newPlanName)
                        this@TrainingPlansMoreFragment.name = newPlanName
                    }
                } catch (exception: ValidationException) {
                    Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                }
            }
            this?.setNegativeButton("Cancel") { _, _ -> }
            this?.setView(dialogLayout)
            this?.show()
        }
    }

    private fun showUpdatePlanNameInHistoryQuery(oldName: String?, newName: String?) {
            val builder = context?.let { AlertDialog.Builder(it) }
            with(builder) {
                this?.setTitle("Do you want to also change plan's name in history?")
                this?.setPositiveButton("Yes") { _, _ ->
                    val workoutHistoryDatabase =
                        WorkoutHistoryDatabaseHelper(requireContext(), null)
                    if (oldName != null && newName != null) {
                        workoutHistoryDatabase.updatePlanNames(oldName, newName)
                    }
                }
                this?.setNegativeButton("No") { _, _ ->
                }
                this?.show()
            }
    }

    private fun endFragment() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }
}