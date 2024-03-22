package com.example.gymapp.persistence

import android.app.backup.BackupAgentHelper
import android.app.backup.FileBackupHelper

class DatabaseBackupAgent : BackupAgentHelper(){

    override fun onCreate() {
        val databaseName = Repository.DATABASE_NAME
        val databasePath = applicationContext.getDatabasePath(databaseName).absolutePath
        val helper = FileBackupHelper(this, databasePath)
        addHelper(databaseName, helper)
    }
}