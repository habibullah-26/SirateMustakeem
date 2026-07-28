package com.habib.siratemustakeem.api

import com.habib.siratemustakeem.models.Prayer
import com.habib.siratemustakeem.models.PrayerTimes

object PrayerTimeApi {
    fun getPrayerTimes(callback: (PrayerTimes) -> Unit) {
        // Placeholder for API integration
        val prayerTimes = PrayerTimes(
            listOf(
                Prayer("Fajr", "5:00 AM"),
                Prayer("Dhuhr", "12:30 PM"),
                Prayer("Asr", "4:15 PM"),
                Prayer("Maghrib", "6:45 PM"),
                Prayer("Isha", "8:00 PM")
            )
        )
        callback(prayerTimes)
    }
}