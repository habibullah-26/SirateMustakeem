package com.habib.siratemustakeem.hadith

import retrofit2.http.GET
import retrofit2.http.Path

data class HadithEditionResponse(
    val hadiths: List<HadithDto>,
    val metadata: HadithMetadata?
)

data class HadithMetadata(
    val name: String?,
    val author: String?
)

data class HadithDto(
    val hadithnumber: Int,
    val arabicnumber: Int? = null,
    val text: String,
    val grades: List<HadithGrade>? = null,
    val reference: HadithReference? = null
)

data class HadithGrade(
    val name: String?,
    val grade: String?
)

data class HadithReference(
    val book: Int? = null,
    val hadith: Int? = null
)

interface HadithApiService {
    // Fetches one section (a natural chapter-sized chunk) of a given edition —
    // small enough to be a lightweight call, unlike fetching an entire book at once.
    @GET("editions/{edition}/sections/{sectionNo}.min.json")
    suspend fun getSection(
        @Path("edition") edition: String,
        @Path("sectionNo") sectionNo: Int
    ): HadithEditionResponse

    @GET("editions/{edition}/{hadithNo}.min.json")
    suspend fun getHadith(
        @Path("edition") edition: String,
        @Path("hadithNo") hadithNo: Int
    ): HadithEditionResponse
}
