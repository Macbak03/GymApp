package com.example.gymapp.persistence

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import com.example.gymapp.model.routine.ExactReps
import com.example.gymapp.model.routine.ExactRpe
import com.example.gymapp.model.routine.Exercise
import com.example.gymapp.model.routine.RangeReps
import com.example.gymapp.model.routine.RangeRpe
import com.example.gymapp.model.workout.WorkoutExercise
import java.time.LocalDate
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class WorkoutHistoryDatabaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    Repository(
        context, factory,
    ) {
    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + DATE_COLUMN + " TEXT NOT NULL," +
                PLAN_NAME_COLUMN + " TEXT NOT NULL," +
                ROUTINE_NAME_COLUMN + " TEXT NOT NULL," +
                EXERCISE_ORDER_COLUMN + " INTEGER NOT NULL," +
                SERIES_ORDER_COLUMN + " INTEGER NOT NULL," +
                EXERCISE_NAME_COLUMN + " TEXT NOT NULL," +
                PAUSE_COLUMN + " INTEGER NOT NULL," +
                LOAD_VALUE_COLUMN + " REAL NOT NULL," +
                LOAD_UNIT_COLUMN + " TEXT NOT NULL," +
                REPS_RANGE_FROM_COLUMN + " INTEGER NOT NULL," +
                REPS_RANGE_TO_COLUMN + " INTEGER NOT NULL," +
                SERIES_COLUMN + " INTEGER NOT NULL," +
                RPE_RANGE_FROM_COLUMN + " INTEGER," +
                RPE_RANGE_TO_COLUMN + " INTEGER," +
                PACE_COLUMN + " TEXT," +
                NOTES_COLUMN + " TEXT" +
                ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    private fun addExerciseToHistory(
        date: String,
        exercise: Exercise,
        planName: String,
        routineName: String,
        exerciseCount: Int,
        seriesCount: Int,
        note: String?
    ) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(DATE_COLUMN, date)
        values.put(PLAN_NAME_COLUMN, planName)
        values.put(ROUTINE_NAME_COLUMN, routineName)
        values.put(EXERCISE_ORDER_COLUMN, exerciseCount)
        values.put(SERIES_ORDER_COLUMN, seriesCount)
        values.put(EXERCISE_NAME_COLUMN, exercise.name)
        values.put(PAUSE_COLUMN, exercise.pause.inWholeSeconds)
        values.put(LOAD_VALUE_COLUMN, exercise.load.weight)
        values.put(LOAD_UNIT_COLUMN, exercise.load.unit.toString())
        when (exercise.reps) {
            is ExactReps -> {
                values.put(REPS_RANGE_FROM_COLUMN, exercise.reps.value)
                values.put(REPS_RANGE_TO_COLUMN, exercise.reps.value)
            }

            is RangeReps -> {
                values.put(REPS_RANGE_FROM_COLUMN, exercise.reps.from)
                values.put(REPS_RANGE_TO_COLUMN, exercise.reps.to)
            }
        }
        values.put(SERIES_COLUMN, exercise.series)
        when (exercise.rpe) {
            is ExactRpe -> {
                values.put(RPE_RANGE_FROM_COLUMN, exercise.rpe.value)
                values.put(RPE_RANGE_TO_COLUMN, exercise.rpe.value)
            }

            is RangeRpe -> {
                values.put(RPE_RANGE_FROM_COLUMN, exercise.rpe.from)
                values.put(RPE_RANGE_TO_COLUMN, exercise.rpe.to)
            }

            null -> {}
        }
        values.put(PACE_COLUMN, exercise.pace.toString())
        values.put(NOTES_COLUMN, note)
        db.insert(TABLE_NAME, null, values)
    }

    fun addWorkout(
        workout: ArrayList<WorkoutExercise>,
        date: String,
        planName: String,
        routineName: String
    ) {
        for (workoutExercise in workout) {
            addExerciseToHistory(
                date,
                workoutExercise.exercise,
                planName,
                routineName,
                workoutExercise.exerciseCount,
                workoutExercise.seriesCount,
                workoutExercise.note
            )
        }

    }

    fun getHistory(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT DISTINCT $PLAN_NAME_COLUMN, $DATE_COLUMN, $ROUTINE_NAME_COLUMN FROM $TABLE_NAME ORDER BY $DATE_COLUMN DESC",
            null
        )
    }

    fun getWorkout() {

    }


    companion object {
        const val TABLE_NAME = "workoutHistory"
        const val DATE_COLUMN = "Date"
        const val PLAN_NAME_COLUMN = "PlanName"
        const val ROUTINE_NAME_COLUMN = "RoutineName"
        const val EXERCISE_ORDER_COLUMN = "ExerciseOrder"
        const val SERIES_ORDER_COLUMN = "SeriesOrder"
        const val EXERCISE_NAME_COLUMN = "ExerciseName"
        const val PAUSE_COLUMN = "Pause"
        const val LOAD_VALUE_COLUMN = "LoadValue"
        const val LOAD_UNIT_COLUMN = "LoadUnit"
        const val REPS_RANGE_FROM_COLUMN = "RepsRangeFrom"
        const val REPS_RANGE_TO_COLUMN = "RepsRangeTo"
        const val SERIES_COLUMN = "Series"
        const val RPE_RANGE_FROM_COLUMN = "RPERangeFrom"
        const val RPE_RANGE_TO_COLUMN = "RPERangeTo"
        const val PACE_COLUMN = "Pace"
        const val NOTES_COLUMN = "Notes"
    }
}