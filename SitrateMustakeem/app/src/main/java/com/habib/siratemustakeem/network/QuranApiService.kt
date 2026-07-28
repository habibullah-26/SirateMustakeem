package com.habib.siratemustakeem.network

import com.habib.siratemustakeem.models.JuzResponse
import com.habib.siratemustakeem.models.JuzSingleEditionResponse
import com.habib.siratemustakeem.models.QuranEditionsResponse
import com.habib.siratemustakeem.models.SurahListResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Free public API — https://alquran.cloud/api — no API key required.
 */
interface QuranApiService {

    // All 114 surahs with metadata (Arabic/English names, ayah counts, Meccan/Medinan)
    @GET("surah")
    suspend fun getAllSurahs(): SurahListResponse

    @GET("juz")
    suspend fun getAllJuz(): JuzResponse

    // Full surah with a dynamic, caller-chosen list of editions (e.g. "quran-uthmani,ar.alafasy"
    // for Tilawat, or "quran-uthmani,ur.kanzuliman,ar.alafasy" for Tafseer).
    // encoded = true so Retrofit doesn't percent-encode the commas we need literally.
    @GET("surah/{surahNumber}/editions/{editions}")
    suspend fun getSurahByEditions(
        @Path("surahNumber") surahNumber: Int,
        @Path("editions", encoded = true) editions: String
    ): QuranEditionsResponse

    // Single-edition juz call. The combined /juz/{juz}/editions/a,b,c endpoint is not
    // reliably supported, so we call this once per edition and merge client-side
    // (see QuranReadActivity).
    @GET("juz/{juzNumber}/{edition}")
    suspend fun getJuzByEdition(
        @Path("juzNumber") juzNumber: Int,
        @Path("edition") edition: String
    ): JuzSingleEditionResponse
}
