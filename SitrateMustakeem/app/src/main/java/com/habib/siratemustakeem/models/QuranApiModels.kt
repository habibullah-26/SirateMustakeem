package com.habib.siratemustakeem.models

import java.io.Serializable

// ---------- List of all 114 Surahs (GET /v1/surah) ----------
data class SurahListResponse(
    val code: Int,
    val status: String,
    val data: List<SurahMeta>
)

data class SurahMeta(
    val number: Int,
    val name: String,                    // Arabic name, e.g. "سورة الفاتحة"
    val englishName: String,             // e.g. "Al-Faatiha"
    val englishNameTranslation: String,   // e.g. "The Opening"
    val numberOfAyahs: Int,
    val revelationType: String           // "Meccan" / "Medinan"
) : Serializable

// ---------- Surah / Juz detail with Arabic + Urdu + Audio editions ----------
data class QuranEditionsResponse(
    val code: Int,
    val status: String,
    val data: List<EditionData>
)

data class EditionData(
    val ayahs: List<AyahDto>,
    val edition: EditionInfo
)

data class EditionInfo(
    val identifier: String,   // "quran-uthmani" | "ur.jalandhry" | "ar.alafasy"
    val language: String,
    val type: String
)

data class AyahDto(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val juz: Int? = null,        // which Para this ayah belongs to
    val ruku: Int? = null,       // which Ruku (within the surah) this ayah belongs to
    val audio: String? = null,   // present only in the audio edition
    val surah: SurahRef? = null  // present only when fetching by Juz (mixes surahs)
)

data class SurahRef(
    val number: Int,
    val name: String,
    val englishName: String
)

// ---------- Single-edition Juz response (GET /v1/juz/{juz}/{edition}) ----------
// Used because the combined /juz/{juz}/editions/a,b,c endpoint is not
// reliably supported the way it is for /surah/ and /ayah/. We instead call
// this once per edition (Arabic, Urdu, Audio) and merge client-side.
data class JuzSingleEditionResponse(
    val code: Int,
    val status: String,
    val data: JuzData
)

data class JuzData(
    val number: Int,
    val ayahs: List<AyahDto>
)

// ---------- Single-edition Juz response (GET /v1/juz/{juz}/{edition}) ----------
// Used because the combined /juz/{juz}/editions/a,b,c endpoint is not
// reliably supported the way it is for /surah/ and /ayah/. We instead call
// this once per edition (Arabic, Urdu, Audio) and merge client-side.
data class JuzResponse(
    val code: Int,
    val status: String,
    val data: List<Juz>
)

data class Juz(
    val number: Int,
    val arabicName: String,
    val englishName: String,
    val urduName: String,
    val firstSurah: Int,
    val firstAyah: Int
)

// ---------- Combined model used to bind each row in the reading screen ----------
data class AyahDisplay(
    val numberInSurah: Int,
    val surahNumber: Int = 0,
    val surahName: String,
    val arabicText: String,
    val urduText: String,
    val audioUrl: String?,
    val juz: Int? = null,
    val ruku: Int? = null
) : Serializable
