package com.example.gymapp.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.gymapp.animation.Animations
import com.example.gymapp.databinding.TrainingPlansRecyclerViewItemLayoutBinding
import com.example.gymapp.fragment.TrainingPlansFragment
import com.example.gymapp.fragment.TrainingPlansMoreFragment
import com.example.gymapp.model.trainingPlans.TrainingPlan

class TrainingPlansRecyclerViewAdapter(private val trainingPlans: MutableList<TrainingPlan>, private val fragment: Fragment) :
    RecyclerView.Adapter<TrainingPlansRecyclerViewAdapter.TrainingPlansViewHolder>() {

    private var onClickListener: OnClickListener? = null

    private val deletedTrainingPlans: MutableList<String> = ArrayList()

    inner class TrainingPlansViewHolder(binding: TrainingPlansRecyclerViewItemLayoutBinding) :
        ViewHolder(binding.root) {
        val trainingPlanName = binding.textViewElement
        val moreButton = binding.moreButton
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

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, trainingPlans[position])
            }
        }

        holder.moreButton.setOnClickListener {
            openMoreDialog(holder, position)
        }

        if(holder.trainingPlanName.text == TrainingPlansFragment.DEFAULT_ELEMENT) {
            holder.moreButton.visibility = View.GONE
        } else{
            holder.moreButton.visibility = View.VISIBLE
        }

    }

    private fun openMoreDialog(holder: TrainingPlansViewHolder, position: Int)
    {
        val bundle = Bundle()
        bundle.putString(SELECTED_ITEM_KEY, holder.trainingPlanName.text.toString())
        bundle.putString(BUTTON_TEXT, "EDIT PLAN'S NAME")
        bundle.putInt(POSITION, position)
        val trainingPlansMoreFragment = TrainingPlansMoreFragment(this)
        trainingPlansMoreFragment.arguments = bundle
        fragment.requireActivity().supportFragmentManager.let { trainingPlansMoreFragment.show(it, "WorkoutMenu")}
    }

    fun deleteSinglePlan(position: Int){
        deletedTrainingPlans.clear()
        deletedTrainingPlans.add(trainingPlans[position].name)
        trainingPlans.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getDeletedPlans() : MutableList<String>
    {
        return deletedTrainingPlans
    }

    fun getElement(position: Int) : TrainingPlan{
        return trainingPlans[position]
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: TrainingPlan)
    }


    companion object{
        const val SELECTED_ITEM_KEY = "SELECTED_ITEM_KEY"
        const val BUTTON_TEXT = "EDIT_BUTTON_TEXT"
        const val POSITION = "POSITION"
    }
}