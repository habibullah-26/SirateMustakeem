package com.habib.siratemustakeem.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.habib.siratemustakeem.models.Duwa
import java.io.InputStreamReader

object JsonUtils {

    private val gson = Gson()

    /**
     * Generic function to read a list of any model from JSON assets safely in release builds.
     * Pass the Class<T> explicitly to avoid TypeToken issues.
     */
    fun <T> getListFromJson(context: Context, fileName: String, clazz: Class<T>): List<T> {
        val inputStream = context.assets.open(fileName)
        val reader = InputStreamReader(inputStream, Charsets.UTF_8)

        // Explicitly create TypeToken for List<T>
        val listType = TypeToken.getParameterized(List::class.java, clazz).type

        return gson.fromJson(reader, listType)
    }

    /**
     * Specific helper for Duwa model
     */
    fun getListDuwa(context: Context, fileName: String): List<Duwa> {
        return getListFromJson(context, fileName, Duwa::class.java)
    }

    /**
     * Optional: read raw JSON string
     */
    fun getJsonString(context: Context, fileName: String): String {
        return context.assets.open(fileName)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
    }
}