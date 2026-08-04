package com.habib.siratemustakeem.prayer

import android.content.Context
import java.util.Calendar

data class NextPrayerInfo(
    val nameUrdu: String,
    val timeFormatted: String,
    val countdown: String
)

data class CurrentPrayerInfo(
    val currentNameUrdu: String,
    val currentTimeFormatted: String,
    val nextNameUrdu: String,
    val nextCountdown: String
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

    /**
     * Picks the *current* prayer — the most recent one whose time has already started (i.e. the
     * waqt we're currently in) — along with the next prayer's name and countdown as secondary
     * info. Before today's Fajr, "current" is treated as last night's Isha (its waqt extends
     * until Fajr). After today's Isha, "next" wraps to tomorrow's Fajr.
     */
    fun getCurrentAndNextPrayer(times: PrayerTimesResult): CurrentPrayerInfo {
        val now = Calendar.getInstance()
        val nowDecimal = now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0

        val schedule = listOf(
            "فجر" to times.fajr,
            "ظہر" to times.dhuhr,
            "عصر" to times.asr,
            "مغرب" to times.maghrib,
            "عشاء" to times.isha
        )

        val currentIndex = schedule.indexOfLast { it.second <= nowDecimal }
        val current: Pair<String, Double>
        val next: Pair<String, Double>
        var nextIsTomorrow = false

        when {
            currentIndex == -1 -> {
                // Before today's Fajr — still within last night's Isha waqt.
                current = schedule.last()
                next = schedule.first()
            }
            currentIndex == schedule.lastIndex -> {
                // Current = Isha; the next prayer is tomorrow's Fajr.
                current = schedule[currentIndex]
                next = schedule.first()
                nextIsTomorrow = true
            }
            else -> {
                current = schedule[currentIndex]
                next = schedule[currentIndex + 1]
            }
        }

        val nextAbsolute = if (nextIsTomorrow) next.second + 24.0 else next.second
        val diffHours = (nextAbsolute - nowDecimal).coerceAtLeast(0.0)
        val totalMinutes = (diffHours * 60).toInt()
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        val countdown = if (h > 0) "$h گھنٹے $m منٹ باقی" else "$m منٹ باقی"

        return CurrentPrayerInfo(
            currentNameUrdu = current.first,
            currentTimeFormatted = PrayerTimeCalculator.formatTime(current.second),
            nextNameUrdu = next.first,
            nextCountdown = countdown
        )
    }
}
