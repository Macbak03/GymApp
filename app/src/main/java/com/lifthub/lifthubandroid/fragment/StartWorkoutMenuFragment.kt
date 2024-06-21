package com.lifthub.lifthubandroid.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifthub.lifthubandroid.R
import com.lifthub.lifthubandroid.activity.WorkoutActivity
import com.lifthub.lifthubandroid.adapter.StartWorkoutMenuRecycleViewAdapter
import com.lifthub.lifthubandroid.databinding.FragmentStartWorkoutMenuBinding
import com.lifthub.lifthubandroid.model.trainingPlans.TrainingPlanElement
import com.lifthub.lifthubandroid.persistence.PlansDataBaseHelper
import com.lifthub.lifthubandroid.persistence.RoutinesDataBaseHelper
import com.lifthub.lifthubandroid.viewModel.ActivityResultData
import com.lifthub.lifthubandroid.viewModel.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StartWorkoutMenuFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentStartWorkoutMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var startWorkoutMenuRecyclerViewAdapter: StartWorkoutMenuRecycleViewAdapter
    private var routines: MutableList<TrainingPlanElement> = ArrayList()

    private val startWorkoutActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val sharedViewModel: SharedViewModel by activityViewModels()
            val isUnsaved = result.resultCode == Activity.RESULT_CANCELED
            val routineNameResult = result.data?.getStringExtra(HomeFragment.ROUTINE_NAME)

            sharedViewModel.setActivityResult(ActivityResultData(isUnsaved, routineNameResult))
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

    companion object {
        const val ROUTINE_NAME = "com.lifthub.lifthubandroid.routinename"
        const val PLAN_NAME = "com.lifthub.lifthubandroid.planname"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        when (sharedPreferences.getString("theme", "")) {
            "Default" -> setStyle(STYLE_NORMAL, R.style.DefaultCustomBottomSheetDialogTheme)
            "Dark" -> setStyle(STYLE_NORMAL, R.style.DarkCustomBottomSheetDialogTheme)
            "DarkBlue" -> setStyle(STYLE_NORMAL, R.style.DarkBlueCustomBottomSheetDialogTheme)
            else -> setStyle(STYLE_NORMAL, R.style.DefaultCustomBottomSheetDialogTheme)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartWorkoutMenuBinding.inflate(layoutInflater, container, false)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        when (sharedPreferences.getString("theme", "")) {
            "Default" -> binding.workoutMenu.setBackgroundResource(R.drawable.default_bottom_dialog_sheet_background)
            "Dark" -> binding.workoutMenu.setBackgroundResource(R.drawable.dark_bottom_dialog_sheet_background)
            "DarkBlue" -> binding.workoutMenu.setBackgroundResource(R.drawable.bottom_dialog_sheet_background)
            else -> binding.workoutMenu.setBackgroundResource(R.drawable.default_bottom_dialog_sheet_background)
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener {it->
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behaviour = BottomSheetBehavior.from(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.RecyclerViewStartWorkoutMenu
        val chosenTrainingPlan = arguments?.getString("SELECTED_ITEM_KEY")
        setRecyclerViewContent(chosenTrainingPlan)
        startWorkoutMenuRecyclerViewAdapter = StartWorkoutMenuRecycleViewAdapter(routines)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = startWorkoutMenuRecyclerViewAdapter

        startWorkoutMenuRecyclerViewAdapter.setOnClickListener(object :
            StartWorkoutMenuRecycleViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: TrainingPlanElement) {
                val explicitIntent = Intent(context, WorkoutActivity::class.java)
                explicitIntent.putExtra(ROUTINE_NAME, model.routineName)
                explicitIntent.putExtra(PLAN_NAME, chosenTrainingPlan)
                startWorkoutActivityForResult.launch(explicitIntent)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRecyclerViewContent(planName: String?) {
        val plansDataBase = PlansDataBaseHelper(requireContext(), null)
        val routinesDataBase = RoutinesDataBaseHelper(requireContext(), null)
        if (planName != null) {
            val planId = plansDataBase.getPlanId(planName)
            if (planId != null) {
                routines = routinesDataBase.getRoutinesInPlan(planId)
            }
        }
    }

}