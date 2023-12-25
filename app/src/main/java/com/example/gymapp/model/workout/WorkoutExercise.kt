package com.example.gymapp.model.workout

import com.example.gymapp.model.routine.Exercise

data class WorkoutExercise(var exercise: Exercise, var actualReps: Float, var exerciseCount: Int, var seriesCount: Int, var note: String?)