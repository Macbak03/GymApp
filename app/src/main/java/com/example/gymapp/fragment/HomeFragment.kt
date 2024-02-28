package com.example.gymapp.fragment

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.gymapp.R
import com.example.gymapp.activity.HistoryDetailsActivity
import com.example.gymapp.activity.TrainingPlanActivity
import com.example.gymapp.activity.WorkoutActivity
import com.example.gymapp.adapter.SpinnerArrayAdapter
import com.example.gymapp.adapter.ViewPagerAdapter
import com.example.gymapp.animation.FragmentAnimator
import com.example.gymapp.databinding.FragmentHomeBinding
import com.example.gymapp.layout.DynamicSizeSpinner
import com.example.gymapp.model.trainingPlans.TrainingPlan
import com.example.gymapp.model.workout.CustomDate
import com.example.gymapp.persistence.PlansDataBaseHelper
import com.example.gymapp.persistence.RoutinesDataBaseHelper
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import com.example.gymapp.viewModel.SharedViewModel


class HomeFragment : Fragment(), FragmentAnimator {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var plansDataBase: PlansDataBaseHelper
    private var trainingPlansNames: MutableList<TrainingPlan> = ArrayList()
    private lateinit var spinner: DynamicSizeSpinner
    private lateinit var buttonReturn: Button
    private val SPINNER_PREF_KEY = "selectedSpinnerItem"

    private var isUnsaved = false
    private var routineNameResult: String? = null


    companion object {
        const val PLAN_NAME = "com.example.gymapp.planname"
        const val ROUTINE_NAME = "com.example.gymapp.routinename"
        const val FORMATTED_DATE = "com.example.gymapp.formatteddate"
        const val RAW_DATE = "com.example.gymapp.rawdate"
        const val IS_UNSAVED = "com.example.gymapp.isunsaved"
    }

