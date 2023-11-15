package com.example.gymapp.persistence


import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper



abstract class Repository (context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context,
        DATABASE_NAME, factory,
        DATABASE_VERSION
    ) {

    open fun getFromDb(
        tableName: String?,
        select: String,
        selectBy: String,
        selectName: String
    ): String? {
        val db = this.readableDatabase
        var selection = "Error"
        val cursor: Cursor = db.query(
            tableName, arrayOf(select),
            "$selectBy = '$selectName'", null, null, null, null
        )
        if (cursor.count == 1) {
            cursor.moveToFirst()
            selection = cursor.getString(cursor.getColumnIndexOrThrow(select))
        }
        cursor.close()
        db.close()
        return selection
    }

    fun setForeignKeys(switch: String){
        val db = this.readableDatabase
        val query = ("PRAGMA foreign_keys = $switch")
        db.execSQL(query)
    }

        companion object{
            private const val DATABASE_NAME = "GymApp"
            private const val DATABASE_VERSION = 11
        }
}