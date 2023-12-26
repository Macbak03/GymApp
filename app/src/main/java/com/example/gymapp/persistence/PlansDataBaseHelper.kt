package com.example.gymapp.persistence

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class PlansDataBaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
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

    fun getPlanId(planName: String): Int? {
        val selectionArgs = arrayOf(planName)
        val selectBy = arrayOf(PLAN_NAME_COLUMN)
        return this.getValue(TABLE_NAME, PLAN_ID_COLUMN, selectBy, selectionArgs)?.toInt()
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
        dataBaseRead.close()
        return isEmpty
    }

    companion object {
        const val TABLE_NAME = "trainingPlans"
        const val PLAN_ID_COLUMN = "planID"
        const val PLAN_NAME_COLUMN = "planName"
    }
}