package com.habib.siratemustakeem.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Single shared Retrofit instance for all networking in the app.
 * Currently only used for the Quran (recitation) feature, but any future
 * screen that needs an API can add its endpoints to a service interface
 * and reuse `RetrofitClient.retrofit`.
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.alquran.cloud/v1/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val quranApi: QuranApiService by lazy {
        retrofit.create(QuranApiService::class.java)
    }
}
