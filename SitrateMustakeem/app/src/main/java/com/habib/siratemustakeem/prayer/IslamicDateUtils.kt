package com.habib.siratemustakeem.prayer

import android.icu.util.Calendar
import android.icu.util.ULocale
import android.os.Build
import com.habib.siratemustakeem.utils.QuranTypographyUtils
import java.text.SimpleDateFormat
import java.util.Locale

object IslamicDateUtils {

    private val hijriMonthNamesUrdu = arrayOf(
        "محرم", "صفر", "ربیع الاول", "ربیع الثانی", "جمادی الاول", "جمادی الثانی",
        "رجب", "شعبان", "رمضان", "شوال", "ذوالقعدہ", "ذوالحجہ"
    )

    /** e.g. "٥ رجب ١٤٤٧ھ" — uses the device's Umm al-Qura calendar (API 24+). */
    fun getHijriDateUrdu(): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return ""
        return try {
            val cal = Calendar.getInstance(ULocale.forLanguageTag("en-u-ca-islamic-umalqura"))
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)
            val monthName = hijriMonthNamesUrdu.getOrElse(month) { "" }
            "${QuranTypographyUtils.toArabicIndicDigits(day)} $monthName ${QuranTypographyUtils.toArabicIndicDigits(year)}ھ"
        } catch (e: Exception) {
            ""
        }
    }

    /** e.g. "23 جولائی 2026" */
    fun getGregorianDateUrdu(): String {
        val format = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)
        return format.format(java.util.Date())
    }
}
