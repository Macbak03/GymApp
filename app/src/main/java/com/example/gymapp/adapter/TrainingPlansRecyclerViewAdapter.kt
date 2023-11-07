package com.example.gymapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.gymapp.databinding.TrainingPlansRecyclerViewItemLayoutBinding
import com.example.gymapp.model.trainingPlans.TrainingPlanName

class TrainingPlansRecyclerViewAdapter(private val trainingPlans: MutableList<TrainingPlanName>): RecyclerView.Adapter<TrainingPlansRecyclerViewAdapter.TrainingPlansViewHolder>(){

    inner class TrainingPlansViewHolder(binding: TrainingPlansRecyclerViewItemLayoutBinding) : ViewHolder(binding.root){
        val trainingPlanName = binding.textViewTrainingPlanName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingPlansViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val trainingPlansItemBinding = TrainingPlansRecyclerViewItemLayoutBinding.inflate(inflater, parent, false)
        return TrainingPlansViewHolder(trainingPlansItemBinding)
    }

    override fun getItemCount(): Int {
        return trainingPlans.size
    }

    override fun onBindViewHolder(holder: TrainingPlansViewHolder, position: Int) {
        holder.trainingPlanName.text = trainingPlans[position].name
    }
}