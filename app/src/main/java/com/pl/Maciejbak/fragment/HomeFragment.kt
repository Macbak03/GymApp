package com.pl.Maciejbak.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.pl.Maciejbak.R
import com.pl.Maciejbak.activity.HistoryDetailsActivity
import com.pl.Maciejbak.activity.NoPlanWorkoutActivity
import com.pl.Maciejbak.activity.TrainingPlanActivity
import com.pl.Maciejbak.activity.WorkoutActivity
import com.pl.Maciejbak.adapter.SpinnerArrayAdapter
import com.pl.Maciejbak.adapter.ViewPagerAdapter
import com.pl.Maciejbak.animation.FragmentAnimator
import com.pl.Maciejbak.databinding.FragmentHomeBinding
import com.pl.Maciejbak.exception.ValidationException
import com.pl.Maciejbak.layout.DynamicSizeSpinner
import com.pl.Maciejbak.model.trainingPlans.TrainingPlan
import com.pl.Maciejbak.model.workout.CustomDate
import com.pl.Maciejbak.persistence.PlansDataBaseHelper
import com.pl.Maciejbak.persistence.RoutinesDataBaseHelper
import com.pl.Maciejbak.persistence.WorkoutHistoryDatabaseHelper
import com.pl.Maciejbak.viewModel.ActivityResultData
import com.pl.Maciejbak.viewModel.SharedViewModel
import kotlin.math.exp


class HomeFragment : Fragment(), FragmentAnimator {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private lateinit var plansDataBase: PlansDataBaseHelper
    private var trainingPlansNames: MutableList<TrainingPlan> = ArrayList()
    private lateinit var spinner: DynamicSizeSpinner
    private lateinit var buttonReturn: Button


    private var isUnsaved = false
    private var routineNameResult: String? = null
    private var isNewWorkoutStartedWithoutCancel = false

    private var prefs: SharedPreferences? = null
    private var savedPlanName: String? = null


    companion object {
        const val PLAN_NAME = "com.pl.Maciejbak.planname"
        const val ROUTINE_NAME = "com.pl.Maciejbak.routinename"
        const val FORMATTED_DATE = "com.pl.Maciejbak.formatteddate"
        const val RAW_DATE = "com.pl.Maciejbak.rawdate"
        const val IS_UNSAVED = "com.pl.Maciejbak.isunsaved"
        const val IS_NEW_WORKOUT_STARTED_WITHOUT_CANCEL = "com.pl.Maciejbak.newworkoutnocancel"

        const val NO_TRAINING_PLAN_OPTION = "No training plan"

        private const val SELECTED_SPINNER_ITEM_ID = "com.pl.Maciejbak.selectedSpinnerItem"
        private const val IS_WORKOUT_UNSAVED_ID = "com.pl.Maciejbak.isWorkoutUnsaved"
    }

    private val startWorkoutActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val sharedViewModel: SharedViewModel by activityViewModels()
            val isUnsaved = result.resultCode == RESULT_CANCELED
            val routineNameResult = result.data?.getStringExtra(ROUTINE_NAME)
            sharedViewModel.setActivityResult(ActivityResultData(isUnsaved, routineNameResult))
            spinner.isEnabled = !isUnsaved
            saveResult(isUnsaved)
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        prefs = activity?.getSharedPreferences("TerminatePreferences", Context.MODE_PRIVATE)
        savedPlanName = prefs?.getString(PLAN_NAME, "")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = binding.spinnerTrainingPlans

        setLastTraining()


        plansDataBase = PlansDataBaseHelper(requireContext(), null)

        initSpinnerData()
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

        binding.textViewCurrentTrainingPlan.isClickable = true
        binding.textViewCurrentTrainingPlan.setOnClickListener {
            openTrainingPlansFragment()
        }


