package com.pl.Maciejbak.activity

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ExpandableListView
import com.pl.Maciejbak.adapter.WorkoutHistoryExpandableListAdapter
import com.pl.Maciejbak.databinding.ActivityHistoryDetailsBinding
import com.pl.Maciejbak.fragment.TrainingHistoryFragment
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft
import com.pl.Maciejbak.persistence.WorkoutHistoryDatabaseHelper
import com.pl.Maciejbak.persistence.WorkoutSeriesDataBaseHelper

class HistoryDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityHistoryDetailsBinding

    private lateinit var expandableListView: ExpandableListView
    private lateinit var workoutExpandableListAdapter: WorkoutHistoryExpandableListAdapter
    private val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(this, null)
    private val workoutSeriesDatabase = WorkoutSeriesDataBaseHelper(this, null)
    private val workout: MutableList<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>> = ArrayList()
    private var routineName: String? = null
    private var planName: String? = null
    private var rawDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        loadTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(TrainingHistoryFragment.ROUTINE_NAME) && intent.hasExtra(TrainingHistoryFragment.FORMATTED_DATE)
            && intent.hasExtra(TrainingHistoryFragment.PLAN_NAME) && intent.hasExtra(TrainingHistoryFragment.RAW_DATE))
        {
            routineName = intent.getStringExtra(TrainingHistoryFragment.ROUTINE_NAME)
            planName = intent.getStringExtra(TrainingHistoryFragment.PLAN_NAME)
            rawDate = intent.getStringExtra(TrainingHistoryFragment.RAW_DATE)
            binding.textViewHistoryDetailRoutineName.text = routineName
            binding.textViewHistoryDetailDate.text = intent.getStringExtra(TrainingHistoryFragment.FORMATTED_DATE)

        }

        expandableListView = binding.expandableListViewHistoryDetails
        workoutExpandableListAdapter = WorkoutHistoryExpandableListAdapter(this, workout, planName)
        expandableListView.setAdapter(workoutExpandableListAdapter)

        binding.goBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        loadWorkoutHistoryDetails()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun loadWorkoutHistoryDetails()
    {
        val rawDate = this.rawDate
        val routineName = this.routineName
        val planName = this.planName
        if(rawDate != null && routineName != null && planName != null)
        {
            val workoutExercises = workoutHistoryDatabase.getWorkoutExercises(rawDate, routineName, planName)
            for (workoutExercise in workoutExercises)
            {
                val exerciseId = workoutExercise.exerciseName?.let {
                    workoutHistoryDatabase.getExerciseID(rawDate, it)
                }
                if(exerciseId != null)
                {
                    val workoutSeries = workoutSeriesDatabase.getSeries(exerciseId)
                    workout.add(Pair(workoutExercise, workoutSeries))
                    workoutExpandableListAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}