package com.example.gymapp.persistence


import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


abstract class Repository(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME, factory,
        DATABASE_VERSION
    ) {


    open fun getValue(
        tableName: String?,
        select: String,
        selectBy: Array<String>,
        selectionArgs: Array<String>
    ): String? {
        val dataBaseRead = this.readableDatabase
        var selection = "Error"
        require(selectBy.size == selectionArgs.size) { "Number of columns and values must be the same." }
        val selectionClause = buildSelectionClause(selectBy)
        val cursor: Cursor = dataBaseRead.query(
            tableName,
            arrayOf(select),
            selectionClause,
            selectionArgs,
            null,
            null,
            null
        )
        if (cursor.count == 1) {
            cursor.moveToFirst()
            selection = cursor.getString(cursor.getColumnIndexOrThrow(select))
        }
        cursor.close()
        return selection
    }

    private fun buildSelectionClause(columns: Array<String>): String {
        return columns.joinToString(separator = " = ? AND ", postfix = " = ?")
    }

    fun getColumn(tableName: String?, columnName: String, orderBy: String): MutableList<String> {
        val dataBaseRead = this.readableDatabase
        val selection = ArrayList<String>()
        val cursor: Cursor =
            dataBaseRead.rawQuery("SELECT $columnName FROM $tableName ORDER BY $orderBy", null)
        if (cursor.moveToFirst()) {
            selection.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)))
            while (cursor.moveToNext()) {
                selection.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)))
            }
        }
        cursor.close()
        return selection
    }


    fun <T, R> convertList(originalList: MutableList<T>, converter: (T) -> R): MutableList<R> {
        return originalList.mapTo(mutableListOf()) { converter(it) }
    }

    fun setForeignKeys(switch: String) {
        val dataBaseWrite = this.writableDatabase
        val query = ("PRAGMA foreign_keys = $switch")
        dataBaseWrite.execSQL(query)
    }


    companion object {
        private const val DATABASE_NAME = "GymApp"
        private const val DATABASE_VERSION = 30
    }
}