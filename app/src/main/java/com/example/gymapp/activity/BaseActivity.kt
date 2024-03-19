package com.example.gymapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.gymapp.R
import com.example.gymapp.persistence.Repository
import java.io.File
import java.io.FileInputStream
import java.io.IOException

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        loadTheme()
        super.onCreate(savedInstanceState, persistentState)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                } else {
                }
                return
            }
        }
    }

    override fun onStop() {
        super.onStop()
        //backupDatabase()
    }

    override fun onResume() {
        loadTheme()
        super.onResume()
    }

    protected fun loadTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        when (sharedPreferences.getString("theme", "")) {
            "Dark" -> setTheme(R.style.Theme_Dark)
            "DarkBlue" -> setTheme(R.style.Theme_DarkBlue)
            else -> setTheme(R.style.Theme_Dark)
        }
    }

    protected fun requirePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }
    }

    protected fun openDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == Activity.RESULT_OK) {
            val directoryUri = data?.data ?: return

            // Persist permission.
            //val takeFlags: Int = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            contentResolver.takePersistableUriPermission(directoryUri, Intent .FLAG_GRANT_WRITE_URI_PERMISSION)

            backupDatabaseToUri(directoryUri)
        }
    }

    private fun backupDatabaseToUri(directoryUri: Uri) {
        val databasePath = applicationContext.getDatabasePath(Repository.DATABASE_NAME).absolutePath
        val databaseFile = File(databasePath)

        val resolver = applicationContext.contentResolver

        // Create a new file in the selected directory.
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${Repository.DATABASE_NAME}_backup.db") // Use a filename for the backup.
            put(MediaStore.MediaColumns.MIME_TYPE, "application/x-sqlite3")
        }

        try {
            val fileUri = resolver.insert(directoryUri, contentValues)
            if (fileUri != null) {
                resolver.openOutputStream(fileUri).use { outputStream ->
                    FileInputStream(databaseFile).use { inputStream ->
                        inputStream.copyTo(outputStream!!)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

  /*  private fun backupDatabase() {
        val databaseFile = File(getDatabasePath(Repository.DATABASE_NAME).path)
        val externalStorage =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val file = File(externalStorage, Repository.DATABASE_NAME)

        try {
            databaseFile.inputStream().use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    protected fun restoreDatabase() {
        val externalStorage =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val externalDatabaseFile = File(externalStorage, Repository.DATABASE_NAME)
        if (externalDatabaseFile.exists()) {
            val internalDbFile = File(getDatabasePath(Repository.DATABASE_NAME).path)

            if (!internalDbFile.exists()) {
                externalDatabaseFile.inputStream().use { input ->
                    internalDbFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }*/

    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
        private const val REQUEST_CODE_OPEN_DIRECTORY = 1
    }
}