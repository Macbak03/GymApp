package com.example.gymapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.activity.HistoryDetailsActivity
import com.example.gymapp.adapter.WorkoutHistoryRecyclerViewAdapter
import com.example.gymapp.databinding.FragmentTrainingHistoryBinding
import com.example.gymapp.model.workout.CustomDate
import com.example.gymapp.model.workoutHistory.WorkoutHistoryElement
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import java.text.SimpleDateFormat
import java.util.Locale

class TrainingHistoryFragment : Fragment() {
    private var _binding: FragmentTrainingHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutHistoryRecyclerViewAdapter: WorkoutHistoryRecyclerViewAdapter
    private var workoutHistory: MutableList<WorkoutHistoryElement> = ArrayList()

    companion object {
        const val PLAN_NAME = "com.example.gymapp.planname"
        const val ROUTINE_NAME = "com.example.gymapp.routinename"
        const val FORMATTED_DATE =  "com.example.gymapp.formatteddate"
        const val RAW_DATE = "com.example.gymapp.rawdate"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerViewWorkoutHistory
        workoutHistoryRecyclerViewAdapter = WorkoutHistoryRecyclerViewAdapter(workoutHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = workoutHistoryRecyclerViewAdapter
        setRecyclerViewContent()

        workoutHistoryRecyclerViewAdapter.setOnClickListener(object :
            WorkoutHistoryRecyclerViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: WorkoutHistoryElement) {
                val explicitIntent = Intent(context, HistoryDetailsActivity::class.java)
                explicitIntent.putExtra(PLAN_NAME, model.planName)
                explicitIntent.putExtra(ROUTINE_NAME, model.routineName)
                explicitIntent.putExtra(FORMATTED_DATE, model.formattedDate)
                explicitIntent.putExtra(RAW_DATE, model.rawDate)
                startActivity(explicitIntent)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRecyclerViewContent(){
        val workoutHistoryDatabase = WorkoutHistoryDatabaseHelper(requireContext(), null)
        val cursor = workoutHistoryDatabase.getHistory()
        val customDate = CustomDate()
        if (cursor.moveToFirst())
        {
            val savedDate = cursor.getString(cursor.getColumnIndexOrThrow(
                WorkoutHistoryDatabaseHelper.DATE_COLUMN))
            val date = customDate.getFormattedDate(savedDate)
            workoutHistory.add(WorkoutHistoryElement(cursor.getString(cursor.getColumnIndexOrThrow(
                WorkoutHistoryDatabaseHelper.PLAN_NAME_COLUMN)), cursor.getString(cursor.getColumnIndexOrThrow(
                WorkoutHistoryDatabaseHelper.ROUTINE_NAME_COLUMN)), date, savedDate
            ))
            workoutHistoryRecyclerViewAdapter.notifyItemInserted(workoutHistoryRecyclerViewAdapter.itemCount)
            while (cursor.moveToNext())
            {
                val nextSavedDate = cursor.getString(cursor.getColumnIndexOrThrow(
                    WorkoutHistoryDatabaseHelper.DATE_COLUMN))
                val nextDate = customDate.getFormattedDate(nextSavedDate)
                workoutHistory.add(WorkoutHistoryElement(cursor.getString(cursor.getColumnIndexOrThrow(
                    WorkoutHistoryDatabaseHelper.PLAN_NAME_COLUMN)), cursor.getString(cursor.getColumnIndexOrThrow(
                    WorkoutHistoryDatabaseHelper.ROUTINE_NAME_COLUMN)), nextDate, nextSavedDate
                ))
                workoutHistoryRecyclerViewAdapter.notifyItemInserted(workoutHistoryRecyclerViewAdapter.itemCount)
            }
        }
    }

}