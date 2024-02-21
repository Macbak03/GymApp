package com.example.gymapp.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.gymapp.R
import com.example.gymapp.activity.EditHistoryActivity
import com.example.gymapp.activity.HistoryDetailsActivity
import com.example.gymapp.adapter.TrainingPlansRecyclerViewAdapter
import com.example.gymapp.adapter.WorkoutHistoryRecyclerViewAdapter
import com.example.gymapp.databinding.FragmentMoreBinding
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TrainingHistoryMoreFragment(private val workoutHistoryRecyclerViewAdapter: WorkoutHistoryRecyclerViewAdapter) :
    BottomSheetDialogFragment() {
    private var _binding: FragmentMoreBinding? = null

    private val binding get() = _binding!!
    private lateinit var workoutHistoryDatabase: WorkoutHistoryDatabaseHelper


    companion object {
        const val PLAN_NAME = "com.example.gymapp.planname"
        const val ROUTINE_NAME = "com.example.gymapp.routinename"
        const val FORMATTED_DATE = "com.example.gymapp.formatteddate"
        const val RAW_DATE = "com.example.gymapp.rawdate"
    }

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

        workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(requireContext(), null)

        val planName = arguments?.getString(WorkoutHistoryRecyclerViewAdapter.SELECTED_ITEM_PLAN_NAME)
        val routineName = arguments?.getString(WorkoutHistoryRecyclerViewAdapter.SELECTED_ITEM_ROUTINE_NAME)
        val formattedDate = arguments?.getString(WorkoutHistoryRecyclerViewAdapter.SELECTED_ITEM_FORMATTED_DATE)
        val rawDate = arguments?.getString(WorkoutHistoryRecyclerViewAdapter.SELECTED_ITEM_RAW_DATE)
        val position = arguments?.getInt(TrainingPlansRecyclerViewAdapter.POSITION)


        binding.moreTextViewName.text = routineName
        binding.moreTextViewDate.text = formattedDate

        binding.moreButtonEdit.setOnClickListener {
            val explicitIntent = Intent(context, EditHistoryActivity::class.java)
            explicitIntent.putExtra(PLAN_NAME, planName)
            explicitIntent.putExtra(ROUTINE_NAME, routineName)
            explicitIntent.putExtra(FORMATTED_DATE, formattedDate)
            explicitIntent.putExtra(RAW_DATE, rawDate)
            startActivity(explicitIntent)
            endFragment()
        }

        binding.moreButtonDelete.setOnClickListener {
            showDeleteDialog(routineName, formattedDate, position)
        }

        binding.moreButtonCancel.setOnClickListener {
            endFragment()
        }
    }

    private fun showDeleteDialog(name: String?, date: String?, position: Int?) {
        if (position != null) {
            val builder = context?.let { AlertDialog.Builder(it) }
            with(builder) {
                this?.setTitle("Are you sure you want to delete $name from $date?")
                this?.setPositiveButton("Yes") { _, _ ->
                    workoutHistoryRecyclerViewAdapter.deleteSingleHistory(position)
                    workoutHistoryDatabase.deleteFromHistory(workoutHistoryRecyclerViewAdapter.getRemovedHistoryDate())
                    endFragment()
                }
                this?.setNegativeButton("Cancel") { _, _ ->
                }
                this?.show()
            }
        }
    }

    private fun endFragment() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }
}