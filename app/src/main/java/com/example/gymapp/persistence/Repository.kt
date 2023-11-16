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


    open fun getValue(
        tableName: String?,
        select: String,
        selectBy: String,
        selectName: String
    ): String? {
        val dataBaseRead = this.readableDatabase
        var selection = "Error"
        val cursor: Cursor = dataBaseRead.query(
            tableName, arrayOf(select),
            "$selectBy = '$selectName'", null, null, null, null
        )
        if (cursor.count == 1) {
            cursor.moveToFirst()
            selection = cursor.getString(cursor.getColumnIndexOrThrow(select))
        }
        cursor.close()
        dataBaseRead.close()
        return selection
    }

    fun getFromTable(tableName: String?, columnName: String, selectBy: String?, selectName: String): Cursor
    {
        val dataBaseRead = this.readableDatabase
        return dataBaseRead.rawQuery("SELECT DISTINCT $columnName FROM $tableName WHERE $selectBy = '$selectName'", null)
    }

    fun getRow(tableName: String?, columnName: String): MutableList<String>
    {
        val dataBaseRead = this.readableDatabase
        val selection = ArrayList<String>()
        val cursor: Cursor = dataBaseRead.rawQuery("SELECT $columnName FROM $tableName", null)
        cursor.moveToFirst()
        selection.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)))
        while (cursor.moveToNext())
        {
            selection.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)))
        }
        cursor.close()
        return selection
    }


    fun <T, R> convertList(originalList: MutableList<T>, converter: (T) -> R): MutableList<R>
    {
        return originalList.mapTo(mutableListOf()){converter(it)}
    }

    fun setForeignKeys(switch: String){
        val dataBaseRead = this.readableDatabase
        val query = ("PRAGMA foreign_keys = $switch")
        dataBaseRead.execSQL(query)
    }

        companion object{
            private const val DATABASE_NAME = "GymApp"
            private const val DATABASE_VERSION = 11
        }
}