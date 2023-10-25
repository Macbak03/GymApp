package com.example.gymapp.model

import com.example.gymapp.exception.ValidationException

data class Weight private constructor(
    val weight: Float,
    val unit: WeightUnit
) {

    override fun toString(): String {
        return weight.toString() + unit.toString()
    }
    companion object {
        operator fun invoke(weight: Float, unit: WeightUnit): Weight? {
            return if (weight < 0) {
                null
            } else {
                Weight(weight, unit)
            }
        }

        fun fromStringWithUnit(weight: String?, unit: WeightUnit) : Weight
        {
            if(weight == null)
            {
                throw ValidationException("weight cannot be empty")
            }
            val floatWeight = weight.toFloat()
            if(floatWeight < 0)
            {
                throw ValidationException("weight cannot be negative")
            }
            return Weight(floatWeight, unit)
        }

/*        fun Float.kg(): Weight? {
            return Weight.invoke(this, WeightUnit.kg)
        }

        fun Int.kg(): Weight? {
            return this.toFloat().kg()
        }


        val x = 10.kg()*/
    }
}
