package com.habib.siratemustakeem.prayer

import android.content.Context
import java.util.Calendar

data class NextPrayerInfo(
    val nameUrdu: String,
    val timeFormatted: String,
    val countdown: String
)

data class TodayScheduleResult(
    val times: PrayerTimesResult,
    val location: ResolvedLocation
)

object PrayerScheduleProvider {

    suspend fun getTodayTimes(context: Context): TodayScheduleResult {
        val location = LocationHelper.resolveLocation(context)
        val times = PrayerTimeCalculator.calculate(
            location.latitude,
            location.longitude,
            PrayerTimeCalculator.currentTimeZoneOffsetHours(),
            Calendar.getInstance()
        )
        return TodayScheduleResult(times, location)
    }

    /** Picks the next upcoming prayer from now, rolling over to tomorrow's Fajr if all of today's have passed. */
    fun getNextPrayer(times: PrayerTimesResult): NextPrayerInfo {
        val now = Calendar.getInstance()
        val nowDecimal = now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0

        // Only the 5 obligatory prayers count as "next prayer" (skip sunrise).
        val schedule = listOf(
            "فجر" to times.fajr,
            "ظہر" to times.dhuhr,
            "عصر" to times.asr,
            "مغرب" to times.maghrib,
            "عشاء" to times.isha
        )

        val upcoming = schedule.firstOrNull { it.second > nowDecimal }
        val (name, time) = upcoming ?: (schedule.first().first to (schedule.first().second + 24.0))

        val diffHours = time - nowDecimal
        val totalMinutes = (diffHours * 60).toInt().coerceAtLeast(0)
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        val countdown = if (h > 0) "$h گھنٹے $m منٹ باقی" else "$m منٹ باقی"

        return NextPrayerInfo(name, PrayerTimeCalculator.formatTime(if (time >= 24.0) time - 24.0 else time), countdown)
    }

    /** Returns the current prayer if the current time falls within its range. */
    fun getCurrentPrayer(times: PrayerTimesResult): NextPrayerInfo? {
        val now = Calendar.getInstance()
        val nowDecimal = now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0

        val schedule = listOf(
            "فجر" to times.fajr,
            "ظہر" to times.dhuhr,
            "عصر" to times.asr,
            "مغرب" to times.maghrib,
            "عشاء" to times.isha
        )

        for (i in schedule.indices) {
            val current = schedule[i]
            val next = schedule.getOrNull(i + 1) ?: (schedule.first().first to schedule.first().second + 24.0)

            if (nowDecimal in current.second..next.second) {
                return NextPrayerInfo(
                    nameUrdu = current.first,
                    timeFormatted = String.format("%02d:%02d", current.second.toInt(), ((current.second % 1) * 60).toInt()),
                    countdown = "جاری ہے"
                )
            }
        }
        return null
    }
}
