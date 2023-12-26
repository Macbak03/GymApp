package com.example.gymapp.persistence

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.model.workout.WorkoutSeries

class WorkoutSeriesDataBaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    Repository(
        context, factory,
    )  {
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

    private fun addSeriesToHistory(
        workoutSeries: WorkoutSeries,
        exerciseId: Int
    )
    {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(EXERCISE_ID_COLUMN, exerciseId)
        values.put(SERIES_ORDER_COLUMN, workoutSeries.seriesCount)
        values.put(ACTUAL_REPS_COLUMN, workoutSeries.actualReps)
        values.put(LOAD_VALUE_COLUMN, workoutSeries.load.weight)

        db.insert(TABLE_NAME, null, values)
    }

    fun addSeries(
        series: ArrayList<WorkoutSeries>,
        exerciseId: Int
    ){
        for (ser in series)
        {
            addSeriesToHistory(ser, exerciseId)
        }
    }


    companion object{
        const val TABLE_NAME = "workoutSeries"
        const val EXERCISE_ID_COLUMN= "ExerciseID"
        const val SERIES_ORDER_COLUMN = "SeriesOrder"
        const val ACTUAL_REPS_COLUMN = "ActualReps"
        const val LOAD_VALUE_COLUMN = "LoadValue"
    }
}