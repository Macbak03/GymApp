package com.example.gymapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.databinding.TrainingPlansRecyclerViewItemLayoutBinding
import com.example.gymapp.model.trainingPlans.TrainingPlanElement

class StartWorkoutMenuRecycleViewAdapter (private val trainingPlanElements: MutableList<TrainingPlanElement>) :
    RecyclerView.Adapter<StartWorkoutMenuRecycleViewAdapter.StartWorkoutMenuViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class StartWorkoutMenuViewHolder(binding: TrainingPlansRecyclerViewItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val trainingPlanName = binding.textViewElement
        val moreButton = binding.moreButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StartWorkoutMenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val trainingPlanItemBinding =
            TrainingPlansRecyclerViewItemLayoutBinding.inflate(inflater, parent, false)
        return StartWorkoutMenuViewHolder(trainingPlanItemBinding)
    }

    override fun getItemCount(): Int {
        return trainingPlanElements.size
    }

    override fun onBindViewHolder(holder: StartWorkoutMenuViewHolder, position: Int) {
        holder.trainingPlanName.text = trainingPlanElements[position].routineName
        holder.moreButton.visibility = View.GONE

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, trainingPlanElements[position])
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: TrainingPlanElement)
    }
}
