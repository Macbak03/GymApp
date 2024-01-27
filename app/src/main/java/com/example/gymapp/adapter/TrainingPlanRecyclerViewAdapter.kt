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
import com.example.gymapp.model.trainingPlans.TrainingPlanElement
import com.example.gymapp.persistence.RoutinesDataBaseHelper

class TrainingPlanRecyclerViewAdapter(private val routines: MutableList<TrainingPlanElement>, private val deleteButton: Button,
) :
    RecyclerView.Adapter<TrainingPlanRecyclerViewAdapter.TrainingPlanViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var onLongCLickListener: OnLongClickListener? = null

    private val animations: Animations = Animations()

    var isLongClickActivated = false
    private var checkBoxTracker = 0
    private val deletedRoutines: MutableList<String> = ArrayList()

    inner class TrainingPlanViewHolder(binding: TrainingPlansRecyclerViewItemLayoutBinding) :
        ViewHolder(binding.root) {
        val routineName = binding.textViewElement
        val checkBox = binding.checkBoxElement
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
        holder.routineName.text = routine.routineName
        holder.checkBox.isChecked = routine.isSelected

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                checkBoxTracker ++
                routine.isSelected = true
            } else {
                checkBoxTracker --
                routine.isSelected = false
            }
            if(checkBoxTracker == 0){
                switchButtonAccessibility(false)
            }else{
                switchButtonAccessibility(true)
            }
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, routines[position])
            }
        }

        holder.itemView.setOnLongClickListener {
            if (onLongCLickListener != null) {
                onLongCLickListener!!.onLongClick(position, routines[position])
                isLongClickActivated = true
                routine.isSelected = true
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

    private fun showCheckBox(holder: TrainingPlanViewHolder) {
        holder.checkBox.visibility = View.VISIBLE
        holder.checkBox.animate()
            .alpha(1f)
            .setDuration(200)
            .withStartAction {
                animations.translateX(
                    holder.routineName.translationX,
                    holder.checkBox.width.toFloat(),
                    holder.routineName,
                    300
                )
            }
            .withEndAction(null)
            .start()
    }

    private fun hideCheckBox(holder: TrainingPlanViewHolder) {
        holder.checkBox.animate()
            .alpha(0f)
            .setDuration(200)
            .withStartAction {
                animations.translateX(
                    holder.routineName.translationX,
                    0f,
                    holder.routineName,
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

    fun deleteSingleRoutine(position: Int){
        deletedRoutines.clear()
        deletedRoutines.add(routines[position].routineName)
        routines.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteRoutinesFromRecyclerView()
    {
        deletedRoutines.clear()
        routines.removeAll {
            if(it.isSelected)
            {
                deletedRoutines.add(it.routineName)
                true
            } else
            {
                false
            }
        }
        notifyDataSetChanged()
    }

    fun getDeletedRoutines() : MutableList<String>
    {
        return deletedRoutines
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
        for (routine in routines) {
            routine.isSelected = false
        }
        checkBoxTracker = 0
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onClick(position: Int, model: TrainingPlanElement)
    }

    interface OnLongClickListener {
        fun onLongClick(position: Int, model: TrainingPlanElement)
    }
}