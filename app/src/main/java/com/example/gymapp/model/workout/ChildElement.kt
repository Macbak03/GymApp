package com.example.gymapp.model.workout

import com.example.gymapp.model.routine.WeightUnit

class ChildElement (
    var reps: String?,
    var load: String?,
    var loadUnit: WeightUnit,
    var note: String?,
    var wasModified: Boolean
){
}