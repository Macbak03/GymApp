package com.pl.Maciejbak.persistence

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.pl.Maciejbak.model.routine.WeightUnit
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft

class WorkoutSeriesDataBaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    Repository(
        context, factory,
    ) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + EXERCISE_ID_COLUMN + " INTEGER NOT NULL," +
                SERIES_ORDER_COLUMN + " INTEGER NOT NULL," +
                ACTUAL_REPS_COLUMN + " REAL NOT NULL," +
                LOAD_VALUE_COLUMN + " REAL NOT NULL," +
                " FOREIGN KEY " + "(" + EXERCISE_ID_COLUMN + ")" + " REFERENCES " + WorkoutHistoryDatabaseHelper.TABLE_NAME + "(" + WorkoutHistoryDatabaseHelper.EXERCISE_ID_COLUMN + ")"
                + "ON UPDATE CASCADE ON DELETE CASCADE" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    private fun getSeriesCursor(exerciseId: Int): Cursor {
        val databaseRead = this.readableDatabase
        val select = arrayOf(ACTUAL_REPS_COLUMN, LOAD_VALUE_COLUMN)
        val selection = "$EXERCISE_ID_COLUMN = ?"
        val selectionArgs = arrayOf(exerciseId.toString())
        val sortOrder = "$SERIES_ORDER_COLUMN ASC"
        return databaseRead.query(
            TABLE_NAME,
            select,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }

    private fun getLoadUnit(exerciseId: Int): WeightUnit? {
        val databaseRead = this.readableDatabase
        var loadUnit: WeightUnit? =  null
        val cursor =
            databaseRead.rawQuery(
                "SELECT ${WorkoutHistoryDatabaseHelper.LOAD_UNIT_COLUMN} FROM ${WorkoutHistoryDatabaseHelper.TABLE_NAME} WHERE ${WorkoutHistoryDatabaseHelper.EXERCISE_ID_COLUMN} = '$exerciseId'",
                null
            )
        if(cursor.moveToFirst())
        {
            loadUnit = WeightUnit.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutHistoryDatabaseHelper.LOAD_UNIT_COLUMN)))
        }
        cursor.close()
        return loadUnit
    }

    fun getSeries(exerciseId: Int): List<WorkoutSeriesDraft> {
        val workoutSeries: MutableList<WorkoutSeriesDraft> = ArrayList()
        val cursor = getSeriesCursor(exerciseId)
        while (cursor.moveToNext()) {
            val actualReps = cursor.getString(cursor.getColumnIndexOrThrow(ACTUAL_REPS_COLUMN))
            val loadValue = cursor.getString(cursor.getColumnIndexOrThrow(LOAD_VALUE_COLUMN))
            val loadUnit = getLoadUnit(exerciseId)
            if(loadUnit != null)
            {
                workoutSeries.add(WorkoutSeriesDraft(actualReps, loadValue, loadUnit,
                    isChecked = false))
            }
        }
        return workoutSeries
    }

    fun updateSeriesValues(exerciseId: Int, setOrder: Int, actualReps: Float, loadValue: Float) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(ACTUAL_REPS_COLUMN, actualReps)
        values.put(LOAD_VALUE_COLUMN, loadValue)

        val selection = "$EXERCISE_ID_COLUMN = ? AND $SERIES_ORDER_COLUMN = ?"
        val selectionArgs = arrayOf(exerciseId.toString(), setOrder.toString())

        db.update(TABLE_NAME, values, selection, selectionArgs)

        db.close()
    }

    fun getChartData(exerciseId: Int): Pair<Float, Float>{
        val dataBaseRead = this.readableDatabase

        var actualReps = 0f
        var loadValue = 0f

        val cursor = dataBaseRead.rawQuery("SELECT $ACTUAL_REPS_COLUMN, $LOAD_VALUE_COLUMN FROM " +
                "$TABLE_NAME WHERE $EXERCISE_ID_COLUMN = ? AND $LOAD_VALUE_COLUMN = (" +
                "SELECT MAX($LOAD_VALUE_COLUMN) FROM $TABLE_NAME WHERE $EXERCISE_ID_COLUMN = ?)",
            arrayOf(exerciseId.toString(), exerciseId.toString()))

        cursor.use { cur ->
            if (cur.moveToFirst()) {
                    actualReps = cur.getFloat(cur.getColumnIndexOrThrow(ACTUAL_REPS_COLUMN))
                    loadValue = cur.getFloat(cur.getColumnIndexOrThrow(LOAD_VALUE_COLUMN))
            }
        }
        return Pair(actualReps, loadValue)
    }




        companion object {
        const val TABLE_NAME = "workoutSeries"
        const val EXERCISE_ID_COLUMN = "ExerciseID"
        const val SERIES_ORDER_COLUMN = "SeriesOrder"
        const val ACTUAL_REPS_COLUMN = "ActualReps"
        const val LOAD_VALUE_COLUMN = "LoadValue"
    }
}