package com.example.gymapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.adapter.WorkoutHistoryRecyclerViewAdapter
import com.example.gymapp.databinding.FragmentTrainingHistoryBinding
import com.example.gymapp.model.workout.CustomDate
import com.example.gymapp.model.workoutHistory.WorkoutHistoryElement
import com.example.gymapp.persistence.RoutinesDataBaseHelper
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

class TrainingHistoryFragment : Fragment() {
    private var _binding: FragmentTrainingHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutHistoryRecyclerViewAdapter: WorkoutHistoryRecyclerViewAdapter
    private var workoutHistory: MutableList<WorkoutHistoryElement> = ArrayList()
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
                TODO("Not yet implemented")
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
        if (cursor.moveToFirst())
        {
            val savedDate = cursor.getString(cursor.getColumnIndexOrThrow(
                WorkoutHistoryDatabaseHelper.DATE_COLUMN))
            val date = getFormatDate(savedDate)
            workoutHistory.add(WorkoutHistoryElement(cursor.getString(cursor.getColumnIndexOrThrow(
                WorkoutHistoryDatabaseHelper.PLAN_NAME_COLUMN)), cursor.getString(cursor.getColumnIndexOrThrow(
                WorkoutHistoryDatabaseHelper.ROUTINE_NAME_COLUMN)), date
            ))
            workoutHistoryRecyclerViewAdapter.notifyItemInserted(workoutHistoryRecyclerViewAdapter.itemCount)
            while (cursor.moveToNext())
            {
                val nextSavedDate = cursor.getString(cursor.getColumnIndexOrThrow(
                    WorkoutHistoryDatabaseHelper.DATE_COLUMN))
                val nextDate = getFormatDate(nextSavedDate)
                workoutHistory.add(WorkoutHistoryElement(cursor.getString(cursor.getColumnIndexOrThrow(
                    WorkoutHistoryDatabaseHelper.PLAN_NAME_COLUMN)), cursor.getString(cursor.getColumnIndexOrThrow(
                    WorkoutHistoryDatabaseHelper.ROUTINE_NAME_COLUMN)), nextDate
                ))
                workoutHistoryRecyclerViewAdapter.notifyItemInserted(workoutHistoryRecyclerViewAdapter.itemCount)
            }
        }
    }

    private fun getFormatDate(savedDate: String): String {
        val inputFormat = SimpleDateFormat(CustomDate.pattern, Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        val date = inputFormat.parse(savedDate)
        return if (date != null) {
            outputFormat.format(date)
        } else {
            "dateError"
        }
    }
}