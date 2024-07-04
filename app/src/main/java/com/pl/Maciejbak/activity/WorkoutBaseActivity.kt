package com.pl.Maciejbak.activity

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.pl.Maciejbak.R

open class WorkoutBaseActivity: BaseActivity() {

    protected var routineName: String? = null
    protected var planName: String? = null

    protected var isCorrectlyClosed = false
    protected var isTerminated = true

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        loadTheme()
        super.onCreate(savedInstanceState, persistentState)

    }

    protected fun View.setTimerButtonBackground(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        when (sharedPreferences.getString("theme", "")) {
            "Default" -> setBackgroundResource(R.drawable.clicked_default_button)
            "Dark" -> setBackgroundResource(R.drawable.dark_button_color)
            "DarkBlue" -> setBackgroundResource(R.drawable.button_color)
            else -> setBackgroundResource(R.drawable.clicked_default_button)
        }
    }

    protected fun openTimerActivity(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            showPermissionExplanation()
            if (this.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                val explicitIntent = Intent(applicationContext, TimerActivity::class.java)
                startActivity(explicitIntent)
            }
        } else {
            val explicitIntent = Intent(applicationContext, TimerActivity::class.java)
            startActivity(explicitIntent)
        }
    }

    private fun showPermissionExplanation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!this.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                android.app.AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This function requires the ability to schedule exact alarms to function properly. Please allow this permission in the settings.")
                    .setPositiveButton("Settings") { _, _ ->
                        val intent =
                            Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    protected fun showCancelDialog() {
        val builder = this.let { AlertDialog.Builder(it, R.style.YourAlertDialogTheme) }
        with(builder) {
            this.setTitle("Are you sure you want to cancel this training? It won't be saved.")
            this.setPositiveButton("Yes") { _, _ ->
                setResult(RESULT_OK)
                isCorrectlyClosed = true
                isTerminated = false
                finish()
            }
            this.setNegativeButton("No") { _, _ -> }
            this.show()
        }
    }
}