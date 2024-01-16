package com.example.gymapp.model.trainingPlans

class TrainingPlan(val name: String, var isSelected: Boolean = false) {
    override fun toString(): String {
        return name
    }
}