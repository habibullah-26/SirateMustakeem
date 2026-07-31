package com.habib.siratemustakeem.hadith

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HadithRetrofitClient {
    private const val BASE_URL = "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/"

    val api: HadithApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HadithApiService::class.java)
    }
}
