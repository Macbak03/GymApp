package com.example.gymapp.model

import com.pl.Maciejbak.exception.ValidationException

import com.pl.Maciejbak.model.routine.Weight
import com.pl.Maciejbak.model.routine.WeightUnit

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test



class WeightUnitTest {

    @Test
    fun `test fromStringWithUnit with valid input`() {
        val weightString = "75"
        val unit = WeightUnit.kg

        val weight = Weight.fromStringWithUnit(weightString, unit, 0)

        assertEquals(weight.weight, 75f)
        assertEquals(weight.unit, unit)
    }

    @Test
    fun `test fromStringWithUnit with invalid input`() {
        val weightString = "-75"
        val unit = WeightUnit.kg

        val exception = assertThrows(ValidationException::class.java) {
            Weight.fromStringWithUnit(weightString, unit, 0)
        }
        assertEquals("weight cannot be negative", exception.message)
    }

    @Test
    fun `test fromStringWithUnit with null input`() {
        val weightString: String? = null
        val unit = WeightUnit.kg

        val exception = assertThrows(ValidationException::class.java) {
            Weight.fromStringWithUnit(weightString, unit, 0)
        }
        assertEquals("weight cannot be empty", exception.message)
    }

    @Test
    fun `test fromStringWithUnit with non-numeric input`() {
        val weightString = "abc"
        val unit = WeightUnit.kg

        val exception = assertThrows(ValidationException::class.java) {
            Weight.fromStringWithUnit(weightString, unit, 0)
        }
        assertEquals("weight must be a number", exception.message)
    }
}
