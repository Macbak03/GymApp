package com.example.gymapp.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.activity.WorkoutActivity
import com.example.gymapp.adapter.StartWorkoutMenuRecycleViewAdapter
import com.example.gymapp.databinding.FragmentStartWorkoutMenuBinding
import com.example.gymapp.model.trainingPlans.TrainingPlanElement
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StartWorkoutMenuFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentStartWorkoutMenuBinding? = null
    private val binding get() = _binding!!
    private var homeFragment: HomeFragment? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var startWorkoutMenuRecyclerViewAdapter: StartWorkoutMenuRecycleViewAdapter
    private var routines: MutableList<TrainingPlanElement> = ArrayList()

    private val startWorkoutActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            homeFragment?.let {
                if (result.resultCode == Activity.RESULT_CANCELED) {
                    it.isUnsaved = true
                    it.routineNameResult = result.data?.getStringExtra(HomeFragment.ROUTINE_NAME)
                    it.buttonReturn.visibility = View.VISIBLE
                    requireActivity().supportFragmentManager.beginTransaction().remove(this@StartWorkoutMenuFragment).commit()
                } else if (result.resultCode == Activity.RESULT_OK) {
                    it.isUnsaved = false
                    it.buttonReturn.visibility = View.GONE
                    requireActivity().supportFragmentManager.beginTransaction().remove(this@StartWorkoutMenuFragment).commit()
                }
                it.spinner.isEnabled = !it.isUnsaved
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
                it.routineNameResult?.let { it1 -> saveResult(it.isUnsaved, it1) }
            }
        }

    companion object {
        const val ROUTINE_NAME = "com.example.gymapp.routinename"
        const val PLAN_NAME = "com.example.gymapp.planname"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartWorkoutMenuBinding.inflate(layoutInflater, container, false)
        homeFragment = parentFragmentManager.findFragmentByTag("HomeFragment") as? HomeFragment
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

    private fun saveResult(boolValue: Boolean, stringValue: String)
    {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val keyBool = "is_unsaved"
        val keyRoutineName = "routine_name"

        editor.putBoolean(keyBool, boolValue)
        editor.putString(keyRoutineName, stringValue)

        editor.apply()
    }
}