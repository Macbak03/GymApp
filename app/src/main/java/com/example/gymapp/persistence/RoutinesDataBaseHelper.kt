package com.example.gymapp.persistence

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class RoutinesDataBaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    Repository(
        context, factory,
    ) {
    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + PLAN_ID_COLUMN + " INTEGER NOT NULL," +
                ROUTINE_ID_COLUMN + " INTEGER PRIMARY KEY," +
                ROUTINE_NAME_COLUMN + " TEXT NOT NULL," +
                "FOREIGN KEY " + "(" + PLAN_ID_COLUMN + ")" + " REFERENCES " + PlansDataBaseHelper.TABLE_NAME + "(" + PlansDataBaseHelper.PLAN_ID_COLUMN + ")"
                + "ON UPDATE CASCADE ON DELETE CASCADE" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }


    fun getRoutinesInPlan(planId: Int): Cursor {
        val dataBaseRead = this.readableDatabase
        return dataBaseRead.rawQuery(
            "SELECT $ROUTINE_NAME_COLUMN FROM $TABLE_NAME WHERE $PLAN_ID_COLUMN = '$planId' ORDER BY $ROUTINE_ID_COLUMN",
            null
        )

    }


    companion object {
        const val TABLE_NAME = "routines"
        const val PLAN_ID_COLUMN = "planID"
        const val ROUTINE_ID_COLUMN = "routineID"
        const val ROUTINE_NAME_COLUMN = "routineName"
    }

}