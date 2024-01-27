package com.example.gymapp.adapter

import android.animation.Animator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.animation.Animations
import com.example.gymapp.databinding.CreateRoutineRecyclerViewItemBinding
import com.example.gymapp.layout.RoutineExpandableLayout
import com.example.gymapp.model.routine.ExerciseDraft

class RoutineRecyclerViewAdapter(
    private val exercises: MutableList<ExerciseDraft>,
    private val touchHelper: ItemTouchHelper
) : RecyclerView.Adapter<RoutineRecyclerViewAdapter.RoutineViewHolder>() {

    private val animations = Animations()

    inner class RoutineViewHolder(binding: CreateRoutineRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val exerciseName = binding.editTextExerciseName
        val expandImage = binding.buttonExpand
        val exerciseDetails = binding.exerciseDetails
        val exerciseTitleElement = binding.linearLayoutExerciseTitleElement
        val moveButton = binding.imageButtonMove
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val routineItemBinding =
            CreateRoutineRecyclerViewItemBinding.inflate(inflater, parent, false)

        return RoutineViewHolder(routineItemBinding)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val exercise = exercises[position]
        val exerciseName = holder.exerciseName
        val exerciseTitleElement = holder.exerciseTitleElement
        val exerciseDetails = holder.exerciseDetails
        val moveButton = holder.moveButton
        exerciseName.setText(exercise.name)
        exerciseDetails.setExercise(exercise)

        //exerciseDetails.setAdapter(this)
/*        exerciseDetails.setExerciseTextChangedListener(object: RoutineExpandableLayout.ExerciseTextChangedListener{
            override fun onExerciseNameChanged(name: String?) {
                exerciseName.text = name
            }

        })*/

        exerciseTitleElement.setOnClickListener{
            if (exerciseDetails.visibility == View.VISIBLE) {
                hideDetails(holder)
                rotateExpandButtonRight(holder)

            } else if(exerciseDetails.visibility == View.GONE) {
                showDetails(holder)
                rotateExpandButtonLeft(holder)
            }
        }

        moveButton.setOnTouchListener { _, event ->
            if(event.actionMasked == MotionEvent.ACTION_DOWN || event.actionMasked == MotionEvent.ACTION_UP){
                touchHelper.startDrag(holder)
            }
            false
        }

    }

    private fun rotateExpandButtonRight(holder: RoutineViewHolder) {
        val expandImage = holder.expandImage
        val currentRotation = expandImage.rotation
        val targetRotation = expandImage.rotation + - 90
        val animator = animations.rotate(currentRotation, targetRotation, expandImage, 200)

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                animator.removeAllUpdateListeners()

            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })

        animator.start()
    }

    private fun rotateExpandButtonLeft(holder: RoutineViewHolder) {
        val expandImage = holder.expandImage
        val currentRotation = expandImage.rotation
        val targetRotation = expandImage.rotation + 90
        val animator = animations.rotate(currentRotation, targetRotation, expandImage, 200)

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {

                animator.removeAllUpdateListeners()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })

        animator.start()
    }


    private fun showDetails(holder: RoutineViewHolder) {
        val exerciseDetails = holder.exerciseDetails
        exerciseDetails.visibility = View.VISIBLE
        exerciseDetails.animate()
            .alpha(1f)
            .setDuration(400)
            .withStartAction{
                holder.exerciseTitleElement.isClickable = false
                holder.exerciseName.isClickable = false
            }
            .withEndAction {
                holder.exerciseTitleElement.isClickable = true
                holder.exerciseName.isClickable = true
            }
            .start()
    }

    private fun hideDetails(holder: RoutineViewHolder) {
        val exerciseDetails = holder.exerciseDetails
        exerciseDetails.animate()
            .alpha(0f)
            .setDuration(400)
            .withStartAction {
                holder.exerciseTitleElement.isClickable = false
                holder.exerciseName.isClickable = false
            }
            .withEndAction {
                holder.exerciseTitleElement.isClickable = true
                holder.exerciseName.isClickable = true
                exerciseDetails.visibility = View.GONE
                exerciseDetails.clearAnimation()
            }
            .start()
    }

}