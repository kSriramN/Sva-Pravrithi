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

    /**
     * Cycle-aware version of [yearMonthOf]: if [startDay] is 1, behaves identically to a
     * normal calendar month. For any other start day (e.g. 25, for a salary-cycle month
     * running the 25th to the 24th), a date before [startDay] belongs to the PREVIOUS
     * month's cycle, not the calendar month it falls in. The returned key is still a plain
     * "yyyyMM" string labeled by the month the cycle *starts* in - addMonths()/monthLabel()-
     * style navigation works unchanged on it.
     */
    fun cycleKeyFor(epochMillis: Long, startDay: Int): String {
        if (startDay <= 1) return yearMonthOf(epochMillis)
        val cal = Calendar.getInstance().apply { timeInMillis = epochMillis }
        if (cal.get(Calendar.DAY_OF_MONTH) < startDay) {
            cal.add(Calendar.MONTH, -1)
        }
        return monthKeyFormat.format(cal.time)
    }

    fun currentCycleKey(startDay: Int): String = cycleKeyFor(System.currentTimeMillis(), startDay)

    fun monthLabel(yearMonth: String): String {
        val cal = Calendar.getInstance()
        cal.set(yearMonth.substring(0, 4).toInt(), yearMonth.substring(4, 6).toInt() - 1, 1)
        return monthLabelFormat.format(cal.time)
    }

    /**
     * Cycle-aware display label. With the default startDay=1, identical to [monthLabel]
     * (e.g. "August 2026"). For any other start day, shows the actual date range so the
     * user isn't confused about what "August" means when it doesn't start on the 1st
     * (e.g. "25 Aug - 24 Sep 2026").
     */
    fun cycleLabel(yearMonth: String, startDay: Int): String {
        if (startDay <= 1) return monthLabel(yearMonth)
        val year = yearMonth.substring(0, 4).toInt()
        val month = yearMonth.substring(4, 6).toInt() - 1
        val startCal = Calendar.getInstance().apply {
            set(year, month, startDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = (startCal.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
            add(Calendar.DAY_OF_MONTH, -1)
        }
        val startFmt = SimpleDateFormat("d MMM", Locale.US)
        val endFmt = SimpleDateFormat("d MMM yyyy", Locale.US)
        return "${startFmt.format(startCal.time)} \u2013 ${endFmt.format(endCal.time)}"
    }

    /** Shifts a "yyyyMM" key by [delta] months (negative = earlier, positive = later). */
    fun addMonths(yearMonth: String, delta: Int): String {
        val cal = Calendar.getInstance()
        cal.set(yearMonth.substring(0, 4).toInt(), yearMonth.substring(4, 6).toInt() - 1, 1)
        cal.add(Calendar.MONTH, delta)
        return monthKeyFormat.format(cal.time)
    }

    fun dayLabel(epochMillis: Long): String = dayLabelFormat.format(Calendar.getInstance().apply { timeInMillis = epochMillis }.time)

    fun startOfDay(epochMillis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = epochMillis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
