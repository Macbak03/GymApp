package com.example.gymapp.adapter

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.gymapp.databinding.TrainingPlansRecyclerViewItemLayoutBinding
import com.example.gymapp.model.trainingPlans.TrainingPlan

class TrainingPlansRecyclerViewAdapter(private val trainingPlans: MutableList<TrainingPlan>) :
    RecyclerView.Adapter<TrainingPlansRecyclerViewAdapter.TrainingPlansViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var onLongCLickListener: OnLongClickListener? = null

    private lateinit var textAnimator: ValueAnimator

    var isLongClickActivated = false

    inner class TrainingPlansViewHolder(binding: TrainingPlansRecyclerViewItemLayoutBinding) :
        ViewHolder(binding.root) {
        val trainingPlanName = binding.textViewElement
        val checkBox = binding.checkBoxTrainingPlans
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingPlansViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val trainingPlansItemBinding =
            TrainingPlansRecyclerViewItemLayoutBinding.inflate(inflater, parent, false)
        textAnimator = ValueAnimator()
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

        if (isLongClickActivated)
        {
            showCheckBox(holder)
        }
        else
        {
            hideCheckBox(holder)
        }
    }

    private fun showCheckBox(holder: TrainingPlansViewHolder){
        holder.checkBox.visibility = View.VISIBLE
        holder.checkBox.alpha = 0f
        holder.checkBox.animate()
            .alpha(1f)
            .setDuration(300)
            .withStartAction{
                translateTextViewRight(holder)
            }
            .withEndAction(null)
            .start()
    }

    private fun hideCheckBox(holder: TrainingPlansViewHolder){
        holder.checkBox.animate()
            .alpha(0f)
            .setDuration(300)
            .withStartAction{
                translateTextViewLeft(holder)
            }
            .withEndAction {
                holder.checkBox.visibility = View.GONE
                holder.checkBox.clearAnimation()
            }
            .start()
    }

    private fun translateTextViewRight(holder: TrainingPlansViewHolder){
        val currentTranslationX = holder.trainingPlanName.translationX
        val targetTranslationX = holder.checkBox.width.toFloat()

        textAnimator.setFloatValues(currentTranslationX, targetTranslationX)
        textAnimator.duration = 300
        textAnimator.start()

        textAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            holder.trainingPlanName.translationX = value
        }

        textAnimator.start()
    }

    private fun translateTextViewLeft(holder: TrainingPlansViewHolder){
        val currentTranslationX = holder.trainingPlanName.translationX
        val targetTranslationX =  0f
        val animator = ValueAnimator.ofFloat( currentTranslationX, targetTranslationX)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            holder.trainingPlanName.translationX = value
        }
        animator.duration = 300
        animator.start()
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
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onClick(position: Int, model: TrainingPlan)
    }

    interface OnLongClickListener {
        fun onLongClick(position: Int, model: TrainingPlan)
    }
}