package com.pl.Maciejbak.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.pl.Maciejbak.R

class SettingsFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listPreference : ListPreference? = findPreference("theme")
        val feedbackPreference: Preference? = findPreference("feedback")

        feedbackPreference?.setOnPreferenceClickListener {
            val feedbackEmailAddress = "janos.macbak@gmail.com"
            val subject = "Feedback for LiftHub"
            val bodyText = """
            Hi there,

            I wanted to share some feedback...

            [Your feedback here]

            Thank you.
        """.trimIndent()

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(feedbackEmailAddress))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, bodyText)
            }

            val chooserIntent = Intent.createChooser(intent, "Choose app")
            startActivity(chooserIntent)
            true
        }

        listPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                loadTheme()
                true
            }
    }

    private fun loadTheme(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())

        when (sharedPreferences.getString("theme", "")) {
            "Dark" -> requireActivity().setTheme(R.style.Theme_Dark)
            "DarkBlue" -> requireActivity().setTheme(R.style.Theme_DarkBlue)
            "Default" -> requireActivity().setTheme(R.style.Theme_Default)
            else -> requireActivity().setTheme(R.style.Theme_Default)
        }
        requireActivity().recreate()
    }

}