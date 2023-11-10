package com.example.gymapp.persistence

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

abstract class Repository (context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context,
        DATABASE_NAME, factory,
        DATABASE_VERSION
    ) {

        companion object{
            private const val DATABASE_NAME = "GymApp"
            private val DATABASE_VERSION = 1
        }
}