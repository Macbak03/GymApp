package com.example.gymapp.adapter

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.animation.Animations
import com.example.gymapp.databinding.CreateRoutineRecyclerViewItemBinding
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
        val wholeItem = binding.wholeExerciseElement
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

        exerciseTitleElement.setOnClickListener{
            if (exerciseDetails.visibility == View.VISIBLE) {
                hideDetails(holder)

            } else if(exerciseDetails.visibility == View.GONE) {
                showDetails(holder)
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
        val targetRotation = expandImage.rotation - 90
        if(currentRotation == 0f) {
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
    }

    private fun rotateExpandButtonLeft(holder: RoutineViewHolder) {
        val expandImage = holder.expandImage
        val currentRotation = expandImage.rotation
        val targetRotation = expandImage.rotation + 90
        if(currentRotation == -90f)
        {
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
    }


    private fun showDetails(holder: RoutineViewHolder) {
        val exerciseDetails = holder.exerciseDetails
        exerciseDetails.visibility = View.VISIBLE
        exerciseDetails.animate()
            .alpha(1f)
            .setDuration(300)
            .withStartAction{
                holder.exerciseTitleElement.isClickable = false
                holder.exerciseName.isClickable = false
                rotateExpandButtonLeft(holder)
                moveItemsDown(holder)
            }
            .withEndAction {
                holder.exerciseTitleElement.isClickable = true
                holder.exerciseName.isClickable = true
            }
            .start()
    }

    fun hideDetails(holder: RoutineViewHolder) {
        val exerciseDetails = holder.exerciseDetails
        exerciseDetails.animate()
            .alpha(0f)
            .setDuration(300)
            .withStartAction {
                holder.exerciseTitleElement.isClickable = false
                holder.exerciseName.isClickable = false
                rotateExpandButtonRight(holder)
                moveItemsUp(holder)
            }
            .withEndAction {
                holder.exerciseTitleElement.isClickable = true
                holder.exerciseName.isClickable = true
                exerciseDetails.visibility = View.GONE
                exerciseDetails.clearAnimation()
            }
            .start()
    }

    private fun moveItemsDown(holder: RoutineViewHolder)
    {
        val wholeItem = holder.wholeItem
        wholeItem.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val initialHeight = holder.exerciseTitleElement.height
        val targetHeight = wholeItem.measuredHeight
       animations.moveItemsY(initialHeight, targetHeight, holder.itemView, 300)
    }

    private fun moveItemsUp(holder: RoutineViewHolder)
    {
        val initialHeight = holder.wholeItem.height
        val targetHeight = holder.exerciseTitleElement.height

       animations.moveItemsY(initialHeight, targetHeight, holder.itemView, 300)
    }


}