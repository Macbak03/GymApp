package com.example.gymapp.model

data class Weight constructor(
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

/*        fun Float.kg(): Weight? {
            return Weight.invoke(this, WeightUnit.kg)
        }

        fun Int.kg(): Weight? {
            return this.toFloat().kg()
        }


        val x = 10.kg()*/
    }
}
