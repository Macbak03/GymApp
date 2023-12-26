package com.example.gymapp.model.workout

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CustomDate {
    fun getDate() : String{
        val date = Calendar.getInstance().time
        val timeZone = TimeZone.getDefault()
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(date)
    }
    companion object{
        const val pattern = "dd.MM.yyyy hh:mm:ss"
    }
}
