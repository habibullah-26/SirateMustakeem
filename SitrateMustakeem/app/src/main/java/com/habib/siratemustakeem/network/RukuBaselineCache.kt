package com.habib.siratemustakeem.network

/**
 * AlQuran Cloud's per-ayah `ruku` field is numbered globally across the whole Quran (there are
 * 556 rukus total), not restarted at 1 for each surah — e.g. Surah Al-Mulk's ayahs report ruku
 * values around 292-296, even though Al-Mulk itself only has 2 rukus. This cache fetches each
 * touched surah's own starting ruku number once (cheap, in-memory for the process lifetime) so
 * the UI can show the surah-relative Ruku number that Mushaf readers actually expect.
 */
object RukuBaselineCache {

    private val cache = mutableMapOf<Int, Int>()

    suspend fun getBaseline(surahNumber: Int): Int {
        cache[surahNumber]?.let { return it }
        val response = RetrofitClient.quranApi.getSurahByEditions(surahNumber, "quran-uthmani")
        val baseline = response.data.getOrNull(0)?.ayahs?.firstOrNull()?.ruku ?: 1
        cache[surahNumber] = baseline
        return baseline
    }
}
