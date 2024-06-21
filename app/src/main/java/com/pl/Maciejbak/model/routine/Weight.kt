package com.pl.Maciejbak.model.routine

import com.pl.Maciejbak.exception.ValidationException

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

        fun fromStringWithUnit(weight: String?, unit: WeightUnit, viewId: Int) : Weight
        {
            if(weight.isNullOrBlank())
            {
                throw ValidationException("weight cannot be empty", viewId)
            }
            val floatWeight = weight.toFloatOrNull() ?: throw ValidationException("weight must be a number", viewId)
            if(floatWeight < 0)
            {
                throw ValidationException("weight cannot be negative", viewId)
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
