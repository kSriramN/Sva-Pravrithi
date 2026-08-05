package com.svapravrithi.app.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtil {
    private val monthKeyFormat = SimpleDateFormat("yyyyMM", Locale.US)
    private val monthLabelFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
    private val dayLabelFormat = SimpleDateFormat("d MMM yyyy", Locale.US)

    fun currentYearMonth(): String = monthKeyFormat.format(Calendar.getInstance().time)

    fun yearMonthOf(epochMillis: Long): String = monthKeyFormat.format(Calendar.getInstance().apply { timeInMillis = epochMillis }.time)

    fun monthLabel(yearMonth: String): String {
        val cal = Calendar.getInstance()
        cal.set(yearMonth.substring(0, 4).toInt(), yearMonth.substring(4, 6).toInt() - 1, 1)
        return monthLabelFormat.format(cal.time)
    }

    fun dayLabel(epochMillis: Long): String = dayLabelFormat.format(Calendar.getInstance().apply { timeInMillis = epochMillis }.time)

    fun startOfDay(epochMillis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = epochMillis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
