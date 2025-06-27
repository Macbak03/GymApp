package com.pl.Maciejbak.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.pl.Maciejbak.R


open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        loadTheme()
        super.onCreate(savedInstanceState, persistentState)

    }

    override fun onResume() {
        loadTheme()
        super.onResume()
    }

    protected fun loadTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        when (sharedPreferences.getString("theme", "Default")) {
            "Default" -> setTheme(R.style.Theme_Default)
            "Dark" -> setTheme(R.style.Theme_Dark)
            "DarkBlue" -> setTheme(R.style.Theme_DarkBlue)
            else -> setTheme(R.style.Theme_Default)
        }
    }

}