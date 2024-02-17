package com.example.gymapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.gymapp.animation.Animations
import com.example.gymapp.databinding.TrainingPlansRecyclerViewItemLayoutBinding
import com.example.gymapp.model.trainingPlans.TrainingPlanElement

class TrainingPlanRecyclerViewAdapter(private val routines: MutableList<TrainingPlanElement>) :
    RecyclerView.Adapter<TrainingPlanRecyclerViewAdapter.TrainingPlanViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private val deletedRoutines: MutableList<String> = ArrayList()

    inner class TrainingPlanViewHolder(binding: TrainingPlansRecyclerViewItemLayoutBinding) :
        ViewHolder(binding.root) {
        val routineNameTextView = binding.textViewElement
        val moreButton = binding.moreButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingPlanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val trainingPlanItemBinding =
            TrainingPlansRecyclerViewItemLayoutBinding.inflate(inflater, parent, false)
        return TrainingPlanViewHolder(trainingPlanItemBinding)
    }

    override fun getItemCount(): Int {
        return routines.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TrainingPlanViewHolder, position: Int) {
        val routine = routines[position]
        holder.routineNameTextView.text = routine.routineName
        holder.moreButton.visibility = View.GONE


        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, routines[position])
            }
        }
    }


    fun deleteSingleRoutine(position: Int){
        deletedRoutines.clear()
        deletedRoutines.add(routines[position].routineName)
        routines.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getDeletedRoutines() : MutableList<String>
    {
        return deletedRoutines
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: TrainingPlanElement)
    }
}