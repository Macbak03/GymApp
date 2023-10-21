package com.example.gymapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.gymapp.model.Routine
import java.time.LocalDate

class DataBaseHelper (context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + DATE_COLUMN + " TEXT, " +
                ROUTINE_NAME_COLUMN + " TEXT," +
                EXERCISE_NAME_COLUMN + " TEXT" +
                PAUSE_COLUMN + " TEXT," +
                LOAD_COLUMN + " TEXT" +
                REPS_COLUMN + " TEXT" +
                SERIES_COLUMN + " TEXT" +
                RPE_COLUMN + " TEXT" +
                PACE_COLUMN + " TEXT" + ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // This method is for adding data in our database
    fun addRoutineToDB(routineText: ArrayList<String>?, routineName: String){

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        values.put(DATE_COLUMN, LocalDate.now().toString())
        values.put(ROUTINE_NAME_COLUMN, routineName)
        values.put(EXERCISE_NAME_COLUMN, routineText!![0])
        values.put(PAUSE_COLUMN, routineText[1])
        values.put(LOAD_COLUMN, routineText[2])
        values.put(REPS_COLUMN, routineText[3])
        values.put(SERIES_COLUMN, routineText[4])
        values.put(RPE_COLUMN, routineText[5])
        values.put(PACE_COLUMN, routineText[6])

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)

        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    fun getRoutineFromDB(): Cursor? {

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)

    }

    companion object{
        private const val DATABASE_NAME = "GymApp"
        private val DATABASE_VERSION = 1

        const val TABLE_NAME = "routine"
        const val DATE_COLUMN = "Date"
        const val ROUTINE_NAME_COLUMN = "Routine name"
        const val EXERCISE_NAME_COLUMN = "Exercise name"
        const val PAUSE_COLUMN = "Pause"
        const val LOAD_COLUMN = "Load"
        const val REPS_COLUMN = "Reps"
        const val SERIES_COLUMN = "Series"
        const val RPE_COLUMN = "RPE"
        const val PACE_COLUMN = "Pace"
    }
}