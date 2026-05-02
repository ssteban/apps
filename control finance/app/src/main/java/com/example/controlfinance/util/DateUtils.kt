package com.example.controlfinance.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    fun monthRangeMillis(referenceMillis: Long = System.currentTimeMillis()): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply { timeInMillis = referenceMillis }
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val end = calendar.timeInMillis

        return start to end
    }

    fun formatMonthYear(millis: Long): String {
        val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return format.format(Date(millis)).replaceFirstChar { it.uppercase() }
    }

    fun formatDate(millis: Long): String {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(Date(millis))
    }
}
