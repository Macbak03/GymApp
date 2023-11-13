package com.example.gymapp.persistence

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class PlanDataBaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    Repository(
        context, factory,
    ) {
    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + PLAN_ID_COLUMN + " INTEGER PRIMARY KEY," +
                PLAN_NAME_COLUMN + " TEXT NOT NULL" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    fun addPLan(planName: String) {
        val values = ContentValues()
        values.put(PLAN_NAME_COLUMN, planName)

        val db = this.writableDatabase

        db.use {
            db.insert(TABLE_NAME, null, values)
        }

    }

    fun InsertIDToRoutine(id: Int)
    {
        val values = ContentValues()
        values.put(RoutineDataBaseHelper.PLAN_ID_COLUMN, id)

        val db = this.writableDatabase
        db.use {
            db.insert(RoutineDataBaseHelper.TABLE_NAME, null, values)
        }
    }

    fun getPlans(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun getPlanID(planName: String) : Cursor?
    {
        val db = this.readableDatabase
        return  db.rawQuery("SELECT DISTINCT $PLAN_ID_COLUMN FROM $TABLE_NAME WHERE $PLAN_NAME_COLUMN LIKE $planName", null)
    }

    companion object {
        const val TABLE_NAME = "trainingPlans"
        const val PLAN_ID_COLUMN = "planID"
        const val PLAN_NAME_COLUMN = "planName"
    }
}