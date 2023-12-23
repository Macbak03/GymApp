package com.example.gymapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.databinding.WorkoutHistoryRecyclerViewItemBinding
import com.example.gymapp.model.workoutHistory.WorkoutHistoryElement

class WorkoutHistoryRecyclerViewAdapter (private val workoutHistoryElements: MutableList<WorkoutHistoryElement>) :
RecyclerView.Adapter<WorkoutHistoryRecyclerViewAdapter.WorkoutHistoryViewHolder>() {

    private var onClickListener: OnClickListener? = null
    inner class WorkoutHistoryViewHolder(binding: WorkoutHistoryRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val trainingPlanName = binding.textViewHistoryPlanName
        val routineName = binding.textViewHistoryRoutineName
        val date = binding.textViewHistoryDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val workoutHistoryElementBinding = WorkoutHistoryRecyclerViewItemBinding.inflate(inflater, parent, false)
        return WorkoutHistoryViewHolder(workoutHistoryElementBinding)
    }

    override fun getItemCount(): Int {
        return workoutHistoryElements.size
    }

    override fun onBindViewHolder(holder: WorkoutHistoryViewHolder, position: Int) {
        holder.trainingPlanName.text = workoutHistoryElements[position].planName
        holder.routineName.text = workoutHistoryElements[position].routineName
        holder.date.text = workoutHistoryElements[position].date.toString()

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, workoutHistoryElements[position])
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: WorkoutHistoryElement)
    }
}