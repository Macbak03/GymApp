package com.example.gymapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.databinding.CreateRoutineRecyclerViewItemBinding
import com.example.gymapp.model.routine.ExerciseDraft

class RoutineRecyclerViewAdapter(
    private val exercises: MutableList<ExerciseDraft>,
) : RecyclerView.Adapter<RoutineRecyclerViewAdapter.RoutineViewHolder>() {

    inner class RoutineViewHolder(binding: CreateRoutineRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val exerciseName = binding.textViewExerciseName
        val expandButton = binding.buttonExpand
        val exerciseDetails = binding.exerciseDetails
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

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name
        holder.exerciseDetails.setExercise(exercise)
    }
}