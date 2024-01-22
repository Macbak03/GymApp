package com.example.gymapp.persistence

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.adapter.WorkoutExpandableListAdapter
import com.example.gymapp.model.routine.ExactPause
import com.example.gymapp.model.routine.ExactReps
import com.example.gymapp.model.routine.ExactRpe
import com.example.gymapp.model.routine.RangePause
import com.example.gymapp.model.routine.RangeReps
import com.example.gymapp.model.routine.RangeRpe
import com.example.gymapp.model.routine.TimeUnit
import com.example.gymapp.model.workout.WorkoutExercise
import com.example.gymapp.model.workout.WorkoutExerciseDraft
import com.example.gymapp.model.workout.WorkoutSeries

class WorkoutHistoryDatabaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?
) : Repository(
        context, factory,
    ) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + DATE_COLUMN + " DATETIME NOT NULL," +
                EXERCISE_ID_COLUMN + " INTEGER PRIMARY KEY," +
                PLAN_NAME_COLUMN + " TEXT NOT NULL," +
                ROUTINE_NAME_COLUMN + " TEXT NOT NULL," +
                EXERCISE_ORDER_COLUMN + " INTEGER NOT NULL," +
                EXERCISE_NAME_COLUMN + " TEXT NOT NULL," +
                PAUSE_RANGE_FROM_COLUMN + " INTEGER NOT NULL," +
                PAUSE_RANGE_TO_COLUMN + " INTEGER NOT NULL," +
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
        values.put(EXERCISE_NAME_COLUMN, workoutExercise.exercise.name)
        when(workoutExercise.exercise.pause) {
            is ExactPause -> {
                values.put(PAUSE_RANGE_FROM_COLUMN, (workoutExercise.exercise.pause as ExactPause).value)
                values.put(PAUSE_RANGE_TO_COLUMN, (workoutExercise.exercise.pause as ExactPause).value)
            }
            is RangePause -> {
                values.put(
                    PAUSE_RANGE_FROM_COLUMN,
                    (workoutExercise.exercise.pause as RangePause).from
                )
                values.put(PAUSE_RANGE_TO_COLUMN, (workoutExercise.exercise.pause as RangePause).to)
            }
        }
        values.put(LOAD_UNIT_COLUMN, workoutExercise.exercise.load.unit.toString())
        when (workoutExercise.exercise.reps) {
            is ExactReps -> {
                values.put(
                    REPS_RANGE_FROM_COLUMN,
                    (workoutExercise.exercise.reps as ExactReps).value
                )
                values.put(REPS_RANGE_TO_COLUMN, (workoutExercise.exercise.reps as ExactReps).value)
            }

            is RangeReps -> {
                values.put(
                    REPS_RANGE_FROM_COLUMN,
                    (workoutExercise.exercise.reps as RangeReps).from
                )
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

    private inline fun SQLiteDatabase.transaction(func: SQLiteDatabase.() -> Unit) {
        beginTransaction()
        try {
            func()
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }

    private fun addSeriesToHistory(
        workoutSeries: WorkoutSeries,
        exerciseId: Int
    ) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(WorkoutSeriesDataBaseHelper.EXERCISE_ID_COLUMN, exerciseId)
        values.put(WorkoutSeriesDataBaseHelper.SERIES_ORDER_COLUMN, workoutSeries.seriesCount)
        values.put(WorkoutSeriesDataBaseHelper.ACTUAL_REPS_COLUMN, workoutSeries.actualReps)
        values.put(WorkoutSeriesDataBaseHelper.LOAD_VALUE_COLUMN, workoutSeries.load.weight)

        db.insert(WorkoutSeriesDataBaseHelper.TABLE_NAME, null, values)
    }

    private fun addSeries(
        series: ArrayList<WorkoutSeries>,
        exerciseId: Int
    ) {
        for (ser in series) {
            addSeriesToHistory(ser, exerciseId)
        }
    }

    fun addExercises(
        workoutExpandableListAdapter: WorkoutExpandableListAdapter,
        date: String,
        planName: String,
        routineName: String
    ) {
        val db = this.writableDatabase
        db.transaction {
            val workout = workoutExpandableListAdapter.getWorkoutGroup()
            for (workoutExercise in workout) {
                addExerciseToHistory(
                    date,
                    workoutExercise,
                    planName,
                    routineName,
                )
                val series =
                    workoutExpandableListAdapter.getWorkoutSeries(workoutExercise.exerciseCount - 1)
                val id = getLastID()
                if (id != null) {
                    addSeries(series, id)

                }
            }
        }
    }

    fun getHistory(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT DISTINCT $PLAN_NAME_COLUMN, $DATE_COLUMN, $ROUTINE_NAME_COLUMN FROM $TABLE_NAME ORDER BY $DATE_COLUMN DESC",
            null
        )
    }

    fun getExerciseID(date: String, exerciseName: String): Int? {
        val selectionArgs = arrayOf(date, exerciseName)
        val selectBy = arrayOf(DATE_COLUMN, EXERCISE_NAME_COLUMN)
        return this.getValue(
            TABLE_NAME,
            EXERCISE_ID_COLUMN,
            selectBy, selectionArgs
        )?.toInt()
    }

    private fun getLastID(): Int? {
        val dataBaseRead = this.readableDatabase
        var id: Int? = null

        val query =
            "SELECT $EXERCISE_ID_COLUMN FROM $TABLE_NAME ORDER BY $EXERCISE_ID_COLUMN DESC LIMIT 1"
        val cursor: Cursor = dataBaseRead.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(EXERCISE_ID_COLUMN))
        }

        cursor.close()
        return id
    }

    private fun getWorkoutExercisesCursor(
        rawDate: String,
        routineName: String,
        planName: String
    ): Cursor {
        val dataBaseRead = this.readableDatabase
        val select = arrayOf(
            EXERCISE_NAME_COLUMN,
            PAUSE_RANGE_FROM_COLUMN,
            PAUSE_RANGE_TO_COLUMN,
            LOAD_UNIT_COLUMN,
            REPS_RANGE_FROM_COLUMN,
            REPS_RANGE_TO_COLUMN,
            SERIES_COLUMN,
            RPE_RANGE_FROM_COLUMN,
            RPE_RANGE_TO_COLUMN,
            PACE_COLUMN,
            NOTES_COLUMN
        )
        val selection = "$DATE_COLUMN = ? AND $PLAN_NAME_COLUMN = ? AND $ROUTINE_NAME_COLUMN = ?"

        val selectionArgs = arrayOf(rawDate, planName, routineName)
        val sortOrder = "$EXERCISE_ORDER_COLUMN ASC"
        return dataBaseRead.query(
            TABLE_NAME,
            select,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }

    fun getWorkoutExercises(
        rawDate: String,
        routineName: String,
        planName: String
    ): List<WorkoutExerciseDraft> {
        val workoutExercises: MutableList<WorkoutExerciseDraft> = ArrayList()
        val cursor = getWorkoutExercisesCursor(rawDate, routineName, planName)
        val seconds = 60
        if (cursor.moveToFirst()) {
            val exerciseName =
                cursor.getString(cursor.getColumnIndexOrThrow(EXERCISE_NAME_COLUMN))

            var pauseRangeFromInt =
                cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.PAUSE_RANGE_FROM_COLUMN))
            var pauseRangeToInt =
                cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.PAUSE_RANGE_TO_COLUMN))
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

            val rpeRangeFrom =
                cursor.getInt(cursor.getColumnIndexOrThrow(RPE_RANGE_FROM_COLUMN))
            val rpeRangeTo =
                cursor.getInt(cursor.getColumnIndexOrThrow(RPE_RANGE_TO_COLUMN))
            val rpe: String = if (rpeRangeFrom == rpeRangeTo) {
                ExactReps(rpeRangeFrom).toString()
            } else {
                RangeReps(rpeRangeFrom, rpeRangeTo).toString()
            }

            val pace =
                cursor.getString(cursor.getColumnIndexOrThrow(PACE_COLUMN))
            val note = cursor.getString(cursor.getColumnIndexOrThrow(NOTES_COLUMN))

            val workoutExercise =
                WorkoutExerciseDraft(exerciseName, pause, pauseUnit, reps, series, rpe, pace, note)
            workoutExercises.add(workoutExercise)

            while (cursor.moveToNext()) {
                val nextExerciseName =
                    cursor.getString(cursor.getColumnIndexOrThrow(EXERCISE_NAME_COLUMN))

                var nextPauseRangeFromInt =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.PAUSE_RANGE_FROM_COLUMN))
                var nextPauseRangeToInt =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ExercisesDataBaseHelper.PAUSE_RANGE_TO_COLUMN))
                val nextPauseUnit: TimeUnit
                if ((nextPauseRangeFromInt % seconds) == 0 && (nextPauseRangeToInt % seconds) == 0) {
                    nextPauseRangeFromInt /= seconds
                    nextPauseRangeToInt /= seconds
                    nextPauseUnit = TimeUnit.min
                } else {
                    nextPauseUnit = TimeUnit.s
                }
                val nextPause: String = if (nextPauseRangeFromInt == nextPauseRangeToInt) {
                    ExactPause(nextPauseRangeFromInt, nextPauseUnit).toString()
                } else {
                    RangePause(nextPauseRangeFromInt, nextPauseRangeToInt, nextPauseUnit).toString()
                }

                val nextRepsRangeFrom =
                    cursor.getInt(cursor.getColumnIndexOrThrow(REPS_RANGE_FROM_COLUMN))
                val nextRepsRangeTo =
                    cursor.getInt(cursor.getColumnIndexOrThrow(REPS_RANGE_TO_COLUMN))
                val nextReps: String = if (nextRepsRangeFrom == nextRepsRangeTo) {
                    ExactReps(nextRepsRangeFrom).toString()
                } else {
                    RangeReps(nextRepsRangeFrom, nextRepsRangeTo).toString()
                }

                val nextSeries =
                    cursor.getString(cursor.getColumnIndexOrThrow(SERIES_COLUMN))

                val nextRpeRangeFrom =
                    cursor.getInt(cursor.getColumnIndexOrThrow(RPE_RANGE_FROM_COLUMN))
                val nextRpeRangeTo =
                    cursor.getInt(cursor.getColumnIndexOrThrow(RPE_RANGE_TO_COLUMN))
                val nextRpe: String = if (nextRpeRangeFrom == nextRpeRangeTo) {
                    ExactReps(nextRpeRangeFrom).toString()
                } else {
                    RangeReps(nextRpeRangeFrom, nextRpeRangeTo).toString()
                }

                val nextPace =
                    cursor.getString(cursor.getColumnIndexOrThrow(PACE_COLUMN))
                val nextNote = cursor.getString(cursor.getColumnIndexOrThrow(NOTES_COLUMN))

                val nextWorkoutExercise =
                    WorkoutExerciseDraft(
                        nextExerciseName,
                        nextPause,
                        nextPauseUnit,
                        nextReps,
                        nextSeries,
                        nextRpe,
                        nextPace,
                        nextNote
                    )
                workoutExercises.add(nextWorkoutExercise)
            }
        }
        return workoutExercises
    }

    fun isTableNotEmpty(): Boolean
    {
        val dataBaseRead = this.readableDatabase
        val cursor = dataBaseRead.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        var isEmpty = true
        if(cursor.moveToFirst())
        {
            val count = cursor.getInt(0)
            isEmpty = count >0
        }
        cursor.close()
        return isEmpty
    }

    fun getLastWorkout() : List<String?>
    {
        var planName: String? = null
        var date: String? = null
        var routineName: String? = null
        val dataBaseRead = this.readableDatabase
        val cursor = dataBaseRead.rawQuery("SELECT $PLAN_NAME_COLUMN, $DATE_COLUMN, $ROUTINE_NAME_COLUMN " +
                "FROM $TABLE_NAME " +
                "WHERE $DATE_COLUMN = (" +
                "   SELECT MAX($DATE_COLUMN)" +
                "   FROM $TABLE_NAME)", null)
        if(cursor.moveToFirst())
        {
            planName = cursor.getString(cursor.getColumnIndexOrThrow(PLAN_NAME_COLUMN))
            date = cursor.getString(cursor.getColumnIndexOrThrow(DATE_COLUMN))
            routineName = cursor.getString(cursor.getColumnIndexOrThrow(ROUTINE_NAME_COLUMN))
        }
        cursor.close()
        return listOf(planName, date, routineName)
    }


    companion object {
        const val TABLE_NAME = "workoutHistory"
        const val EXERCISE_ID_COLUMN = "ExerciseID"
        const val DATE_COLUMN = "Date"
        const val PLAN_NAME_COLUMN = "PlanName"
        const val ROUTINE_NAME_COLUMN = "RoutineName"
        const val EXERCISE_ORDER_COLUMN = "ExerciseOrder"
        const val EXERCISE_NAME_COLUMN = "ExerciseName"
        const val PAUSE_RANGE_FROM_COLUMN = "PauseRangeFrom"
        const val PAUSE_RANGE_TO_COLUMN = "PauseRangeTo"
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