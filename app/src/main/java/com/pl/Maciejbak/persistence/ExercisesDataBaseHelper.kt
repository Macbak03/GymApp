package com.pl.Maciejbak.persistence

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.pl.Maciejbak.model.routine.ExactPause
import com.pl.Maciejbak.model.routine.ExactReps
import com.pl.Maciejbak.model.routine.ExactIntensity
import com.pl.Maciejbak.model.routine.Exercise
import com.pl.Maciejbak.model.routine.ExerciseDraft
import com.pl.Maciejbak.model.routine.IntensityIndex
import com.pl.Maciejbak.model.routine.RangePause
import com.pl.Maciejbak.model.routine.RangeReps
import com.pl.Maciejbak.model.routine.RangeIntensity
import com.pl.Maciejbak.model.routine.TimeUnit
import com.pl.Maciejbak.model.routine.WeightUnit

class ExercisesDataBaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    Repository(context, factory) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + PLAN_ID_COLUMN + " INTEGER NOT NULL," +
                ROUTINE_ID_COLUMN + " INTEGER NOT NULL," +
                ROUTINE_NAME_COLUMN + " TEXT NOT NULL," +
                EXERCISE_ORDER_COLUMN + " INTEGER NOT NULL," +
                EXERCISE_NAME_COLUMN + " TEXT NOT NULL," +
                PAUSE_RANGE_FROM_COLUMN + " INTEGER NOT NULL," +
                PAUSE_RANGE_TO_COLUMN + " INTEGER NOT NULL," +
                LOAD_VALUE_COLUMN + " REAL NOT NULL," +
                LOAD_UNIT_COLUMN + " TEXT NOT NULL," +
                REPS_RANGE_FROM_COLUMN + " INTEGER NOT NULL," +
                REPS_RANGE_TO_COLUMN + " INTEGER NOT NULL," +
                SERIES_COLUMN + " INTEGER NOT NULL," +
                INTENSITY_RANGE_FROM_COLUMN + " INTEGER," +
                INTENSITY_RANGE_TO_COLUMN + " INTEGER," +
                INTENSITY_INDEX_COLUMN + " TEXT NOT NULL," +
                PACE_COLUMN + " TEXT," +
                "FOREIGN KEY " + "(" + ROUTINE_ID_COLUMN + ")" + " REFERENCES " + RoutinesDataBaseHelper.TABLE_NAME + "(" + RoutinesDataBaseHelper.ROUTINE_ID_COLUMN + ")"
                + "ON UPDATE CASCADE ON DELETE CASCADE," +
                "FOREIGN KEY " + "(" + PLAN_ID_COLUMN + ")" + " REFERENCES " + PlansDataBaseHelper.TABLE_NAME + "(" + PlansDataBaseHelper.PLAN_ID_COLUMN + ")"
                + "ON UPDATE CASCADE ON DELETE CASCADE" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        if (oldVersion < 34) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $INTENSITY_INDEX_COLUMN TEXT")
            db.execSQL("ALTER TABLE $TABLE_NAME RENAME COLUMN RPERangeFrom TO $INTENSITY_RANGE_FROM_COLUMN")
            db.execSQL("ALTER TABLE $TABLE_NAME RENAME COLUMN RPERangeTo TO $INTENSITY_RANGE_TO_COLUMN")
        }
    }


    private fun addExercise(
        exercise: Exercise,
        routineName: String,
        planId: Int,
        routineId: Int,
        exerciseCount: Int
    ) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(PLAN_ID_COLUMN, planId)
        values.put(ROUTINE_ID_COLUMN, routineId)
        values.put(ROUTINE_NAME_COLUMN, routineName)
        values.put(EXERCISE_ORDER_COLUMN, exerciseCount)
        values.put(EXERCISE_NAME_COLUMN, exercise.name)
        when (exercise.pause) {
            is ExactPause -> {
                values.put(PAUSE_RANGE_FROM_COLUMN, exercise.pause.value)
                values.put(PAUSE_RANGE_TO_COLUMN, exercise.pause.value)
            }

            is RangePause -> {
                values.put(PAUSE_RANGE_FROM_COLUMN, exercise.pause.from)
                values.put(PAUSE_RANGE_TO_COLUMN, exercise.pause.to)
            }
        }
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
        when (exercise.intensity) {
            is ExactIntensity -> {
                values.put(INTENSITY_RANGE_FROM_COLUMN, exercise.intensity.value)
                values.put(INTENSITY_RANGE_TO_COLUMN, exercise.intensity.value)
                values.put(INTENSITY_INDEX_COLUMN, exercise.intensity.index.toString())
            }

            is RangeIntensity -> {
                values.put(INTENSITY_RANGE_FROM_COLUMN, exercise.intensity.from)
                values.put(INTENSITY_RANGE_TO_COLUMN, exercise.intensity.to)
                values.put(INTENSITY_INDEX_COLUMN, exercise.intensity.index.toString())
            }

            null -> {}
        }
        values.put(PACE_COLUMN, exercise.pace.toString())
        db.insert(TABLE_NAME, null, values)
    }

    fun addRoutine(
        routine: ArrayList<Exercise>,
        routineName: String,
        planId: Int,
        originalRoutineName: String?
    ) {
        val db = this.writableDatabase
        db.transaction {
            if (originalRoutineName == null) {
                addToRoutines(routineName, planId)
                val routineId = getRoutineId(routineName, planId)
                if (routineId != null) {
                    var exerciseCount = 1
                    for (exercise in routine) {
                        addExercise(exercise, routineName, planId, routineId, exerciseCount)
                        exerciseCount++
                    }
                }
            } else {
                val routineId = getRoutineId(originalRoutineName, planId)
                if (routineId != null) {
                    updateRoutine(planId, routineId, originalRoutineName, routineName)
                    deleteRoutine(planId, routineId, originalRoutineName)
                    var exerciseCount = 1
                    for (exercise in routine) {
                        addExercise(exercise, routineName, planId, routineId, exerciseCount)
                        exerciseCount++
                    }
                }
            }
        }
    }

    private fun addToRoutines(routineName: String, planId: Int) {
        val values = ContentValues()
        values.put(RoutinesDataBaseHelper.PLAN_ID_COLUMN, planId)
        values.put(RoutinesDataBaseHelper.ROUTINE_NAME_COLUMN, routineName)

        val db = this.writableDatabase

        db.insert(RoutinesDataBaseHelper.TABLE_NAME, null, values)

    }

    private fun getRoutineId(routineName: String, planId: Int): Int? {
        val db = this.writableDatabase
        val cursor =
            db.rawQuery(
                "SELECT DISTINCT ${RoutinesDataBaseHelper.ROUTINE_ID_COLUMN} FROM ${RoutinesDataBaseHelper.TABLE_NAME} WHERE ${RoutinesDataBaseHelper.ROUTINE_NAME_COLUMN} = '$routineName' AND ${RoutinesDataBaseHelper.PLAN_ID_COLUMN} = '$planId'",
                null
            )
        return cursor.use {
            if (it.moveToFirst()) {
                it.getInt(it.getColumnIndexOrThrow(RoutinesDataBaseHelper.ROUTINE_ID_COLUMN))
            } else {
                null
            }
        }
    }

    private fun updateRoutine(
        planId: Int,
        routineId: Int,
        originalRoutineName: String,
        routineName: String
    ) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(RoutinesDataBaseHelper.ROUTINE_NAME_COLUMN, routineName)

        val whereClause =
            "${RoutinesDataBaseHelper.PLAN_ID_COLUMN} = ? AND ${RoutinesDataBaseHelper.ROUTINE_ID_COLUMN} = ? AND ${RoutinesDataBaseHelper.ROUTINE_NAME_COLUMN} = ?"
        val whereArguments = arrayOf(planId.toString(), routineId.toString(), originalRoutineName)
        db.update(RoutinesDataBaseHelper.TABLE_NAME, contentValues, whereClause, whereArguments)
    }

    private inline fun SQLiteDatabase.transaction(func: SQLiteDatabase.() -> Unit) {
        beginTransaction()
        try {
            func()
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }

    private fun getRoutineCursor(routineName: String, planId: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $ROUTINE_NAME_COLUMN = '$routineName' AND $PLAN_ID_COLUMN = '$planId' ORDER BY $EXERCISE_ORDER_COLUMN",
            null
        )
    }

    fun getRoutine(routineName: String, planId: String): MutableList<ExerciseDraft> {
        val exercises: MutableList<ExerciseDraft> = ArrayList()
        val cursor = getRoutineCursor(routineName, planId)
        val seconds = 60
        var count: Long = 0
        while (cursor.moveToNext()) {
            val exerciseName =
                cursor.getString(cursor.getColumnIndexOrThrow(EXERCISE_NAME_COLUMN))

            var pauseRangeFromInt =
                cursor.getInt(cursor.getColumnIndexOrThrow(PAUSE_RANGE_FROM_COLUMN))
            var pauseRangeToInt =
                cursor.getInt(cursor.getColumnIndexOrThrow(PAUSE_RANGE_TO_COLUMN))
            val pauseUnit: TimeUnit
            if ((pauseRangeFromInt % seconds) == 0 && (pauseRangeToInt % seconds) == 0) {
                pauseRangeFromInt /= seconds
                pauseRangeToInt /= seconds
                pauseUnit = TimeUnit.min
            } else {
                pauseUnit = TimeUnit.s
            }
            val pause: String = if (pauseRangeFromInt == pauseRangeToInt) {
                ExactPause(pauseRangeFromInt, pauseUnit).toString()
            } else {
                RangePause(pauseRangeFromInt, pauseRangeToInt, pauseUnit).toString()
            }

            val loadValue =
                cursor.getString(cursor.getColumnIndexOrThrow(LOAD_VALUE_COLUMN))

            val loadUnit =
                cursor.getString(cursor.getColumnIndexOrThrow(LOAD_UNIT_COLUMN))

            val repsRangeFrom =
                cursor.getInt(cursor.getColumnIndexOrThrow(REPS_RANGE_FROM_COLUMN))
            val repsRangeTo =
                cursor.getInt(cursor.getColumnIndexOrThrow(REPS_RANGE_TO_COLUMN))
            val reps: String = if (repsRangeFrom == repsRangeTo) {
                ExactReps(repsRangeFrom).toString()
            } else {
                RangeReps(repsRangeFrom, repsRangeTo).toString()
            }

            val series =
                cursor.getString(cursor.getColumnIndexOrThrow(SERIES_COLUMN))

            val intensityIndex =
                cursor.getString(cursor.getColumnIndexOrThrow(INTENSITY_INDEX_COLUMN))
            val intensityRangeFrom =
                cursor.getInt(cursor.getColumnIndexOrThrow(INTENSITY_RANGE_FROM_COLUMN))
            val intensityRangeTo =
                cursor.getInt(cursor.getColumnIndexOrThrow(INTENSITY_RANGE_TO_COLUMN))
            val intensity: String = if (intensityRangeFrom == intensityRangeTo) {
                ExactIntensity(
                    intensityRangeFrom,
                    IntensityIndex.valueOf(intensityIndex)
                ).toString()
            } else {
                RangeIntensity(
                    intensityRangeFrom,
                    intensityRangeTo,
                    IntensityIndex.valueOf(intensityIndex)
                ).toString()
            }

            val pace =
                cursor.getString(cursor.getColumnIndexOrThrow(PACE_COLUMN))

            val exercise = ExerciseDraft(
                ++count,
                exerciseName,
                pause,
                pauseUnit,
                loadValue,
                WeightUnit.valueOf(loadUnit),
                series,
                reps,
                intensity,
                IntensityIndex.valueOf(intensityIndex),
                pace,
                false
            )
            exercises.add(exercise)

        }
        return exercises
    }

    private fun deleteRoutine(planId: Int, routineId: Int, originalRoutineName: String?) {
        val dataBaseWrite = this.writableDatabase
        val deleteSelection =
            "$PLAN_ID_COLUMN = ? AND $ROUTINE_ID_COLUMN = ? AND $ROUTINE_NAME_COLUMN = ?"
        val deleteSelectionArgs =
            arrayOf(planId.toString(), routineId.toString(), originalRoutineName)

        val cursor =
            dataBaseWrite.query(
                TABLE_NAME,
                null,
                deleteSelection,
                deleteSelectionArgs,
                null,
                null,
                null
            )
        cursor.use { cur ->
            if (cur.moveToFirst()) {
                dataBaseWrite.delete(TABLE_NAME, deleteSelection, deleteSelectionArgs)
            }
        }
    }

    companion object {

        const val TABLE_NAME = "exercises"
        const val PLAN_ID_COLUMN = "PlanID"
        const val ROUTINE_ID_COLUMN = "RoutineID"
        const val ROUTINE_NAME_COLUMN = "RoutineName"
        const val EXERCISE_ORDER_COLUMN = "ExerciseOrder"
        const val EXERCISE_NAME_COLUMN = "ExerciseName"
        const val PAUSE_RANGE_FROM_COLUMN = "PauseRangeFrom"
        const val PAUSE_RANGE_TO_COLUMN = "PauseRangeTo"
        const val LOAD_VALUE_COLUMN = "LoadValue"
        const val LOAD_UNIT_COLUMN = "LoadUnit"
        const val REPS_RANGE_FROM_COLUMN = "RepsRangeFrom"
        const val REPS_RANGE_TO_COLUMN = "RepsRangeTo"
        const val SERIES_COLUMN = "Series"
        const val INTENSITY_RANGE_FROM_COLUMN = "RPERangeFrom"
        const val INTENSITY_RANGE_TO_COLUMN = "RPERangeTo"
        const val INTENSITY_INDEX_COLUMN = "IntensityIndex"
        const val PACE_COLUMN = "Pace"
    }
}