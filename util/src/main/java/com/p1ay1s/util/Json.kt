package com.p1ay1s.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder

fun Any?.toPrettyJson(): String {
    val gson = GsonBuilder().setPrettyPrinting().create()
    return gson.toJson(this)
}

fun Any?.toJson(): String {
    return Gson().toJson(this)
}

inline fun <reified T> String?.toJsonClass(): T? {
    return try {
        Gson().fromJson(this, T::class.java)
    } catch (_: Exception) {
        null
    }
}