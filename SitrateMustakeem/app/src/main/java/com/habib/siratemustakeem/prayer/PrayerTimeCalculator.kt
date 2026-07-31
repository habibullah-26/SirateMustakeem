package com.habib.siratemustakeem.prayer

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.*

data class PrayerTimesResult(
    val fajr: Double,
    val sunrise: Double,
    val dhuhr: Double,
    val asr: Double,
    val maghrib: Double,
    val isha: Double
)

/**
 * Kotlin port of the standard sun-position based prayer time calculation, ported directly
 * against the reference PrayTimes.org algorithm (the same one used by praytimes.org and most
 * prayer-time apps) to avoid drifting from it.
 * Default method: Muslim World League (Fajr 18°, Isha 17°, Maghrib = sunset), standard
 * (Shafi/Maliki/Hanbali) Asr with shadow factor 1.
 * Times are returned as decimal hours in the *local* time zone supplied.
 */
object PrayerTimeCalculator {

    private const val FAJR_ANGLE = 18.0
    private const val ISHA_ANGLE = 17.0

    fun calculate(latitude: Double, longitude: Double, timeZoneOffsetHours: Double, date: Calendar): PrayerTimesResult {
        // Julian date at 0h for this calendar day, shifted by longitude so the sun-position
        // calculations below are evaluated at (approximately) the right moment for this location.
        val baseJd = julianDate(date) - longitude / (15.0 * 24.0)

        fun midDay(t: Double): Double {
            val eqt = equationOfTime(baseJd + t)
            return fixHour(12.0 - eqt)
        }

        fun computeTime(angleDeg: Double, t: Double): Double {
            val decl = sunDeclination(baseJd + t)
            val z = midDay(t)
            val beg = -sinDeg(angleDeg) - sinDeg(decl) * sinDeg(latitude)
            val mid = cosDeg(decl) * cosDeg(latitude)
            val v = acosDeg(beg / mid) / 15.0
            return z + (if (angleDeg > 90) -v else v)
        }

        fun computeAsr(shadowFactor: Double, t: Double): Double {
            val decl = sunDeclination(baseJd + t)
            val angle = -acotDeg(shadowFactor + tanDeg(abs(latitude - decl)))
            return computeTime(angle, t)
        }

        // Seed estimates (hours) refined by one pass through the formulas above, exactly as the
        // reference algorithm does (its default numIterations = 1) — using each prayer's own
        // rough time-of-day when evaluating sun position, rather than a single fixed reference
        // moment for the whole day.
        val fajrEst = computeTime(180 - FAJR_ANGLE, 5.0 / 24.0)
        val sunriseEst = computeTime(180 - 0.833, 6.0 / 24.0)
        val dhuhrEst = midDay(12.0 / 24.0)
        val asrEst = computeAsr(1.0, 13.0 / 24.0)
        val sunsetEst = computeTime(0.833, 18.0 / 24.0)
        val ishaEst = computeTime(ISHA_ANGLE, 18.0 / 24.0)

        // Final conversion from apparent solar time to this zone's clock time. This is the step
        // that was previously missing the "- longitude / 15" term — without it, every time here
        // would be off by (timeZoneOffsetHours - longitude/15), which for most real locations is
        // close to the *entire* timezone offset (several hours), not a small correction.
        fun adjust(t: Double) = fixHour(t + timeZoneOffsetHours - longitude / 15.0)

        return PrayerTimesResult(
            fajr = adjust(fajrEst),
            sunrise = adjust(sunriseEst),
            dhuhr = adjust(dhuhrEst),
            asr = adjust(asrEst),
            maghrib = adjust(sunsetEst), // Muslim World League: Maghrib = sunset + 0 minutes
            isha = adjust(ishaEst)
        )
    }

    private fun julianDate(cal: Calendar): Double {
        var year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        if (month <= 2) { year -= 1; month += 12 }
        val a = floor(year / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (year + 4716)) + floor(30.6001 * (month + 1)) + day + b - 1524.5
    }

    private fun sunDeclination(jd: Double): Double {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sinDeg(g) + 0.020 * sinDeg(2 * g))
        val e = 23.439 - 0.00000036 * d
        return asinDeg(sinDeg(e) * sinDeg(l))
    }

    private fun equationOfTime(jd: Double): Double {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sinDeg(g) + 0.020 * sinDeg(2 * g))
        val e = 23.439 - 0.00000036 * d
        var ra = atan2Deg(cosDeg(e) * sinDeg(l), cosDeg(l)) / 15.0
        ra = fixHour(ra)
        return q / 15.0 - ra
    }

    // ---- degree-based trig helpers ----
    private fun sinDeg(d: Double) = sin(Math.toRadians(d))
    private fun cosDeg(d: Double) = cos(Math.toRadians(d))
    private fun tanDeg(d: Double) = tan(Math.toRadians(d))
    private fun acosDeg(d: Double) = Math.toDegrees(acos(d.coerceIn(-1.0, 1.0)))
    private fun asinDeg(d: Double) = Math.toDegrees(asin(d.coerceIn(-1.0, 1.0)))
    private fun acotDeg(d: Double) = Math.toDegrees(atan(1.0 / d))
    private fun atan2Deg(y: Double, x: Double) = Math.toDegrees(atan2(y, x))

    private fun fixAngle(a: Double): Double { var v = a % 360.0; if (v < 0) v += 360.0; return v }
    private fun fixHour(h: Double): Double { var v = h % 24.0; if (v < 0) v += 24.0; return v }

    fun formatTime(decimalHours: Double): String {
        val totalMinutes = (decimalHours * 60).roundToInt()
        val h = (totalMinutes / 60) % 24
        val m = totalMinutes % 60
        val suffix = if (h >= 12) "PM" else "AM"
        val h12 = if (h % 12 == 0) 12 else h % 12
        return String.format("%d:%02d %s", h12, m, suffix)
    }

    /** Returns the local device timezone offset from UTC in hours (including DST), e.g. 5.0 for PKT. */
    fun currentTimeZoneOffsetHours(): Double {
        val tz = TimeZone.getDefault()
        return tz.getOffset(System.currentTimeMillis()) / (1000.0 * 60.0 * 60.0)
    }
}
