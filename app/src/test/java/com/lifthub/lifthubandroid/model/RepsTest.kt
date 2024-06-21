package com.lifthub.lifthubandroid.model

import com.lifthub.lifthubandroid.exception.ValidationException
import com.lifthub.lifthubandroid.model.routine.ExactReps
import com.lifthub.lifthubandroid.model.routine.RangeReps
import com.lifthub.lifthubandroid.model.routine.Reps

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class RepsTest {
    @ParameterizedTest
    @MethodSource("exactRepsTestCases")
    fun `fromString should return ExactReps when given a single number`(
        input: String,
        expected: ExactReps
    ) {
        //When
        val result = Reps.fromString(input)
        //Then
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @MethodSource("rangeRepsTestCases")
    fun `fromString should return RangeReps when given a range of numbers`(
        input: String,
        expected: RangeReps
    ) {
        //When
        val result = Reps.fromString(input)
        //Then
        assertEquals(expected, result)
    }


    @ParameterizedTest
    @MethodSource("validationTestCases")
    fun `fromString should fail when given invalid input`(input: String, errorMessage: String) {
        //When
        val exception = assertThrows(ValidationException::class.java)
        {
            Reps.fromString(input)
        }
        //Then
        assertEquals(errorMessage, exception.message)
    }

    companion object {
        @JvmStatic
        fun exactRepsTestCases() = listOf(
            Arguments.of("5", ExactReps(5)),
            Arguments.of("23", ExactReps(23))
        )

        @JvmStatic
        fun rangeRepsTestCases() =
            listOf(Arguments.of("1-5", RangeReps(1, 5)), Arguments.of("20-25", RangeReps(20, 25)))

        @JvmStatic
        fun validationTestCases() = listOf(
            Arguments.of("gae", "reps must be a number (eg. 5) or range (eg. 3-5) and cannot be negative"),
            Arguments.of("124awffw", "reps must be a number (eg. 5) or range (eg. 3-5) and cannot be negative"),
            Arguments.of("-5", "reps must be a number (eg. 5) or range (eg. 3-5) and cannot be negative"),
            Arguments.of("12-8", "first number of the range must be lower than the second number")
        )
    }
}