        val routinesDataBase = RoutinesDataBaseHelper(requireContext(), null)
        binding.buttonStartWorkout.setOnClickListener {
            try {
                checkSpinnerSelection(routinesDataBase)
            } catch (validationException: ValidationException) {
                Toast.makeText(requireContext(), validationException.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.buttonReturnToWorkout.setOnClickListener {
            if (isNoPlanOptionSelected() && savedPlanName == NO_TRAINING_PLAN_OPTION) {
                startWorkout(Intent(context, NoPlanWorkoutActivity::class.java))
            } else {
                startWorkout(Intent(context, WorkoutActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        buttonReturn.setReturnButtonColor()
        isNewWorkoutStartedWithoutCancel = false
        setLastTraining()
        initSpinnerData()
        if (!plansDataBase.isTableNotEmpty() && savedPlanName != NO_TRAINING_PLAN_OPTION) {
            spinner.isEnabled = true
            buttonReturn.visibility = View.GONE
            isUnsaved = false
        } else {
            val planFound = "Current plan:"
            binding.textViewCurrentTrainingPlan.text = planFound
        }
        initSpinner()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isNoPlanOptionSelected(): Boolean {
        return spinner.selectedItem.toString() == NO_TRAINING_PLAN_OPTION
    }

    private fun checkSpinnerSelection(routinesDataBase: RoutinesDataBaseHelper) {
        if (spinner.selectedItem == null) {
            throw ValidationException("No training plan selected")
        }
        if (!isNoPlanOptionSelected()) {
            val planName = spinner.selectedItem.toString()
            val planId = plansDataBase.getPlanId(planName)
            if (planId != null && !routinesDataBase.isPlanNotEmpty(planId.toString())) {
                openRoutinesActivity(planName)
            } else {
                handleNewWorkoutBasedOnSavedStatus()
            }
        } else {
            handleNewWorkoutBasedOnSavedStatus()
        }
    }

    private fun handleNewWorkoutBasedOnSavedStatus() {
        if (isUnsaved) {
            showWarningDialog()
        } else {
            if (isNoPlanOptionSelected()) {
                startWorkout(Intent(context, NoPlanWorkoutActivity::class.java))
            } else {
                openStartWorkoutMenuFragment()
            }
        }
    }


    private fun checkOnWorkoutTerminatePreferences() {
        if (!routineNameResult.isNullOrBlank()) {
            isUnsaved = true
        }
    }

    private fun View.setReturnButtonColor() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        when (sharedPreferences.getString("theme", "")) {
            "Default" -> setBackgroundResource(R.drawable.clicked_default_button)
            "Dark" -> setBackgroundResource(R.drawable.dark_button_color)
            "DarkBlue" -> setBackgroundResource(R.drawable.button_color)
            else -> setBackgroundResource(R.drawable.clicked_button_color)
        }
    }

    private fun getSavedRoutineName() {
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

    private fun initSpinnerData() {
        val trainingPlanNamesString = plansDataBase.getColumn(
            PlansDataBaseHelper.TABLE_NAME,
            PlansDataBaseHelper.PLAN_NAME_COLUMN,
            PlansDataBaseHelper.PLAN_ID_COLUMN
        )
        trainingPlanNamesString.add(0, NO_TRAINING_PLAN_OPTION)
        trainingPlansNames = plansDataBase.convertList(trainingPlanNamesString) { TrainingPlan(it) }
    }

    private fun initSpinner() {
        val adapter =
            SpinnerArrayAdapter(requireContext(), R.layout.spinner_header, trainingPlansNames)
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
            requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(SELECTED_SPINNER_ITEM_ID, selectedItem)
        editor.apply()
    }

    private fun loadSpinnerSelection(): String {
        val sharedPreferences =
            requireActivity().getPreferences(Context.MODE_PRIVATE)
        return sharedPreferences.getString(SELECTED_SPINNER_ITEM_ID, NO_TRAINING_PLAN_OPTION)
            ?: NO_TRAINING_PLAN_OPTION
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

    private fun startWorkout(explicitIntent: Intent) {
        val planName = spinner.selectedItem.toString()
        explicitIntent.putExtra(IS_UNSAVED, isUnsaved)
        explicitIntent.putExtra(PLAN_NAME, planName)
        explicitIntent.putExtra(ROUTINE_NAME, routineNameResult)
        explicitIntent.putExtra(RAW_DATE, CustomDate().getDate())
        explicitIntent.putExtra(IS_NEW_WORKOUT_STARTED_WITHOUT_CANCEL, isNewWorkoutStartedWithoutCancel)
        startWorkoutActivityForResult.launch(explicitIntent)
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

        editor.putBoolean(IS_WORKOUT_UNSAVED_ID, boolValue)

        editor.apply()
    }

    private fun loadResult() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        isUnsaved = sharedPreferences.getBoolean(IS_WORKOUT_UNSAVED_ID, false)
    }

    private fun observeViewModel() {
        val sharedViewModel: SharedViewModel by activityViewModels()

        sharedViewModel.activityResult.observe(viewLifecycleOwner) { resultData ->
            isUnsaved = resultData.isUnsaved
            routineNameResult = resultData.routineNameResult
            buttonReturn.visibility = if (resultData.isUnsaved) View.VISIBLE else View.GONE
            spinner.isEnabled = !isUnsaved
            saveResult(isUnsaved)
        }
    }


    @SuppressLint("InflateParams")
    private fun showWarningDialog() {
        val builder = context?.let { AlertDialog.Builder(it, R.style.YourAlertDialogTheme) }
        val dialogLayout = layoutInflater.inflate(R.layout.text_view_dialog_layout, null)
        val textView = dialogLayout.findViewById<TextView>(R.id.textViewDialog)
        val message = "You have unsaved workout that will be lost. Do you want to start new one?"
        textView.text = message
        with(builder) {
            this?.setTitle(" ")
            this?.setPositiveButton("Yes") { _, _ ->
                if (isNoPlanOptionSelected()) {
                    isNewWorkoutStartedWithoutCancel = true
                    startWorkout(Intent(context, NoPlanWorkoutActivity::class.java))
                } else {
                    openStartWorkoutMenuFragment()
                }
            }
            this?.setNegativeButton("No") { _, _ -> }
            this?.setView(dialogLayout)
            this?.show()
        }
    }

    override fun triggerAnimation() {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
        requireView().startAnimation(slideIn)
    }

}