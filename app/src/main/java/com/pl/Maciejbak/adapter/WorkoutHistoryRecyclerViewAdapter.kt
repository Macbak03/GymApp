package com.pl.Maciejbak.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.pl.Maciejbak.databinding.WorkoutHistoryRecyclerViewItemBinding
import com.pl.Maciejbak.fragment.TrainingHistoryMoreFragment
import com.pl.Maciejbak.model.workoutHistory.WorkoutHistoryElement

class WorkoutHistoryRecyclerViewAdapter(
    private val workoutHistoryElements: MutableList<WorkoutHistoryElement>,
    private val fragment: Fragment
) :
    RecyclerView.Adapter<WorkoutHistoryRecyclerViewAdapter.WorkoutHistoryViewHolder>() {

    private var onClickListener: OnClickListener? = null

    private var deletedHistoryDate: String = ""

    inner class WorkoutHistoryViewHolder(binding: WorkoutHistoryRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val trainingPlanName = binding.textViewHistoryPlanName
        val routineName = binding.textViewHistoryRoutineName
        val date = binding.textViewHistoryDate
        val moreButton = binding.moreButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val workoutHistoryElementBinding =
            WorkoutHistoryRecyclerViewItemBinding.inflate(inflater, parent, false)
        return WorkoutHistoryViewHolder(workoutHistoryElementBinding)
    }

    override fun getItemCount(): Int {
        return workoutHistoryElements.size
    }

    override fun onBindViewHolder(holder: WorkoutHistoryViewHolder, position: Int) {
        holder.trainingPlanName.text = workoutHistoryElements[position].planName
        holder.routineName.text = workoutHistoryElements[position].routineName
        holder.date.text = workoutHistoryElements[position].formattedDate

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, workoutHistoryElements[position])
            }
        }

        holder.moreButton.setOnClickListener {
            openMoreDialog(position)
        }
    }

    private fun openMoreDialog(position: Int) {
        val bundle = Bundle()
        bundle.putString(SELECTED_ITEM_PLAN_NAME, workoutHistoryElements[position].planName)
        bundle.putString(SELECTED_ITEM_ROUTINE_NAME, workoutHistoryElements[position].routineName)
        bundle.putString(SELECTED_ITEM_FORMATTED_DATE, workoutHistoryElements[position].formattedDate)
        bundle.putString(SELECTED_ITEM_RAW_DATE, workoutHistoryElements[position].rawDate)
        bundle.putInt(POSITION, position)
        val trainingHistoryMoreFragment = TrainingHistoryMoreFragment(this)
        trainingHistoryMoreFragment.arguments = bundle
        fragment.requireActivity().supportFragmentManager.let {
            trainingHistoryMoreFragment.show(
                it,
                "PlansMenu"
            )
        }
    }

    fun deleteSingleHistory(position: Int){
        deletedHistoryDate = workoutHistoryElements[position].rawDate
        workoutHistoryElements.removeAt(position)
        this.notifyItemRemoved(position)
    }

    fun getRemovedHistoryDate(): String{
        return deletedHistoryDate
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: WorkoutHistoryElement)
    }

    companion object {
        const val SELECTED_ITEM_PLAN_NAME = "SELECTED_ITEM_PLAN_NAME"
        const val SELECTED_ITEM_ROUTINE_NAME = "SELECTED_ITEM_ROUTINE_NAME"
        const val SELECTED_ITEM_FORMATTED_DATE = "SELECTED_ITEM_FORMATTED_DATE"
        const val SELECTED_ITEM_RAW_DATE = "SELECTED_ITEM_RAW_DATE"
        const val POSITION = "POSITION"
    }
}