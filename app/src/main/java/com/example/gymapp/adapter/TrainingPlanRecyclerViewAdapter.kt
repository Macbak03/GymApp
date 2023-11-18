package com.example.gymapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.gymapp.databinding.TrainingPlansRecyclerViewItemLayoutBinding
import com.example.gymapp.model.trainingPlans.TrainingPlanElement

class TrainingPlanRecyclerViewAdapter(private val trainingPlanElements: MutableList<TrainingPlanElement>) :
    RecyclerView.Adapter<TrainingPlanRecyclerViewAdapter.TrainingPlanViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class TrainingPlanViewHolder(binding: TrainingPlansRecyclerViewItemLayoutBinding) :
        ViewHolder(binding.root) {
        val trainingPlanName = binding.textViewElement
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingPlanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val trainingPlanItemBinding =
            TrainingPlansRecyclerViewItemLayoutBinding.inflate(inflater, parent, false)
        return TrainingPlanViewHolder(trainingPlanItemBinding)
    }

    override fun getItemCount(): Int {
        return trainingPlanElements.size
    }

    override fun onBindViewHolder(holder: TrainingPlanViewHolder, position: Int) {
        holder.trainingPlanName.text = trainingPlanElements[position].routineName

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