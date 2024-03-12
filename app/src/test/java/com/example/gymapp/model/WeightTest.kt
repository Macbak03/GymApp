package com.example.gymapp.model

import com.example.gymapp.exception.ValidationException

import com.example.gymapp.model.routine.Weight
import com.example.gymapp.model.routine.WeightUnit

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test



class WeightUnitTest {

    @Test
    fun `test fromStringWithUnit with valid input`() {
        val weightString = "75"
        val unit = WeightUnit.kg

        val weight = Weight.fromStringWithUnit(weightString, unit)

        assertEquals(weight.weight, 75f)
        assertEquals(weight.unit, unit)
    }

    @Test
    fun `test fromStringWithUnit with invalid input`() {
        val weightString = "-75"
        val unit = WeightUnit.kg

        val exception = assertThrows(ValidationException::class.java) {
            Weight.fromStringWithUnit(weightString, unit)
        }
        assertEquals("weight cannot be negative", exception.message)
    }

    @Test
    fun `test fromStringWithUnit with null input`() {
        val weightString: String? = null
        val unit = WeightUnit.kg

        val exception = assertThrows(ValidationException::class.java) {
            Weight.fromStringWithUnit(weightString, unit)
        }
        assertEquals("weight cannot be empty", exception.message)
    }

    @Test
    fun `test fromStringWithUnit with non-numeric input`() {
        val weightString = "abc"
        val unit = WeightUnit.kg

        val exception = assertThrows(ValidationException::class.java) {
            Weight.fromStringWithUnit(weightString, unit)
        }
        assertEquals("weight must be a number", exception.message)
    }
}
