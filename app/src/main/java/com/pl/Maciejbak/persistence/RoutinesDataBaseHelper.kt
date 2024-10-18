package com.pl.Maciejbak.persistence

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.pl.Maciejbak.model.trainingPlans.TrainingPlanElement

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
    fun deleteRoutines(planID: Int, routineNames: List<String>) {
        val db = this.writableDatabase
        for (routineName in routineNames)
        {
            val deleteSelection =
                "$PLAN_ID_COLUMN = ? AND $ROUTINE_NAME_COLUMN = ?"
            val deleteSelectionArgs =
                arrayOf(planID.toString(), routineName)

            val cursor =
                db.query(TABLE_NAME, null, deleteSelection, deleteSelectionArgs, null, null, null)
            cursor.use { cur ->
                if (cur.moveToFirst()) {
                    db.delete(TABLE_NAME, deleteSelection, deleteSelectionArgs)
                }
            }
        }
    }
    private fun getRoutinesInPlanCursor(planId: Int): Cursor {
        val dataBaseRead = this.readableDatabase
        return dataBaseRead.rawQuery(
            "SELECT $ROUTINE_NAME_COLUMN FROM $TABLE_NAME WHERE $PLAN_ID_COLUMN = '$planId' ORDER BY $ROUTINE_ID_COLUMN",
            null
        )
    }

    fun getRoutinesInPlan(planId: Int): MutableList<TrainingPlanElement>
    {
        val routines: MutableList<TrainingPlanElement> = ArrayList()
        val cursor = getRoutinesInPlanCursor(planId)
        if(cursor.moveToFirst())
        {
            routines.add(TrainingPlanElement(cursor.getString(cursor.getColumnIndexOrThrow(ROUTINE_NAME_COLUMN))))
            while (cursor.moveToNext()) {
                routines.add(TrainingPlanElement(cursor.getString(cursor.getColumnIndexOrThrow(ROUTINE_NAME_COLUMN))))
            }
        }
        return routines
    }

    fun isPlanNotEmpty(planId: String): Boolean{
        val dataBaseRead = this.readableDatabase
        val selection = "$PLAN_ID_COLUMN = ?"
        val selectionArgs = arrayOf(planId)

        val cursor = dataBaseRead.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val idFound: Boolean

        try {
            idFound = cursor.moveToFirst()
        } finally {
            cursor.close()
            dataBaseRead.close()
        }

        return idFound
    }


    companion object {
        const val TABLE_NAME = "routines"
        const val PLAN_ID_COLUMN = "planID"
        const val ROUTINE_ID_COLUMN = "routineID"
        const val ROUTINE_NAME_COLUMN = "routineName"
    }


}