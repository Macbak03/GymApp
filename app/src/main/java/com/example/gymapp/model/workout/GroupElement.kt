package com.example.gymapp.model.workout

import com.example.gymapp.model.routine.TimeUnit

class GroupElement(
    var exerciseName: String?,
    var pause: String?,
    var pauseUnit: TimeUnit,
    var reps: String?,
    var series: String?,
    var rpe: String?,
    var pace: String?
) {
}