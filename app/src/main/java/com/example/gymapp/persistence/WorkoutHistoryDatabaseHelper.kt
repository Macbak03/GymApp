package com.example.gymapp.persistence

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.model.routine.ExactReps
import com.example.gymapp.model.routine.ExactRpe
import com.example.gymapp.model.routine.RangeReps
import com.example.gymapp.model.routine.RangeRpe
import com.example.gymapp.model.workout.WorkoutExercise

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
                ACTUAL_REPS_COLUMN + " REAL NOT NULL," +
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
        workoutExercise: WorkoutExercise,
        planName: String,
        routineName: String,
    ) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(DATE_COLUMN, date)
        values.put(PLAN_NAME_COLUMN, planName)
        values.put(ROUTINE_NAME_COLUMN, routineName)
        values.put(EXERCISE_ORDER_COLUMN, workoutExercise.exerciseCount)
        values.put(SERIES_ORDER_COLUMN, workoutExercise.seriesCount)
        values.put(EXERCISE_NAME_COLUMN, workoutExercise.exercise.name)
        values.put(PAUSE_COLUMN, workoutExercise.exercise.pause.inWholeSeconds)
        values.put(ACTUAL_REPS_COLUMN, workoutExercise.actualReps)
        values.put(LOAD_VALUE_COLUMN, workoutExercise.exercise.load.weight)
        values.put(LOAD_UNIT_COLUMN, workoutExercise.exercise.load.unit.toString())
        when (workoutExercise.exercise.reps) {
            is ExactReps -> {
                values.put(REPS_RANGE_FROM_COLUMN, (workoutExercise.exercise.reps as ExactReps).value)
                values.put(REPS_RANGE_TO_COLUMN, (workoutExercise.exercise.reps as ExactReps).value)
            }

            is RangeReps -> {
                values.put(REPS_RANGE_FROM_COLUMN, (workoutExercise.exercise.reps as RangeReps).from)
                values.put(REPS_RANGE_TO_COLUMN, (workoutExercise.exercise.reps as RangeReps).to)
            }
        }
        values.put(SERIES_COLUMN, workoutExercise.exercise.series)
        when (workoutExercise.exercise.rpe) {
            is ExactRpe -> {
                values.put(RPE_RANGE_FROM_COLUMN, (workoutExercise.exercise.rpe as ExactRpe).value)
                values.put(RPE_RANGE_TO_COLUMN, (workoutExercise.exercise.rpe as ExactRpe).value)
            }

            is RangeRpe -> {
                values.put(RPE_RANGE_FROM_COLUMN, (workoutExercise.exercise.rpe as RangeRpe).from)
                values.put(RPE_RANGE_TO_COLUMN, (workoutExercise.exercise.rpe as RangeRpe).to)
            }

            null -> {}
        }
        values.put(PACE_COLUMN, workoutExercise.exercise.pace.toString())
        values.put(NOTES_COLUMN, workoutExercise.note)
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
                workoutExercise,
                planName,
                routineName,
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
        const val EXERCISE_ID_COLUMN = "ExerciseID"
        const val DATE_COLUMN = "Date"
        const val PLAN_NAME_COLUMN = "PlanName"
        const val ROUTINE_NAME_COLUMN = "RoutineName"
        const val EXERCISE_ORDER_COLUMN = "ExerciseOrder"
        const val SERIES_ORDER_COLUMN = "SeriesOrder"
        const val EXERCISE_NAME_COLUMN = "ExerciseName"
        const val PAUSE_COLUMN = "Pause"
        const val ACTUAL_REPS_COLUMN = "ActualReps"
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