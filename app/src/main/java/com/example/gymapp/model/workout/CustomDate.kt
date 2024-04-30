package com.example.gymapp.model.workout

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CustomDate {
    fun getDate() : String{
        val date = Calendar.getInstance().time
        val timeZone = TimeZone.getDefault()
        val formatter = SimpleDateFormat(RAW_PATTERN, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(date)
    }

    fun getFormattedDate(savedDate: String): String {
        val inputFormat = SimpleDateFormat(RAW_PATTERN, Locale.getDefault())
        val outputFormat = SimpleDateFormat(PATTERN, Locale.getDefault())

        val date = inputFormat.parse(savedDate)
        return if (date != null) {
            outputFormat.format(date)
        } else {
            "dateError"
        }
    }

    fun getChartFormattedDate(savedDate: String): String{
        val inputFormat = SimpleDateFormat(RAW_PATTERN, Locale.getDefault())
        val outputFormat = SimpleDateFormat(CHART_PATTERN, Locale.getDefault())

        val date = inputFormat.parse(savedDate)
        return if (date != null) {
            outputFormat.format(date)
        } else {
            "dateError"
        }
    }

    companion object{
        const val RAW_PATTERN = "yyyy-MM-dd HH:mm:ss"
        const val PATTERN = "dd.MM.yyyy"
        const val CHART_PATTERN = "yyyy-MM-dd"
    }
}
