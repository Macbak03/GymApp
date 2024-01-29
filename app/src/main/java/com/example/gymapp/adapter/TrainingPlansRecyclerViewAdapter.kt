package com.example.gymapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.gymapp.animation.Animations
import com.example.gymapp.databinding.TrainingPlansRecyclerViewItemLayoutBinding
import com.example.gymapp.model.trainingPlans.TrainingPlan
import com.example.gymapp.persistence.PlansDataBaseHelper

class TrainingPlansRecyclerViewAdapter(private val trainingPlans: MutableList<TrainingPlan>, private val deleteButton: Button
) :
    RecyclerView.Adapter<TrainingPlansRecyclerViewAdapter.TrainingPlansViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var onLongCLickListener: OnLongClickListener? = null
    private val animations: Animations = Animations()

    var isLongClickActivated = false

    private var checkBoxTracker = 0
    private val deletedTrainingPlans: MutableList<String> = ArrayList()

    inner class TrainingPlansViewHolder(binding: TrainingPlansRecyclerViewItemLayoutBinding) :
        ViewHolder(binding.root) {
        val trainingPlanName = binding.textViewElement
        val checkBox = binding.checkBoxElement
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingPlansViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val trainingPlansItemBinding =
            TrainingPlansRecyclerViewItemLayoutBinding.inflate(inflater, parent, false)
        return TrainingPlansViewHolder(trainingPlansItemBinding)
    }

    override fun getItemCount(): Int {
        return trainingPlans.size
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TrainingPlansViewHolder, position: Int) {
        val trainingPlan = trainingPlans[position]
        holder.trainingPlanName.text = trainingPlan.name
        holder.checkBox.isChecked = trainingPlan.isSelected

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                checkBoxTracker ++
                trainingPlan.isSelected = true
            } else {
                checkBoxTracker --
                trainingPlan.isSelected = false
            }
            if(checkBoxTracker == 0){
                switchButtonAccessibility(false)
            }else{
                switchButtonAccessibility(true)
            }
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, trainingPlans[position])
            }
        }

        holder.itemView.setOnLongClickListener {
            if (onLongCLickListener != null) {
                onLongCLickListener!!.onLongClick(position, trainingPlans[position])
                isLongClickActivated = true
                trainingPlan.isSelected = true
                notifyDataSetChanged()
            }
            true
        }

        if (isLongClickActivated) {
            showCheckBox(holder)
        } else {
            hideCheckBox(holder)
        }
    }

    private fun showCheckBox(holder: TrainingPlansViewHolder) {
        holder.checkBox.visibility = View.VISIBLE
        holder.checkBox.animate()
            .alpha(1f)
            .setDuration(200)
            .withStartAction {
                animations.translateX(
                    holder.trainingPlanName.translationX,
                    holder.checkBox.width.toFloat(),
                    holder.trainingPlanName,
                    300
                )
            }
            .withEndAction(null)
            .start()
    }

    private fun hideCheckBox(holder: TrainingPlansViewHolder) {
        holder.checkBox.animate()
            .alpha(0f)
            .setDuration(200)
            .withStartAction {
                animations.translateX(
                    holder.trainingPlanName.translationX,
                    0f,
                    holder.trainingPlanName,
                    300
                )
            }
            .withEndAction {
                holder.checkBox.visibility = View.GONE
                holder.checkBox.clearAnimation()
            }
            .start()
    }

    private fun switchButtonAccessibility(switch: Boolean)
    {
        deleteButton.isEnabled = switch
    }



    fun deleteSinglePlan(position: Int){
        deletedTrainingPlans.clear()
        deletedTrainingPlans.add(trainingPlans[position].name)
        trainingPlans.removeAt(position)
        notifyItemRemoved(position)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun deletePlansFromRecyclerView()
    {
        deletedTrainingPlans.clear()
        trainingPlans.removeAll {
            if(it.isSelected)
            {
                deletedTrainingPlans.add(it.name)
                true
            } else
            {
                false
            }
        }
        notifyDataSetChanged()
    }

    fun getDeletedPlans() : MutableList<String>
    {
        return deletedTrainingPlans
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener) {
        this.onLongCLickListener = onLongClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetLongClickState() {
        isLongClickActivated = false
        for (trainingPlan in trainingPlans) {
            trainingPlan.isSelected = false
        }
        checkBoxTracker = 0
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onClick(position: Int, model: TrainingPlan)
    }

    interface OnLongClickListener {
        fun onLongClick(position: Int, model: TrainingPlan)
    }
}