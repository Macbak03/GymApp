package com.example.gymapp.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.gymapp.R
import com.example.gymapp.animation.FragmentAnimator

class SettingsFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listPreference : ListPreference? = findPreference("theme")
        val feedbackPreference: Preference? = findPreference("feedback")

        feedbackPreference?.setOnPreferenceClickListener {
            // Define your feedback email address
            val feedbackEmailAddress = "janos.macbak@gmail.com"
            // Define the email subject
            val subject = "Feedback for YourApp"
            // Define the default email body text
            val bodyText = """
            Hi there,

            I wanted to share some feedback...

            [Your feedback here]

            Thank you.
        """.trimIndent()

            // Create an intent to open an email client
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // Only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf(feedbackEmailAddress))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, bodyText)
            }

            // Verify that the intent will resolve to an activity
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
            }
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
            else -> requireActivity().setTheme(R.style.Theme_Dark)
        }
        requireActivity().recreate()
    }

}