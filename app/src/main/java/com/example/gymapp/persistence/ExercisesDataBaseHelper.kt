package com.example.gymapp.persistence

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.model.routine.ExactReps
import com.example.gymapp.model.routine.ExactRpe
import com.example.gymapp.model.routine.Exercise
import com.example.gymapp.model.routine.RangeReps
import com.example.gymapp.model.routine.RangeRpe

class ExercisesDataBaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    Repository(context, factory) {
    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + PLAN_ID_COLUMN + " INTEGER NOT NULL," +
                ROUTINE_ID_COLUMN + " INTEGER NOT NULL," +
                ROUTINE_NAME_COLUMN + " TEXT NOT NULL," +
                EXERCISE_ORDER_COLUMN + " INTEGER NOT NULL," +
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
                "FOREIGN KEY " + "(" + ROUTINE_ID_COLUMN + ")" + " REFERENCES " + RoutinesDataBaseHelper.TABLE_NAME + "(" + RoutinesDataBaseHelper.ROUTINE_ID_COLUMN + ")"
                + "ON UPDATE CASCADE ON DELETE CASCADE," +
                "FOREIGN KEY " + "(" + PLAN_ID_COLUMN + ")" + " REFERENCES " + PlansDataBaseHelper.TABLE_NAME + "(" + PlansDataBaseHelper.PLAN_ID_COLUMN + ")"
                + "ON UPDATE CASCADE ON DELETE CASCADE" + ")")
        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    // This method is for adding data in our database
    fun addExercise(exercise: Exercise, routineName: String, planId: Int, routineId: Int, exerciseOrder: Int, ) {
        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        values.put(PLAN_ID_COLUMN, planId)
        values.put(ROUTINE_ID_COLUMN, routineId)
        values.put(ROUTINE_NAME_COLUMN, routineName)
        values.put(EXERCISE_ORDER_COLUMN, exerciseOrder)
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

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase
        db.use {
            db.insert(TABLE_NAME, null, values)
        }

        // all values are inserted into database
        // at last we are
        // closing our database
    }

    // below method is to get
    // all data from our database

    fun getRoutine(routineName: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $ROUTINE_NAME_COLUMN = '$routineName' ORDER BY $EXERCISE_ORDER_COLUMN",
            null
        )
    }

    fun doesIdExist(idToCheck: Int): Boolean {
        val db = this.readableDatabase
        val selectionArgs = arrayOf(idToCheck.toString())

        val cursor =
            db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME WHERE $ROUTINE_ID_COLUMN = ?", selectionArgs)

        var idExists = false

        try {
            if (cursor.moveToFirst()) {
                val count = cursor.getInt(0)
                idExists = count > 0
            }
        } finally {
            cursor.close()
            db.close()
        }

        return idExists
    }

    fun deleteRoutine(planId: Int, routineId: Int, originalRoutineName: String?) {
        val db = this.writableDatabase
        val deleteSelection = "$PLAN_ID_COLUMN = ? $ROUTINE_ID_COLUMN = ? AND $ROUTINE_NAME_COLUMN = ?"
        val deleteSelectionArgs = arrayOf(planId.toString(), routineId.toString(), originalRoutineName)

        val cursor =
            db.query(TABLE_NAME, null, deleteSelection, deleteSelectionArgs, null, null, null)
        try {
            if (cursor.moveToFirst()) {
                db.delete(TABLE_NAME, deleteSelection, deleteSelectionArgs)
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    companion object {

        const val TABLE_NAME = "exercises"
        const val PLAN_ID_COLUMN = "PlanID"
        const val ROUTINE_ID_COLUMN = "RoutineID"
        const val ROUTINE_NAME_COLUMN = "RoutineName"
        const val EXERCISE_ORDER_COLUMN = "ExerciseOrder"
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
    }
}