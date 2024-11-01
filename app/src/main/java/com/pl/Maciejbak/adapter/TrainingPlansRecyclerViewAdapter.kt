package com.pl.Maciejbak.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pl.Maciejbak.databinding.TrainingPlansRecyclerViewItemLayoutBinding
import com.pl.Maciejbak.fragment.TrainingPlansFragment
import com.pl.Maciejbak.fragment.TrainingPlansMoreFragment
import com.pl.Maciejbak.model.trainingPlans.TrainingPlan

class TrainingPlansRecyclerViewAdapter(private val trainingPlans: MutableList<TrainingPlan>, private val fragment: Fragment) :
    RecyclerView.Adapter<TrainingPlansRecyclerViewAdapter.TrainingPlansViewHolder>() {

    private var onClickListener: OnClickListener? = null

    private var deletedTrainingPlan: String = ""

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
            openMoreDialog(position)
        }

        if(holder.trainingPlanName.text == TrainingPlansFragment.DEFAULT_ELEMENT) {
            holder.moreButton.visibility = View.GONE
        } else{
            holder.moreButton.visibility = View.VISIBLE
        }

    }

    private fun openMoreDialog(position: Int)
    {
        val bundle = Bundle()
        bundle.putString(SELECTED_ITEM_KEY, trainingPlans[position].name)
        bundle.putString(BUTTON_TEXT, "EDIT PLAN'S NAME")
        bundle.putInt(POSITION, position)
        val trainingPlansMoreFragment = TrainingPlansMoreFragment(this)
        trainingPlansMoreFragment.arguments = bundle
        fragment.requireActivity().supportFragmentManager.let { trainingPlansMoreFragment.show(it, "PlansMenu")}
    }

    fun deleteSinglePlan(position: Int){
        deletedTrainingPlan = trainingPlans[position].name
        trainingPlans.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, trainingPlans.size)
    }

    fun getDeletedPlans() : String
    {
        return deletedTrainingPlan
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