    private val startWorkoutActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_CANCELED) {
                isUnsaved = true
                routineNameResult = result.data?.getStringExtra(ROUTINE_NAME)
                buttonReturn.visibility = View.VISIBLE
            } else if (result.resultCode == RESULT_OK) {
                isUnsaved = false
                buttonReturn.visibility = View.GONE
            }
            spinner.isEnabled = !isUnsaved
            saveResult(isUnsaved)
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = binding.spinnerTrainingPlans


        plansDataBase = PlansDataBaseHelper(requireContext(), null)
        val trainingPlanNamesString = plansDataBase.getColumn(
            PlansDataBaseHelper.TABLE_NAME,
            PlansDataBaseHelper.PLAN_NAME_COLUMN,
            PlansDataBaseHelper.PLAN_ID_COLUMN
        )
        trainingPlansNames = plansDataBase.convertList(trainingPlanNamesString) { TrainingPlan(it) }
        if (!plansDataBase.isTableNotEmpty()) {
            val noneTrainingPlanFound = "Go to training plans section to create your first plan"
            binding.textViewCurrentTrainingPlan.text = noneTrainingPlanFound
            spinner.visibility = View.GONE
        } else {
            spinner.visibility = View.VISIBLE
        }
        initSpinner()

        buttonReturn = binding.buttonReturnToWorkout
        buttonReturn.visibility = View.GONE

        observeViewModel()

        getSavedRoutineName()
        loadResult()
        checkOnWorkoutTerminatePreferences()
        if (isUnsaved) {
            spinner.isEnabled = false
            buttonReturn.visibility = View.VISIBLE
        } else {
            spinner.isEnabled = true
            buttonReturn.visibility = View.GONE
        }



        val routinesDataBase = RoutinesDataBaseHelper(requireContext(), null)
        binding.buttonStartWorkout.setOnClickListener {
            if (!plansDataBase.isTableNotEmpty()) {
                openTrainingPlansFragment()
            } else if (spinner.selectedItem != null) {
                val planName = spinner.selectedItem.toString()
                val planId = plansDataBase.getPlanId(planName)
                if (planId != null && !routinesDataBase.isPlanNotEmpty(planId.toString())) {
                    openRoutinesActivity(planName)
                } else {
                    if (isUnsaved) {
                        showWarningDialog()
                    } else {
                        openStartWorkoutMenuFragment()
                    }

                }
            }
        }
        binding.buttonReturnToWorkout.setOnClickListener {
            val explicitIntent = Intent(context, WorkoutActivity::class.java)
            val planName = spinner.selectedItem.toString()
            explicitIntent.putExtra(IS_UNSAVED, isUnsaved)
            explicitIntent.putExtra(PLAN_NAME, planName)
            explicitIntent.putExtra(ROUTINE_NAME, routineNameResult)
            startWorkoutActivityForResult.launch(explicitIntent)
        }
    }

    override fun onResume() {
        super.onResume()

        setLastTraining()
        val trainingPlanNamesString = plansDataBase.getColumn(
            PlansDataBaseHelper.TABLE_NAME,
            PlansDataBaseHelper.PLAN_NAME_COLUMN,
            PlansDataBaseHelper.PLAN_ID_COLUMN
        )
        trainingPlansNames = plansDataBase.convertList(trainingPlanNamesString) { TrainingPlan(it) }
        if (!plansDataBase.isTableNotEmpty()) {
            val noneTrainingPlanFound = "Go to training plans section to create your first plan"
            binding.textViewCurrentTrainingPlan.text = noneTrainingPlanFound
            binding.textViewCurrentTrainingPlan.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_color)
            spinner.visibility = View.GONE
            spinner.isEnabled = true
            buttonReturn.visibility = View.GONE
            isUnsaved = false
        } else {
            val planFound = "Current plan: "
            binding.textViewCurrentTrainingPlan.text = planFound
            spinner.visibility = View.VISIBLE
        }
        initSpinner()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkOnWorkoutTerminatePreferences(){
        if(!routineNameResult.isNullOrBlank())
        {
            isUnsaved = true
        }
    }

    private fun getSavedRoutineName(){
        val prefs = activity?.getSharedPreferences("TerminatePreferences", Context.MODE_PRIVATE)
        routineNameResult = prefs?.getString("ROUTINE_NAME", "")
    }

    private fun openStartWorkoutMenuFragment() {
        val selectedSpinnerItem = spinner.selectedItem as TrainingPlan?
        if (selectedSpinnerItem != null) {
            val bundle = Bundle()
            bundle.putString("SELECTED_ITEM_KEY", selectedSpinnerItem.name)
            val startWorkoutMenuFragment = StartWorkoutMenuFragment()
            startWorkoutMenuFragment.arguments = bundle
            requireActivity().supportFragmentManager.let {
                startWorkoutMenuFragment.show(
                    it,
                    "WorkoutMenu"
                )
            }
        }
    }

    private fun initSpinner() {
        val adapter = SpinnerArrayAdapter(requireContext(), R.layout.spinner_header, trainingPlansNames)
        //adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.adapter = adapter

        val savedSelection = loadSpinnerSelection()
        if (savedSelection.isNotEmpty()) {
            val selectionIndex = trainingPlansNames.indexOfFirst { it.name == savedSelection }
            if (selectionIndex != AdapterView.INVALID_POSITION) {
                spinner.setSelection(selectionIndex)
            }
        }

        with(spinner)
        {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item = parent?.getItemAtPosition(position) as TrainingPlan?
                    if (item != null) {
                        saveSpinnerSelection(item.toString())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun saveSpinnerSelection(selectedItem: String) {
        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(SPINNER_PREF_KEY, selectedItem)
        editor.apply()
    }

    private fun loadSpinnerSelection(): String {
        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(SPINNER_PREF_KEY, "") ?: ""
    }

    private fun openTrainingPlansFragment() {
        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        val adapter = ViewPagerAdapter(requireActivity())
        viewPager.adapter = adapter
        viewPager.currentItem = ViewPagerAdapter.TRAINING_PLANS_FRAGMENT
    }

    private fun openRoutinesActivity(planName: String) {
        val explicitIntent = Intent(context, TrainingPlanActivity::class.java)
        explicitIntent.putExtra(TrainingPlansFragment.NEXT_SCREEN, planName)
        startActivity(explicitIntent)
    }

    private fun setLastTraining() {
        val historyDataBase = WorkoutHistoryDatabaseHelper(requireContext(), null)
        val customDate = CustomDate()
        if (!historyDataBase.isTableNotEmpty()) {
            binding.linearLayoutLastWorkout.visibility = View.GONE
        } else {
            binding.linearLayoutLastWorkout.visibility = View.VISIBLE
            val rawDate = historyDataBase.getLastWorkout()[1]
            if (rawDate != null) {
                val date = customDate.getFormattedDate(rawDate)
                binding.textViewLastTrainingPlanName.text = historyDataBase.getLastWorkout()[0]
                binding.textViewLastTrainingDate.text = date
                binding.textViewLastTrainingRoutineName.text = historyDataBase.getLastWorkout()[2]
                setLastTrainingClick(rawDate)
            }
        }
    }

    private fun setLastTrainingClick(rawDate: String) {
        binding.cardViewLastWorkout.setOnClickListener {
            val explicitIntent = Intent(context, HistoryDetailsActivity::class.java)
            explicitIntent.putExtra(PLAN_NAME, binding.textViewLastTrainingPlanName.text)
            explicitIntent.putExtra(ROUTINE_NAME, binding.textViewLastTrainingRoutineName.text)
            explicitIntent.putExtra(FORMATTED_DATE, binding.textViewLastTrainingDate.text)
            explicitIntent.putExtra(RAW_DATE, rawDate)
            startActivity(explicitIntent)
        }
    }

    private fun saveResult(boolValue: Boolean) {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val keyBool = "is_unsaved"

        editor.putBoolean(keyBool, boolValue)

        editor.apply()
    }

    private fun loadResult() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        val keyBool = "is_unsaved"

        isUnsaved = sharedPreferences.getBoolean(keyBool, false)
    }

    private fun observeViewModel() {
        val sharedViewModel: SharedViewModel by activityViewModels()

        sharedViewModel.activityResult.observe(viewLifecycleOwner) { resultData ->
            isUnsaved = resultData.isUnsaved
            routineNameResult = resultData.routineNameResult
            buttonReturn.visibility = if (resultData.shouldShowButton) View.VISIBLE else View.GONE
            spinner.isEnabled = !isUnsaved
            saveResult(isUnsaved)
        }
    }


    private fun showWarningDialog() {
        val builder = context?.let { AlertDialog.Builder(it, R.style.YourAlertDialogTheme) }
        with(builder) {
            this?.setTitle("You have unsaved workout that will be lost. Do you want to start new one?")
            this?.setPositiveButton("Yes") { _, _ ->
                openStartWorkoutMenuFragment()
            }
            this?.setNegativeButton("No") { _, _ -> }
            this?.show()
        }
    }

    override fun triggerAnimation() {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
        requireView().startAnimation(slideIn)
    }

}