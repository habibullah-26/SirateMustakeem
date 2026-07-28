package com.habib.siratemustakeem.utils

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan

object QuranTypographyUtils {

    private val arabicIndicDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    /** Converts a positive Int (e.g. 255) to Arabic-Indic numerals (e.g. ٢٥٥ -> ۲۵۵). */
    fun toArabicIndicDigits(number: Int): String {
        return number.toString().map { ch ->
            if (ch.isDigit()) arabicIndicDigits[ch - '0'] else ch
        }.joinToString("")
    }

    /**
     * The standard Uthmani end-of-ayah marker: U+06DD (ARABIC END OF AYAH) followed by the
     * ayah number in Arabic-Indic digits. Authentic Quran fonts (including PDMS Saleem Quran
     * Font, used in this app) render this combination as the traditional circular/ornate
     * ayah-number seal seen in the Madani and Indo-Pak Mushaf — it is not part of the raw
     * "text" field returned by the API, which only gives the ayah's words.
     */
    fun ayahEndMarker(numberInSurah: Int): String {
        return "\u06DD${toArabicIndicDigits(numberInSurah)}"
    }

    /** Standard Bismillah, recited at the start of every surah except At-Tawbah (9). */
    const val BISMILLAH = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"

    // Diacritics + Quranic annotation marks, stripped only for comparison purposes so a
    // Bismillah match isn't missed over minor tashkeel/rendering differences in the source data.
    private val diacriticsRegex = Regex("[\u064B-\u0652\u0670\u06D6-\u06ED]")

    /**
     * Some editions of the Uthmani text embed the Bismillah as a literal prefix inside ayah 1's
     * own text for every surah except Al-Faatiha and At-Tawbah (Al-Faatiha's ayah 1 *is* the
     * Bismillah; At-Tawbah has none). Since this app already shows Bismillah as its own separate
     * row above the ayah content, that embedded copy needs to be removed from the ayah text
     * itself — otherwise it renders twice in a row.
     */
    fun stripLeadingBismillahIfPresent(ayahText: String): String {
        val normalizedAyah = diacriticsRegex.replace(ayahText, "")
        val normalizedBismillah = diacriticsRegex.replace(BISMILLAH, "")
        if (!normalizedAyah.startsWith(normalizedBismillah)) return ayahText

        var nonDiacriticCount = 0
        var cutIndex = ayahText.length
        for (i in ayahText.indices) {
            if (!diacriticsRegex.matches(ayahText[i].toString())) {
                nonDiacriticCount++
            }
            if (nonDiacriticCount == normalizedBismillah.length) {
                cutIndex = i + 1
                break
            }
        }
        return ayahText.substring(cutIndex).trimStart()
    }

    /**
     * Rumūz al-Awqāf (pause/stop marks: ۖ ۗ ۚ ۛ ۙ etc.) live in the Quranic annotation Unicode
     * block. This app's bundled Quran font does not reliably render that block (the same gap
     * that affected the ayah-end marker), so these characters need to be rendered with a
     * fallback typeface instead of the Quran font to actually be visible.
     */
    fun isWaqfMark(c: Char): Boolean {
        val code = c.code
        return (code in 0x06D6..0x06DC) || (code in 0x06DE..0x06ED) || (code in 0x08D4..0x08FF)
    }

    /**
     * Appends ayah text to a SpannableStringBuilder, giving any waqf/pause marks (رموزالاوقاف)
     * within it a system fallback typeface + accent color instead of the Quran font. The Quran
     * font bundled in this app doesn't render the Quranic annotation Unicode block, so left as
     * plain text those marks are invisible; Android's default font fallback chain does have
     * glyphs for them, and coloring them sets them apart the way printed Mushafs often do.
     */
    fun appendAyahTextWithWaqfStyling(builder: SpannableStringBuilder, text: String, waqfColor: Int) {
        var i = 0
        while (i < text.length) {
            val c = text[i]
            if (isWaqfMark(c)) {
                val start = builder.length
                builder.append(c)
                val end = builder.length
                builder.setSpan(TypefaceSpan("sans-serif"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                builder.setSpan(ForegroundColorSpan(waqfColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                builder.append(c)
            }
            i++
        }
    }
}
