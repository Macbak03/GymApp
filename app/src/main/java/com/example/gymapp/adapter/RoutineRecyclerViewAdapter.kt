package com.example.gymapp.adapter

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.animation.Animations
import com.example.gymapp.databinding.CreateRoutineRecyclerViewItemBinding
import com.example.gymapp.model.routine.ExerciseDraft

class RoutineRecyclerViewAdapter(
    private val exercises: MutableList<ExerciseDraft>,
    private val touchHelper: ItemTouchHelper,
    private val context: Context,
    private val layoutInflater: LayoutInflater
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
        val pauseDescription = exerciseDetails.getPauseDescription()
        val loadDescription = exerciseDetails.getLoadDescription()
        val repsDescription = exerciseDetails.getRepsDescription()
        val seriesDescription = exerciseDetails.getSeriesDescription()
        val rpeDescription = exerciseDetails.getRpeDescription()
        val paceDescription = exerciseDetails.getPaceDescription()
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


        holder.pauseDescription.setOnClickListener{
            showDescriptionDialog("Rest", "A period of time between sets, " +
                    "allowing the muscles to recover partially before the next set.")
        }

        holder.loadDescription.setOnClickListener{
            showDescriptionDialog("Load", "The amount of weight lifted during an exercise, " +
                    "measured in kilograms or pounds. Value typed here is for reference during the workout.")
        }

        holder.repsDescription.setOnClickListener{
            showDescriptionDialog("Reps", "Short for repetitions, reps refer to the number of times " +
                    "a particular exercise is performed in a set. " +
                    "For example 10 reps of bench press means you can perform bench press for 10 reps in 1 set.")
        }

        holder.seriesDescription.setOnClickListener{
            showDescriptionDialog("Series", "Also known as sets, " +
                    "a series refers to a group of consecutive repetitions of an exercise. " +
                    "For example 3 sets of bench press means you can perform bench press for similar " +
                    "number of times 3 times in a row with a rest between.")
        }

        holder.rpeDescription.setOnClickListener{
            showDescriptionDialog("RPE", "It's a subjective measure used to gauge the intensity of " +
                    "exercise based on how difficult it feels. " +
                    "RPE typically ranges from 1 to 10, with 1 being very easy and 10 being maximal exertion.")
        }

        holder.paceDescription.setOnClickListener{
            showDescriptionDialog("Pace", "Pace refers to the speed at which an exercise or workout is performed. " +
                    "It can vary depending on the type of exercise and the goals of the individual. " +
                    "Maintaining a consistent pace can help regulate intensity and improve performance. \n" +
                    "First number is eccentric phase - muscle stretching phase. \n" +
                    "Second number is pause after eccentric phase. \n" +
                    "Third number is concentric phase - the muscle shortens as it contracts. \n" +
                    "The fourth number is pause after concentric phase. \n" +
                    "Everything is measured in seconds, \"x\" means as fast as you can \n" +
                    "For example pace 21x0 in bench press means you go down for 2 seconds, 1 second pause " +
                    "at the bottom, push as fast as you can to the top and immediately start to go down again.")
        }

    }

    private fun showDescriptionDialog(title: String, description: String)
    {
        val builder = context.let { AlertDialog.Builder(it) }
        val dialogLayout = layoutInflater.inflate(R.layout.description_text_view, null)
        val descriptionTextView = dialogLayout.findViewById<TextView>(R.id.textViewDescription)
        descriptionTextView.text = description
        with(builder) {
            this.setTitle(title)
            this.setPositiveButton("OK") { _, _ -> }
            this.setView(dialogLayout)
            this.show()
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