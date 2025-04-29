package com.example.parkingsystem.utils

import java.text.SimpleDateFormat
import java.util.*

object DatesHelper {
    fun getTodayDate(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        return formatter.format(calendar.time)
    }

    fun getTomorrowDate(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)

        return formatter.format(calendar.time)
    }
}