package com.example.gymapp.model.workout

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CustomDate {
    fun getDate() : String{
        val date = Calendar.getInstance().time
        val timeZone = TimeZone.getDefault()
        val formatter = SimpleDateFormat(rawPattern, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(date)
    }

    fun getFormattedDate(savedDate: String): String {
        val inputFormat = SimpleDateFormat(rawPattern, Locale.getDefault())
        val outputFormat = SimpleDateFormat(pattern, Locale.getDefault())

        val date = inputFormat.parse(savedDate)
        return if (date != null) {
            outputFormat.format(date)
        } else {
            "dateError"
        }
    }

    companion object{
        const val rawPattern = "yyyy-MM-dd hh:mm:ss"
        const val pattern = "dd.MM.yyyy"
    }
}
